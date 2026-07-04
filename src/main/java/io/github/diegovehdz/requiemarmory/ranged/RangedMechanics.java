package io.github.diegovehdz.requiemarmory.ranged;

/**
 * Shared firing math for tiered ranged weapons, kept in one place so the modded item classes and the
 * vanilla-item mixin (wooden tier) stay in sync.
 *
 * <p>Damage and range are both carried by launch velocity (see {@link RangedTier}), so there is no
 * per-arrow damage manipulation here — the heavy lifting (ammo, multishot, spawning) is reused from
 * vanilla {@code ProjectileWeaponItem#draw}/{@code #shoot}. This only supplies the tier-dependent
 * <em>charge curve</em>.</p>
 */
public final class RangedMechanics {
    private RangedMechanics() {}

    /**
     * Charge fraction (0..1) for a bow held for {@code charge} ticks with the given draw time. This
     * generalises vanilla {@code BowItem#getPowerForTime} (which hardcodes a 20-tick draw) to any
     * tier's draw speed while keeping the same easing curve.
     */
    public static float powerForCharge(int charge, int drawTicks) {
        float f = (float) charge / drawTicks;
        f = (f * f + f * 2.0F) / 3.0F;
        return f > 1.0F ? 1.0F : f;
    }
}
