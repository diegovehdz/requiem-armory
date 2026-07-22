package io.github.diegovehdz.requiemarmory.registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.ranged.BowWeaponItem;
import io.github.diegovehdz.requiemarmory.ranged.CrossbowWeaponItem;
import io.github.diegovehdz.requiemarmory.ranged.RangedStats;
import io.github.diegovehdz.requiemarmory.ranged.RangedTier;
import io.github.diegovehdz.requiemarmory.ranged.RangedType;
import io.github.diegovehdz.requiemarmory.weapon.ThrowableWeaponItem;
import io.github.diegovehdz.requiemarmory.weapon.WeaponItem;
import io.github.diegovehdz.requiemarmory.weapon.WeaponMaterial;
import io.github.diegovehdz.requiemarmory.weapon.WeaponType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * Registers every item. Weapons are produced from the cross product of {@link WeaponType} ×
 * {@link WeaponMaterial}, so adding a weapon — or a material — is a one-line change.
 *
 * <p>The cross product is built from a {@link RegisterEvent} listener rather than a static
 * initialiser. That is deliberate: {@code WeaponMaterial} is an open registry, and an add-on that
 * depends on this mod has its constructor run <em>after</em> ours. Registry events fire after every
 * mod constructor, so waiting until then is what lets an add-on's materials get a full weapon set.
 * Static components (handle, pole) have no such constraint and stay on a {@link DeferredRegister}.</p>
 */
public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RequiemArmory.MOD_ID);

    /** Every weapon shown in-game, keyed by "&lt;material&gt;_&lt;type&gt;", in registration (and tab) order. */
    public static final Map<String, Item> WEAPONS = new LinkedHashMap<>();

    /** Every tiered ranged weapon (bows/crossbows), keyed by "&lt;material&gt;_&lt;type&gt;". The wooden
     *  tier of vanilla-backed types is omitted here — it is the vanilla item, adjusted via mixin. */
    public static final Map<String, Item> RANGED = new LinkedHashMap<>();

    /** Short grip used to craft most weapons. */
    public static final DeferredItem<Item> HANDLE = ITEMS.registerSimpleItem("handle");

    /** Long "pole handle" (shaft) used to craft polearms. */
    public static final DeferredItem<Item> POLE = ITEMS.registerSimpleItem("pole");

    /** Builds and registers every weapon. Bound to the mod bus from the main mod class. */
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.ITEM, helper -> {
            // From here on a new material would never get weapons, so refuse to add one.
            WeaponMaterial.lock();

            for (WeaponType type : WeaponType.values()) {
                for (WeaponMaterial material : WeaponMaterial.all()) {
                    String name = material.id + "_" + type.id;
                    Item.Properties props = material.decorate(new Item.Properties())
                            .attributes(WeaponItem.buildAttributes(type, material));
                    Item weapon = type.abilities.isThrowable()
                            ? new ThrowableWeaponItem(type, material, props)
                            : new WeaponItem(type, material, props);
                    helper.register(id(name), weapon);
                    WEAPONS.put(name, weapon);
                }
            }

            for (RangedType type : RangedType.values()) {
                for (RangedTier tier : RangedTier.values()) {
                    // Vanilla-backed types (bow/crossbow) get their wooden tier from the vanilla item.
                    if (tier == RangedTier.WOODEN && type.vanillaWooden) {
                        continue;
                    }
                    String name = tier.material.id + "_" + type.id;
                    RangedStats stats = RangedStats.of(type, tier);
                    Item.Properties props = tier.material.decorate(
                            new Item.Properties().durability(stats.durability()));
                    Item ranged = switch (type.family) {
                        case BOW -> new BowWeaponItem(type, tier, props);
                        case CROSSBOW -> new CrossbowWeaponItem(type, tier, props);
                    };
                    helper.register(id(name), ranged);
                    RANGED.put(name, ranged);
                }
            }

            RequiemArmory.LOGGER.info("[{}] Registered {} melee and {} ranged weapons across {} materials",
                    RequiemArmory.MOD_ID, WEAPONS.size(), RANGED.size(), WeaponMaterial.all().size());
        });
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, path);
    }

    /** Convenience accessor used e.g. for the creative-tab icon. */
    public static WeaponItem weapon(String key) {
        return (WeaponItem) WEAPONS.get(key);
    }

    /**
     * Looks up any weapon of this mod — melee or ranged — by registry path, e.g.
     * {@code "iron_warhammer"} or {@code "diamond_longbow"}. Empty when the combination was never
     * registered (there is no stone bow, and the wooden bow/crossbow are the vanilla items).
     */
    public static Optional<Item> find(String path) {
        Item melee = WEAPONS.get(path);
        return Optional.ofNullable(melee != null ? melee : RANGED.get(path));
    }
}
