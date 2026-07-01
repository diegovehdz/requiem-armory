package io.github.diegovehdz.requiemarmory.weapon;

/**
 * The special combat properties a weapon type can carry. Built with a small fluent builder so each
 * {@link WeaponType} row only mentions the abilities it actually has, e.g.
 * {@code Abilities.builder().pierce(2).build()}.
 *
 * <p>Values follow Dixta's Armory so the relative balance matches.</p>
 */
public final class WeaponAbilities {
    /** Vanilla post-hit invulnerability, in ticks (0.5s). Below = quick strike, above = slow strike. */
    public static final int DEFAULT_INVINCIBILITY = 20;

    public final float armorPierceAmount;
    public final float armorPierceChance;
    public final float unarmoredBonus;
    public final int invincibilityTicks;
    public final boolean canSweep;
    public final float sweepRadius;
    public final float sweepDamage;
    public final boolean breach;
    public final boolean versatile;

    private WeaponAbilities(Builder b) {
        this.armorPierceAmount = b.armorPierceAmount;
        this.armorPierceChance = b.armorPierceChance;
        this.unarmoredBonus = b.unarmoredBonus;
        this.invincibilityTicks = b.invincibilityTicks;
        this.canSweep = b.canSweep;
        this.sweepRadius = b.sweepRadius;
        this.sweepDamage = b.sweepDamage;
        this.breach = b.breach;
        this.versatile = b.versatile;
    }

    public boolean hasArmorPierce() { return armorPierceAmount > 0.0f; }
    public boolean hasUnarmoredBonus() { return unarmoredBonus > 0.0f; }
    public boolean hasQuickStrike() { return invincibilityTicks < DEFAULT_INVINCIBILITY; }
    public boolean hasSlowStrike() { return invincibilityTicks > DEFAULT_INVINCIBILITY; }

    /** Whether the sweep is noteworthy enough to advertise in the tooltip. */
    public boolean showsSweep() { return canSweep && (sweepRadius != 1.0f || sweepDamage > 0.0f); }

    public static Builder builder() { return new Builder(); }
    public static WeaponAbilities none() { return builder().build(); }

    public static final class Builder {
        private float armorPierceAmount = 0.0f;
        private float armorPierceChance = 1.0f;
        private float unarmoredBonus = 0.0f;
        private int invincibilityTicks = DEFAULT_INVINCIBILITY;
        private boolean canSweep = false;
        private float sweepRadius = 1.0f;
        private float sweepDamage = 0.0f;
        private boolean breach = false;
        private boolean versatile = false;

        /** Extra armour-ignoring damage on a fully-charged hit (always applies). */
        public Builder pierce(float amount) { this.armorPierceAmount = amount; return this; }

        /** Extra armour-ignoring damage on a charged hit, with a chance to trigger. */
        public Builder pierce(float amount, float chance) {
            this.armorPierceAmount = amount;
            this.armorPierceChance = chance;
            return this;
        }

        /** Extra damage against targets wearing no armour (on a charged hit). */
        public Builder unarmored(float bonus) { this.unarmoredBonus = bonus; return this; }

        /** Post-hit invulnerability in ticks; {@code < 20} = quick strike, {@code > 20} = slow strike. */
        public Builder invincibility(int ticks) { this.invincibilityTicks = ticks; return this; }

        public Builder sweep() { this.canSweep = true; return this; }
        public Builder sweep(float radius) { this.canSweep = true; this.sweepRadius = radius; return this; }
        public Builder sweep(float radius, float bonusDamage) {
            this.canSweep = true;
            this.sweepRadius = radius;
            this.sweepDamage = bonusDamage;
            return this;
        }

        /** Disables the target's shield on hit, like an axe. */
        public Builder breach() { this.breach = true; return this; }

        /** Can be used as an axe (stripping, scraping, de-waxing) as well as a weapon. */
        public Builder versatile() { this.versatile = true; return this; }

        public WeaponAbilities build() { return new WeaponAbilities(this); }
    }
}
