package io.github.diegovehdz.requiemarmory.registry;

import java.util.LinkedHashMap;
import java.util.Map;

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
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers every item. Weapons are produced from the cross product of {@link WeaponType} ×
 * {@link WeaponMaterial}, so adding a weapon is a one-line change in those enums.
 */
public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RequiemArmory.MOD_ID);

    /** Every weapon shown in-game, keyed by "&lt;material&gt;_&lt;type&gt;", in registration (and tab) order. */
    public static final Map<String, DeferredItem<WeaponItem>> WEAPONS = new LinkedHashMap<>();

    /** Every tiered ranged weapon (bows/crossbows), keyed by "&lt;material&gt;_&lt;type&gt;". The wooden
     *  tier of vanilla-backed types is omitted here — it is the vanilla item, adjusted via mixin. */
    public static final Map<String, DeferredItem<Item>> RANGED = new LinkedHashMap<>();

    /** Short grip used to craft most weapons. */
    public static final DeferredItem<Item> HANDLE = ITEMS.registerSimpleItem("handle");

    /** Long "pole handle" (shaft) used to craft polearms. */
    public static final DeferredItem<Item> POLE = ITEMS.registerSimpleItem("pole");

    static {
        for (WeaponType type : WeaponType.values()) {
            for (WeaponMaterial material : WeaponMaterial.values()) {
                String name = material.id + "_" + type.id;
                DeferredItem<WeaponItem> weapon = ITEMS.registerItem(name, props -> {
                    Item.Properties p = material.decorate(props)
                            .attributes(WeaponItem.buildAttributes(type, material));
                    return type.abilities.isThrowable()
                            ? new ThrowableWeaponItem(type, material, p)
                            : new WeaponItem(type, material, p);
                });
                WEAPONS.put(name, weapon);
            }
        }
    }

    static {
        for (RangedType type : RangedType.values()) {
            for (RangedTier tier : RangedTier.values()) {
                // Vanilla-backed types (bow/crossbow) get their wooden tier from the vanilla item.
                if (tier == RangedTier.WOODEN && type.vanillaWooden) {
                    continue;
                }
                String name = tier.material.id + "_" + type.id;
                RangedStats stats = RangedStats.of(type, tier);
                DeferredItem<Item> item = ITEMS.registerItem(name, props -> {
                    Item.Properties p = tier.material.decorate(props.durability(stats.durability()));
                    Item created = switch (type.family) {
                        case BOW -> new BowWeaponItem(type, tier, p);
                        case CROSSBOW -> new CrossbowWeaponItem(type, tier, p);
                    };
                    return created;
                });
                RANGED.put(name, item);
            }
        }
    }

    /** Convenience accessor used e.g. for the creative-tab icon. */
    public static WeaponItem weapon(String key) {
        return WEAPONS.get(key).get();
    }
}
