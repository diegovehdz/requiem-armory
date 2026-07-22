package io.github.diegovehdz.requiemarmory.weapon;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

/**
 * The material a weapon is made of. Durability, enchantability, mining speed and the base
 * attack-damage bonus all come from the wrapped {@link Tier}, so a higher material hits harder and
 * lasts longer automatically.
 *
 * <p>This is an <b>open registry, not an enum</b>: other mods can add their own materials by calling
 * {@link #register} from their mod constructor. Every constructor runs before the first registry
 * event, and {@code ModItems} builds the weapon cross-product from a {@code RegisterEvent} listener,
 * so anything registered that early gets a full set of weapons with no further work.</p>
 *
 * <p><b>Timing caveat:</b> the config spec is built during <em>our</em> constructor, which is before an
 * add-on that depends on us has run. Materials added by other mods therefore get the shape-level
 * config toggle but no per-material one, and default to enabled.</p>
 *
 * <p><b>Ingredient tags.</b> Each material names the tag its recipes are crafted from. When that tag is
 * empty — the providing mod is absent — the recipes simply cannot be crafted, and the creative tab
 * hides the material entirely (tab contents are rebuilt after datapacks load, so the check is live).
 * That is what lets a material exist in code but stay invisible without its mod, and it means an
 * Overgeared or similar compat can be a pure datapack.</p>
 */
public final class WeaponMaterial {
    private static final Map<String, WeaponMaterial> REGISTRY = new LinkedHashMap<>();

    // --- The vanilla ladder ----------------------------------------------------------------------
    public static final WeaponMaterial WOODEN =
            register("wooden", Tiers.WOOD, ItemTags.PLANKS, false);
    public static final WeaponMaterial STONE =
            register("stone", Tiers.STONE, ItemTags.STONE_TOOL_MATERIALS, false);
    /** Copper: vanilla's tool-less metal, sitting between stone and iron. Needs no other mod. */
    public static final WeaponMaterial COPPER =
            register("copper", copperTier(), commonTag("ingots/copper"), false);
    public static final WeaponMaterial IRON =
            register("iron", Tiers.IRON, commonTag("ingots/iron"), false);
    public static final WeaponMaterial GOLDEN =
            register("golden", Tiers.GOLD, commonTag("ingots/gold"), false);
    public static final WeaponMaterial DIAMOND =
            register("diamond", Tiers.DIAMOND, commonTag("gems/diamond"), false);
    public static final WeaponMaterial NETHERITE =
            register("netherite", Tiers.NETHERITE, commonTag("ingots/netherite"), true);

    /** Lowercase id used in registry names, textures and lang keys (e.g. {@code iron}). */
    public final String id;
    public final Tier tier;
    /** The tag a recipe of this material is crafted from; also the "does this material exist" probe. */
    public final TagKey<Item> ingredient;
    public final boolean fireResistant;

    private WeaponMaterial(String id, Tier tier, TagKey<Item> ingredient, boolean fireResistant) {
        this.id = id;
        this.tier = tier;
        this.ingredient = ingredient;
        this.fireResistant = fireResistant;
    }

    /**
     * Adds a material. Call from a mod constructor — after the first registry event this throws,
     * because the weapons for it would never be built.
     *
     * @param id            lowercase, used verbatim in item ids as {@code <id>_<weapon>}
     * @param tier          durability, mining speed, damage bonus and enchantability
     * @param ingredient    item tag the recipes craft from; an empty tag hides the material in game
     * @param fireResistant survives fire and lava, as netherite does
     */
    public static synchronized WeaponMaterial register(String id, Tier tier, TagKey<Item> ingredient,
                                                        boolean fireResistant) {
        if (locked) {
            throw new IllegalStateException(
                    "Weapon material '" + id + "' was registered too late — weapons are built during "
                    + "RegisterEvent, so materials must be added from a mod constructor.");
        }
        WeaponMaterial existing = REGISTRY.get(id);
        if (existing != null) {
            throw new IllegalArgumentException("Duplicate weapon material id: " + id);
        }
        WeaponMaterial material = new WeaponMaterial(id, tier, ingredient, fireResistant);
        REGISTRY.put(id, material);
        return material;
    }

    /** Every registered material, in registration order. */
    public static Collection<WeaponMaterial> all() {
        return REGISTRY.values();
    }

    public static Optional<WeaponMaterial> byId(String id) {
        return Optional.ofNullable(REGISTRY.get(id));
    }

    private static volatile boolean locked;

    /** Called once the weapon cross-product has been built; later registrations are a programming error. */
    public static void lock() {
        locked = true;
    }

    /** Applies material-wide item properties (netherite survives fire/lava). */
    public Item.Properties decorate(Item.Properties properties) {
        return fireResistant ? properties.fireResistant() : properties;
    }

    /**
     * Whether this material's ingredient tag currently resolves to anything. False means the mod that
     * provides the metal is not installed, so its weapons are uncraftable and should stay hidden.
     * Only meaningful once tags have loaded.
     */
    public boolean isAvailable() {
        return BuiltInRegistries.ITEM.getTag(ingredient).map(t -> t.size() > 0).orElse(false);
    }

    @Override
    public String toString() {
        return "WeaponMaterial[" + id + "]";
    }

    // ------------------------------------------------------------------ helpers

    /** A tag in the cross-loader {@code c} namespace, e.g. {@code c:ingots/copper}. */
    private static TagKey<Item> commonTag(String path) {
        return TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("c", path));
    }

    /** Copper sits between stone and iron: sturdier and sharper than stone, short of iron on every axis. */
    private static Tier copperTier() {
        Supplier<Ingredient> repair = () -> Ingredient.of(Items.COPPER_INGOT);
        return new SimpleTier(BlockTags.INCORRECT_FOR_STONE_TOOL, 200, 5.0F, 1.5F, 8, repair);
    }
}
