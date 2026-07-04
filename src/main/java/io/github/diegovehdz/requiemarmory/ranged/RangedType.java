package io.github.diegovehdz.requiemarmory.ranged;

/**
 * The "book of ranged weapons": one row per ranged weapon shape (bow, longbow, crossbow, heavy
 * crossbow), holding the <em>type-level</em> base profile. The per-material scaling (draw speed,
 * velocity, durability, …) lives in {@link RangedTier}; a concrete weapon is one {@code RangedType}
 * combined with one {@code RangedTier}, resolved into a {@link RangedStats}.
 *
 * <p>Design mirrors the melee {@code WeaponType × WeaponMaterial} cross-product, but ranged weapons
 * are a different Minecraft item family ({@code BowItem}/{@code CrossbowItem}) so they get their own
 * enums and item classes.</p>
 *
 * <p>Reference values are anchored to the vanilla bow at the {@link RangedTier#IRON} tier
 * ("vanilla = iron"): a fully-charged vanilla bow launches an arrow at velocity {@code 3.0} after a
 * {@code 20}-tick draw, for ~6 damage.</p>
 */
public enum RangedType {
    //          id          family            vanillaWooden  drawTicks  velocity  duraFactor  fireworks  range
    BOW        ("bow",      Family.BOW,       true,          20,        3.0f,     1.0f,       false,     15),
    // Crossbow: charge time is the "draw", velocity is fixed per shot (3.15 vanilla) not charge-scaled.
    CROSSBOW   ("crossbow", Family.CROSSBOW,  true,          25,        3.15f,    1.2f,       true,      8);

    /** Broad family: which vanilla item class backs this type and how it fires. */
    public enum Family { BOW, CROSSBOW }

    public final String id;
    public final Family family;
    /** True when the wooden tier is provided by the vanilla item (bow/crossbow) rather than a new
     *  item — the wooden behaviour is then applied to the vanilla item via mixin. */
    public final boolean vanillaWooden;
    /** Ticks to reach full charge at the iron reference tier (vanilla bow = 20). */
    public final int baseDrawTicks;
    /** Full-charge launch velocity at the iron reference tier (vanilla bow = 3.0). Sets range + damage. */
    public final float baseVelocity;
    /** Multiplier applied to the tier durability (crossbows are sturdier than bows). */
    public final float durabilityFactor;
    /** Whether firework rockets are a valid projectile (crossbows yes, heavy crossbow no). */
    public final boolean supportsFireworks;
    /** Default projectile range used by dispensers/AI. */
    public final int projectileRange;

    RangedType(String id, Family family, boolean vanillaWooden, int baseDrawTicks, float baseVelocity,
               float durabilityFactor, boolean supportsFireworks, int projectileRange) {
        this.id = id;
        this.family = family;
        this.vanillaWooden = vanillaWooden;
        this.baseDrawTicks = baseDrawTicks;
        this.baseVelocity = baseVelocity;
        this.durabilityFactor = durabilityFactor;
        this.supportsFireworks = supportsFireworks;
        this.projectileRange = projectileRange;
    }
}
