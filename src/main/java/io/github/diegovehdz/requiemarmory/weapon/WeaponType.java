package io.github.diegovehdz.requiemarmory.weapon;

/**
 * The central, human-readable "book of weapons": every weapon shape and its base stats live here,
 * one row each — deliberately close in spirit to Dixta's config, but type-safe and without runtime
 * JSON parsing. A weapon item is simply one {@code WeaponType} combined with one
 * {@link WeaponMaterial}.
 *
 * <p>Stat conventions (matching the values datamined from Dixta's Armory so the relative balance
 * feels the same):</p>
 * <ul>
 *   <li><b>attackDamageModifier</b> — added on top of the material's tier bonus, so damage scales
 *       with material. Diamond total ≈ {@code attackDamageModifier + 4}.</li>
 *   <li><b>attackSpeedStat</b> — on a 0–4 scale where 4.0 is "no cooldown". Converted to the vanilla
 *       attack-speed attribute via {@code stat - 4.0}.</li>
 *   <li><b>reachStat</b> — desired reach in blocks; the player's base is 3.0, so the modifier is
 *       {@code reachStat - 3.0} (can be negative for short weapons like daggers).</li>
 *   <li><b>separateModel</b> — true when the weapon uses a distinct inventory (GUI) texture and a
 *       separate in-hand (handheld) texture, via the {@code neoforge:separate_transforms} loader.</li>
 * </ul>
 *
 * <p>Special abilities (armor piercing, two-handed, sweeping, throwing, …) are layered on in later
 * phases; this phase only wires up damage, speed, reach and rendering.</p>
 */
public enum WeaponType {
    //             id                 dmg  speed  reach  split  category
    DAGGER        ("dagger",           0,  3.5f,  1.8f,  false, Category.BLADE),
    LONGSWORD     ("longsword",        5,  1.3f,  3.5f,  true,  Category.BLADE),
    RAPIER        ("rapier",           0,  2.0f,  3.0f,  true,  Category.BLADE),
    KATANA        ("katana",           3,  1.8f,  3.25f, true,  Category.BLADE),
    TWINBLADE     ("twinblade",        3,  1.9f,  3.5f,  true,  Category.BLADE),

    GREATSWORD    ("greatsword",       4,  1.2f,  3.5f,  true,  Category.HEAVY),
    BATTLE_AXE    ("battle_axe",      11,  0.6f,  3.25f, true,  Category.HEAVY),
    GLAIVE        ("glaive",           6,  1.0f,  4.0f,  true,  Category.HEAVY),

    SPEAR         ("spear",            1,  1.3f,  4.2f,  true,  Category.POLEARM),
    PIKE          ("pike",             4,  0.8f,  5.0f,  true,  Category.POLEARM),
    HALBERD       ("halberd",          6,  0.7f,  4.5f,  true,  Category.POLEARM),

    THROWING_KNIFE("throwing_knife",   0,  3.0f,  1.8f,  false, Category.THROWN),
    JAVELIN       ("javelin",          0,  1.2f,  4.0f,  true,  Category.THROWN),
    HATCHET       ("hatchet",          2,  1.0f,  3.0f,  false, Category.THROWN);

    /** Broad family, used for grouping/tab order and (later) shared ability defaults. */
    public enum Category { BLADE, HEAVY, POLEARM, THROWN }

    public final String id;
    public final int attackDamageModifier;
    public final float attackSpeedStat;
    public final float reachStat;
    public final boolean separateModel;
    public final Category category;

    WeaponType(String id, int attackDamageModifier, float attackSpeedStat, float reachStat,
               boolean separateModel, Category category) {
        this.id = id;
        this.attackDamageModifier = attackDamageModifier;
        this.attackSpeedStat = attackSpeedStat;
        this.reachStat = reachStat;
        this.separateModel = separateModel;
        this.category = category;
    }

    /** True for weapons meant to be thrown (behaviour arrives in a later phase). */
    public boolean isThrown() {
        return category == Category.THROWN;
    }

    /** Attack-speed attribute value (vanilla scale; negative is slower than bare fists). */
    public float attackSpeedModifier() {
        return attackSpeedStat - 4.0f;
    }

    /** Entity-interaction-range bonus relative to the player's 3.0 base reach. */
    public float reachModifier() {
        return reachStat - 3.0f;
    }
}
