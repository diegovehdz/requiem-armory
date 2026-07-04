package io.github.diegovehdz.requiemarmory.client;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.entity.ModEntities;
import io.github.diegovehdz.requiemarmory.ranged.BowWeaponItem;
import io.github.diegovehdz.requiemarmory.ranged.CrossbowWeaponItem;
import io.github.diegovehdz.requiemarmory.ranged.RangedStats;
import io.github.diegovehdz.requiemarmory.ranged.RangedTier;
import io.github.diegovehdz.requiemarmory.ranged.RangedType;
import io.github.diegovehdz.requiemarmory.registry.ModItems;
import io.github.diegovehdz.requiemarmory.weapon.ThrowableWeaponItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Client-only setup: entity renderers and the throwables' {@code throwing} model property. */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID, value = Dist.CLIENT)
public final class RequiemArmoryClient {
    private RequiemArmoryClient() {}

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THROWN_WEAPON.get(), ThrownWeaponRenderer::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ResourceLocation throwing = ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, "throwing");
        ResourceLocation pulling = ResourceLocation.withDefaultNamespace("pulling");
        ResourceLocation pull = ResourceLocation.withDefaultNamespace("pull");
        event.enqueueWork(() -> {
            ModItems.WEAPONS.values().forEach(holder -> {
                if (holder.get() instanceof ThrowableWeaponItem item) {
                    // 1.0 while the player is charging a throw, so the model can switch to a throwing pose.
                    ItemProperties.register(item, throwing, (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
                }
            });
            ResourceLocation charged = ResourceLocation.withDefaultNamespace("charged");
            ResourceLocation firework = ResourceLocation.withDefaultNamespace("firework");
            ModItems.RANGED.values().forEach(holder -> {
                if (holder.get() instanceof BowWeaponItem bow) {
                    // Same predicates the vanilla bow uses, but the pull fraction is scaled to this
                    // tier's draw speed so the animation finishes exactly when the shot is full.
                    registerBowPull(bow, pulling, pull, bow.stats().drawTicks());
                } else if (holder.get() instanceof CrossbowWeaponItem crossbow) {
                    registerCrossbowPredicates(crossbow, pulling, pull, charged, firework);
                }
            });

            // The vanilla bow is now the wooden tier: match its faster draw animation server-side.
            // (The vanilla crossbow's charge animation follows getChargeDuration, which the mixin
            // already retunes, so it needs no client-side re-registration.)
            registerBowPull(Items.BOW, pulling, pull, RangedStats.of(RangedType.BOW, RangedTier.WOODEN).drawTicks());
        });
    }

    /** The four crossbow model predicates ({@code pull}/{@code pulling}/{@code charged}/{@code firework}). */
    private static void registerCrossbowPredicates(CrossbowWeaponItem crossbow, ResourceLocation pulling,
                                                   ResourceLocation pull, ResourceLocation charged, ResourceLocation firework) {
        ItemProperties.register(crossbow, pull, (stack, level, entity, seed) -> {
            if (entity == null || CrossbowItem.isCharged(stack)) {
                return 0.0f;
            }
            return (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks())
                    / (float) crossbow.chargeDurationTicks(stack, entity);
        });
        ItemProperties.register(crossbow, pulling, (stack, level, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack
                        && !CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
        ItemProperties.register(crossbow, charged, (stack, level, entity, seed) ->
                CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
        ItemProperties.register(crossbow, firework, (stack, level, entity, seed) -> {
            ChargedProjectiles cp = stack.get(DataComponents.CHARGED_PROJECTILES);
            return cp != null && cp.contains(Items.FIREWORK_ROCKET) ? 1.0f : 0.0f;
        });
    }

    /** Registers the {@code pulling}/{@code pull} model predicates for a bow with a given draw time. */
    private static void registerBowPull(net.minecraft.world.item.Item bow, ResourceLocation pulling,
                                        ResourceLocation pull, int drawTicks) {
        ItemProperties.register(bow, pulling, (stack, level, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
        ItemProperties.register(bow, pull, (stack, level, entity, seed) -> {
            if (entity == null || entity.getUseItem() != stack) {
                return 0.0f;
            }
            return (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / (float) drawTicks;
        });
    }
}
