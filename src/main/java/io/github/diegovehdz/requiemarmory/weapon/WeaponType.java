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
    //             id                 dmg  speed  reach  split  category          abilities
    // --- Swords ---
    DAGGER        ("dagger",           0,  3.5f,  1.8f,  false, Category.SWORD,   ab().invincibility(15).sweep(0.25f)),
    RAPIER        ("rapier",           0,  2.0f,  3.0f,  true,  Category.SWORD,   ab().unarmored(3.0f).sweep()),
    SABER         ("saber",            1,  2.4f,  2.75f, true,  Category.SWORD,   ab().sweep(1.0f)),
    KATANA        ("katana",           3,  1.8f,  3.25f, true,  Category.SWORD,   ab().sweep(1.25f, 2.0f).twoHandedI(2.0f, 5.0f, 0.05f, 1.0f)),
    GREATSWORD    ("greatsword",       4,  1.2f,  3.5f,  true,  Category.SWORD,   ab().sweep(1.5f, 5.0f).twoHandedII(6.0f, 0.4f)),
    LONGSWORD     ("longsword",        5,  1.3f,  3.5f,  true,  Category.SWORD,   ab().sweep(2.0f).twoHandedII(6.0f, 0.4f)),
    TWINBLADE     ("twinblade",        3,  1.9f,  3.5f,  true,  Category.SWORD,   ab().sweep().twoHandedII(6.0f, 0.4f)),
    BATTLE_AXE    ("battle_axe",      11,  0.6f,  3.25f, true,  Category.SWORD,   ab().versatile().twoHandedI(1.0f, 4.0f, 0.1f, 0.3f)),

    // --- Bludgeons (new; not in Dixta, designed in the same style) ---
    WARHAMMER     ("warhammer",        9,  0.7f,  3.25f, true,  Category.BLUDGEON, ab().pierce(3.0f).twoHandedII(6.0f, 0.4f)),
    MACE          ("mace",             2,  1.6f,  3.0f,  true,  Category.BLUDGEON, ab().pierce(1.5f)),

    // --- Polearms ---
    GLAIVE        ("glaive",           6,  1.0f,  4.0f,  true,  Category.POLEARM, ab().sweep().twoHandedII(4.0f, 0.5f)),
    SPEAR         ("spear",            1,  1.3f,  4.2f,  true,  Category.POLEARM, ab().pierce(2.0f).twoHandedI(0.0f, 3.0f, 0.1f, 0.55f)),
    HALBERD       ("halberd",          6,  0.7f,  4.5f,  true,  Category.POLEARM, ab().pierce(4.0f, 0.5f).breach().twoHandedII(5.0f, 0.2f)),
    PIKE          ("pike",             4,  0.8f,  5.0f,  true,  Category.POLEARM, ab().pierce(2.0f).twoHandedII(4.0f, 0.3f)),

    // --- Throwables: usable in melee and thrown like a trident (single, recoverable) ---
    THROWING_KNIFE("throwing_knife",   0,  3.0f,  1.8f,  false, Category.THROWN,  ab().invincibility(15).throwable(7.0f, 1.5f, 8)),
    HATCHET       ("hatchet",          2,  1.0f,  3.0f,  false, Category.THROWN,  ab().versatile().throwable(8.0f, 2.0f, 10)),
    JAVELIN       ("javelin",          0,  1.2f,  4.0f,  true,  Category.THROWN,  ab().pierce(1.0f).throwable(11.0f, 3.0f, 15));

    /** Broad family, used for grouping/tab order and (later) shared ability defaults. */
    public enum Category { SWORD, BLUDGEON, POLEARM, THROWN }

    public final String id;
    public final int attackDamageModifier;
    public final float attackSpeedStat;
    public final float reachStat;
    public final boolean separateModel;
    public final Category category;
    public final WeaponAbilities abilities;

    WeaponType(String id, int attackDamageModifier, float attackSpeedStat, float reachStat,
               boolean separateModel, Category category, WeaponAbilities.Builder abilities) {
        this.id = id;
        this.attackDamageModifier = attackDamageModifier;
        this.attackSpeedStat = attackSpeedStat;
        this.reachStat = reachStat;
        this.separateModel = separateModel;
        this.category = category;
        this.abilities = abilities.build();
    }

    /** Short alias for {@link WeaponAbilities#builder()} to keep the table above readable. */
    private static WeaponAbilities.Builder ab() {
        return WeaponAbilities.builder();
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
