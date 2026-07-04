package io.github.diegovehdz.requiemarmory.ranged;

import net.minecraft.world.item.ItemStack;

/**
 * Common surface for the mod's tiered ranged items ({@link BowWeaponItem} and, later, the crossbow),
 * so code that only has an {@link ItemStack} — notably the arrow-consistency mixin — can read a
 * weapon's resolved {@link RangedStats} without caring about its concrete class.
 */
public interface RangedWeapon {
    RangedStats stats();

    /**
     * The full-charge damage-spread multiplier for the weapon that fired an arrow, or {@code 1.0}
     * (vanilla randomness) for the vanilla bow/crossbow and anything that is not a tiered ranged item.
     */
    static float varianceOf(ItemStack weapon) {
        return weapon != null && weapon.getItem() instanceof RangedWeapon ranged
                ? ranged.stats().variance()
                : 1.0f;
    }
}
