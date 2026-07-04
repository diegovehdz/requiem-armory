package io.github.diegovehdz.requiemarmory.ranged;

/**
 * The concrete, resolved stats for one {@link RangedType} × {@link RangedTier} combination — the
 * numbers the item actually fires with. Produced once per item at construction.
 *
 * <p>Damage is not stored here: it emerges from {@link #velocity} via vanilla's
 * {@code impactSpeed × baseDamage(2.0)} formula, keeping range and damage on the same lever.</p>
 *
 * @param drawTicks    ticks to reach full charge (drives both firing and the pull animation)
 * @param velocity     full-charge launch velocity (vanilla bow = 3.0); sets both range and damage
 * @param durability   item max durability
 * @param moveModifier walk-speed modifier while drawn (positive = faster); applied in a later phase
 * @param enchantValue enchantability value
 * @param variance     full-charge damage-spread multiplier (1.0 = vanilla, lower = more consistent)
 */
public record RangedStats(int drawTicks, float velocity, int durability,
                          float moveModifier, int enchantValue, float variance) {

    public static RangedStats of(RangedType type, RangedTier tier) {
        int draw = Math.max(1, Math.round(type.baseDrawTicks * tier.drawMult));
        float velocity = type.baseVelocity * tier.velocityMult;
        int durability = Math.max(1, Math.round(tier.durability * type.durabilityFactor));
        return new RangedStats(draw, velocity, durability, tier.moveModifier, tier.enchantValue, tier.variance);
    }
}
