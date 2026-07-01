package io.github.diegovehdz.requiemarmory.registry;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
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

    /** Every weapon, keyed by "&lt;material&gt;_&lt;type&gt;", in registration (and tab) order. */
    public static final Map<String, DeferredItem<WeaponItem>> WEAPONS = new LinkedHashMap<>();

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
                    return type.isThrown()
                            ? new ThrowableWeaponItem(type, material, p)
                            : new WeaponItem(type, material, p);
                });
                WEAPONS.put(name, weapon);
            }
        }
    }

    /** Convenience accessor used e.g. for the creative-tab icon. */
    public static WeaponItem weapon(String key) {
        return WEAPONS.get(key).get();
    }
}
