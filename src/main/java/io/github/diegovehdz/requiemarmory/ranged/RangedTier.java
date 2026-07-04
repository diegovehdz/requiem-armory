package io.github.diegovehdz.requiemarmory.ranged;

import io.github.diegovehdz.requiemarmory.weapon.WeaponMaterial;

/**
 * The material progression for ranged weapons and its per-material scaling. Deliberately a
 * <em>subset</em> of the melee {@link WeaponMaterial} set — ranged skips stone — with
 * {@link #IRON} pinned to the vanilla bow's stats ("vanilla = iron").
 *
 * <p><b>Damage and range share one lever: {@code velocityMult}.</b> An arrow's damage is
 * {@code impactSpeed × baseDamage} (vanilla {@code baseDamage} = 2.0), and impact speed tracks launch
 * velocity, so a faster bow both reaches further <em>and</em> hits harder — matching the vanilla
 * per-material {@code +1} damage steps (iron 6 → diamond 7 → netherite 8) instead of ballooning.
 * Keeping the two coupled avoids the compounding that made higher tiers wildly overpowered.</p>
 *
 * <p><b>{@code variance}</b> is a separate lever: it scales the random spread of a full-charge shot's
 * damage (vanilla's crit bonus) <em>around its mean</em>, so higher tiers hit more consistently
 * without changing their average — netherite is the tightest. It never adds damage.</p>
 *
 * <p>Lower tiers draw and move faster but hit softer, travel shorter and break sooner; higher tiers
 * invert that. Gold is the classic outlier: fast, highly enchantable and fragile, but no harder-
 * hitting than iron. Numbers are the single place to balance ranged weapons.</p>
 */
public enum RangedTier {
    //           material                 draw×  vel×   durability  move+    ench  variance
    WOODEN   (WeaponMaterial.WOODEN,      0.85f, 0.834f, 240,       0.03f,   1,    1.00f),  // ~vel 2.5 -> 5 dmg
    IRON     (WeaponMaterial.IRON,        1.00f, 1.000f, 384,       0.00f,   14,   0.90f),  // ~vel 3.0 -> 6 dmg (vanilla)
    GOLDEN   (WeaponMaterial.GOLDEN,      0.80f, 1.000f, 150,       0.04f,   22,   0.75f),  // ~vel 3.0 -> 6 dmg (side-grade)
    DIAMOND  (WeaponMaterial.DIAMOND,     1.15f, 1.167f, 660,      -0.02f,   10,   0.55f),  // ~vel 3.5 -> 7 dmg
    NETHERITE(WeaponMaterial.NETHERITE,   1.30f, 1.334f, 900,      -0.04f,   15,   0.35f);  // ~vel 4.0 -> 8 dmg, tightest

    public final WeaponMaterial material;
    /** Multiplier on the type's base draw time; {@code < 1} draws faster than the iron reference. */
    public final float drawMult;
    /** Multiplier on the type's base launch velocity — drives both range <em>and</em> damage. */
    public final float velocityMult;
    /** Item durability (bows; scaled by {@link RangedType#durabilityFactor} for other shapes). */
    public final int durability;
    /** Walk-speed modifier applied while the weapon is drawn (positive = faster). Used in a later phase. */
    public final float moveModifier;
    /** Enchantability value (higher = better table enchantments). */
    public final int enchantValue;
    /** Full-charge damage-spread multiplier; {@code 1.0} = vanilla randomness, lower = more consistent. */
    public final float variance;

    RangedTier(WeaponMaterial material, float drawMult, float velocityMult,
               int durability, float moveModifier, int enchantValue, float variance) {
        this.material = material;
        this.drawMult = drawMult;
        this.velocityMult = velocityMult;
        this.durability = durability;
        this.moveModifier = moveModifier;
        this.enchantValue = enchantValue;
        this.variance = variance;
    }
}
