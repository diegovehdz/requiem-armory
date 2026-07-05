package io.github.diegovehdz.requiemarmory.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.ranged.BowWeaponItem;
import io.github.diegovehdz.requiemarmory.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * The arrow-pull compat feature: while a bow is drawn, the nocked arrow is drawn on top of the bow's
 * pull sprite. <b>Compat contract</b> (no code dependency, no API): a mod makes its arrow show up by
 * shipping a model at {@code <namespace>:models/item/arrow_pull/<arrow_path>.json} (an
 * {@code item/generated} pointing at its own overlay texture). We ship overlays for the vanilla
 * arrow / spectral arrow / tipped arrow; anything else falls back to the generic arrow overlay.
 *
 * <p>Referenced Archery Expansion's convention idea (a per-arrow overlay resolved by name) but the
 * implementation here is original: we discover overlay models by resource enumeration, then wrap each
 * bow's <em>base</em> baked model so its {@link ItemOverrides#resolve} composites the overlay onto
 * whichever pull stage vanilla picked.</p>
 *
 * <p>Note: for a clean result the bow's {@code _pulling} sprites should be <em>arrow-less</em> (bow
 * only); the current Archeries placeholder sprites already include an arrow, so the overlay will
 * double up until arrow-less pull art exists. The pipeline is correct regardless.</p>
 */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID, value = Dist.CLIENT)
public final class ArrowPullOverlay {
    private ArrowPullOverlay() {}

    private static final String MODEL_DIR = "models/item/arrow_pull";
    private static final String MODEL_PREFIX = "item/arrow_pull/";
    /** Generic overlay used when an arrow has no specific one (its item is minecraft:arrow). */
    private static final ResourceLocation FALLBACK_ARROW = ResourceLocation.withDefaultNamespace("arrow");

    /** arrow item id → its overlay model, discovered from resources. */
    private static final Map<ResourceLocation, ModelResourceLocation> OVERLAYS = new HashMap<>();

    // ------------------------------------------------------------------ registration

    @SubscribeEvent
    static void registerOverlayModels(ModelEvent.RegisterAdditional event) {
        OVERLAYS.clear();
        Minecraft.getInstance().getResourceManager()
                .listResources(MODEL_DIR, rl -> rl.getPath().endsWith(".json"))
                .keySet()
                .forEach(rl -> {
                    String path = rl.getPath(); // models/item/arrow_pull/<arrow>.json
                    String arrow = path.substring((MODEL_DIR + "/").length(), path.length() - ".json".length());
                    ResourceLocation arrowItem = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), arrow);
                    ModelResourceLocation model = new ModelResourceLocation(
                            ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), MODEL_PREFIX + arrow),
                            ModelResourceLocation.STANDALONE_VARIANT);
                    event.register(model);
                    OVERLAYS.put(arrowItem, model);
                });
    }

    @SubscribeEvent
    static void wrapBowModels(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        List<ResourceLocation> bows = new ArrayList<>();
        bows.add(BuiltInRegistries.ITEM.getKey(Items.BOW)); // wooden tier
        ModItems.RANGED.values().forEach(holder -> {
            if (holder.get() instanceof BowWeaponItem) { // covers bows + longbows
                bows.add(BuiltInRegistries.ITEM.getKey(holder.get()));
            }
        });
        for (ResourceLocation id : bows) {
            ModelResourceLocation mrl = ModelResourceLocation.inventory(id);
            BakedModel base = models.get(mrl);
            if (base != null) {
                models.put(mrl, new BowBaseModel(base));
            }
        }
    }

    // ------------------------------------------------------------------ resolution

    /** The baked overlay model for a nocked arrow, or {@code null} if none applies. */
    private static BakedModel overlayFor(ItemStack arrow) {
        if (arrow.isEmpty()) {
            return null;
        }
        ModelResourceLocation mrl = OVERLAYS.get(BuiltInRegistries.ITEM.getKey(arrow.getItem()));
        if (mrl == null) {
            mrl = OVERLAYS.get(FALLBACK_ARROW);
        }
        if (mrl == null) {
            return null;
        }
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(mrl);
        return model == Minecraft.getInstance().getModelManager().getMissingModel() ? null : model;
    }

    // ------------------------------------------------------------------ model wrappers

    /** Wraps a bow's base model so its overrides composite the nocked-arrow overlay. */
    private static final class BowBaseModel extends BakedModelWrapper<BakedModel> {
        private final ItemOverrides overrides;

        BowBaseModel(BakedModel base) {
            super(base);
            this.overrides = new BowOverrides(base.getOverrides());
        }

        @Override
        public ItemOverrides getOverrides() {
            return overrides;
        }
    }

    /** Runs the bow's normal override resolution, then layers the arrow overlay while drawing. */
    private static final class BowOverrides extends ItemOverrides {
        private final ItemOverrides original;
        private final Map<BakedModel, Map<BakedModel, BakedModel>> cache = new HashMap<>();

        BowOverrides(ItemOverrides original) {
            this.original = original;
        }

        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
            BakedModel picked = original.resolve(model, stack, level, entity, seed);
            if (picked == null || !(entity instanceof Player player)
                    || !player.isUsingItem() || player.getUseItem() != stack) {
                return picked;
            }
            BakedModel overlay = overlayFor(player.getProjectile(stack));
            if (overlay == null) {
                return picked;
            }
            return cache.computeIfAbsent(picked, k -> new HashMap<>())
                    .computeIfAbsent(overlay, o -> new CompositeModel(picked, o));
        }
    }

    /** A bow pull model with a second set of quads (the arrow overlay) drawn on top. */
    private static final class CompositeModel extends BakedModelWrapper<BakedModel> {
        private final BakedModel overlay;

        CompositeModel(BakedModel base, BakedModel overlay) {
            super(base);
            this.overlay = overlay;
        }

        private List<BakedQuad> combine(List<BakedQuad> base, List<BakedQuad> add) {
            if (add.isEmpty()) {
                return base;
            }
            List<BakedQuad> all = new ArrayList<>(base.size() + add.size());
            all.addAll(base);
            all.addAll(add);
            return all;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
            return combine(super.getQuads(state, side, rand), overlay.getQuads(state, side, rand));
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
            return combine(super.getQuads(state, side, rand, data, renderType),
                    overlay.getQuads(state, side, rand, data, renderType));
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) {
            return List.of(this); // render our combined quads, not the wrapped model's
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY; // already fully resolved
        }
    }
}
