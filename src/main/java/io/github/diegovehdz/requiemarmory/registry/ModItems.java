package io.github.diegovehdz.requiemarmory.registry;

import java.util.LinkedHashMap;
import java.util.Map;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
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

    /** Structural ingredient for polearm-style recipes (used later by crafting). */
    public static final DeferredItem<Item> POLE = ITEMS.registerSimpleItem("pole");

    static {
        for (WeaponType type : WeaponType.values()) {
            for (WeaponMaterial material : WeaponMaterial.values()) {
                String name = material.id + "_" + type.id;
                DeferredItem<WeaponItem> weapon = ITEMS.registerItem(name, props ->
                        new WeaponItem(type, material,
                                material.decorate(props).attributes(WeaponItem.buildAttributes(type, material))));
                WEAPONS.put(name, weapon);
            }
        }
    }

    /** Convenience accessor used e.g. for the creative-tab icon. */
    public static WeaponItem weapon(String key) {
        return WEAPONS.get(key).get();
    }
}
