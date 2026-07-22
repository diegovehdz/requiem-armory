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
    // Balanced around the vanilla sword (the "middleground": iron sword = 6 dmg, 1.6 speed, 9.6 DPS)
    // and the vanilla axe (heavier: more damage, slower — retuned in ModEvents to 8 dmg, 1.1 speed,
    // 8.8 DPS at iron). No weapon exceeds the same-tier sword's DPS, and the heavy ones cap at the axe.
    // Speeds are deliberately a notch below vanilla's so melee reads as weighty rather than frantic.
    //
    // Iron-tier reference (dmg = 3 + modifier; "eff" folds in the armour-piercing hit):
    //   dagger 3×2.0=6.0 · rapier 4×1.7=6.8 · saber 5×1.6=8.0 · katana 6×1.4=8.4 · longsword 7×1.2=8.4
    //   greatsword 8×1.0=8.0 · battle axe 9×0.85=7.7 · warhammer 10×0.7=7.0 (eff 8.4) · mace 6×1.15=6.9
    //   (eff 8.6) · glaive 7×0.95=6.7 · spear 5×1.2=6.0 (eff 8.4) · halberd 8×0.8=6.4 (eff 8.0)
    //   pike 7×0.85=6.0 (eff 7.7) · hatchet 6×1.0=6.0 · javelin 4×1.1=4.4 (eff 5.5)
    // --- Swords ---
    // Dagger: fast, low melee damage, no sweep — but throwable.
    DAGGER        ("dagger",           0,  2.0f,  1.8f,  false, Category.SWORD,   ab().invincibility(15).throwable(2.0f, 1.4f, 8)),
    RAPIER        ("rapier",           1,  1.7f,  3.0f,  true,  Category.SWORD,   ab().unarmored(3.0f).sweep()),
    SABER         ("saber",            2,  1.6f,  2.75f, true,  Category.SWORD,   ab().sweep(1.0f)),
    KATANA        ("katana",           3,  1.4f,  3.25f, true,  Category.SWORD,   ab().sweep(1.25f, 2.0f).twoHanded(3.0f, 0.4f)),
    GREATSWORD    ("greatsword",       5,  1.0f,  3.5f,  true,  Category.SWORD,   ab().sweep(1.5f, 5.0f).twoHanded(4.0f, 0.3f)),
    LONGSWORD     ("longsword",        4,  1.2f,  3.5f,  true,  Category.SWORD,   ab().sweep(2.0f).twoHanded(3.0f, 0.35f)),
    BATTLE_AXE    ("battle_axe",       6,  0.85f, 3.25f, true,  Category.SWORD,   ab().versatile().breach().twoHanded(3.0f, 0.3f)),

    // --- Bludgeons (new; not in Dixta, designed in the same style) ---
    // Warhammer: the heaviest swing in the mod, so it pays for it in speed. Its armour piercing is a
    // modest top-up rather than a second weapon's worth of damage.
    WARHAMMER     ("warhammer",        7,  0.7f,  3.25f, true,  Category.BLUDGEON, ab().pierce(2.0f).breach().twoHanded(4.0f, 0.3f)),
    MACE          ("mace",             3,  1.15f, 3.0f,  true,  Category.BLUDGEON, ab().pierce(1.5f).breach()),

    // --- Polearms ---
    GLAIVE        ("glaive",           4,  0.95f, 4.0f,  true,  Category.POLEARM, ab().sweep().twoHanded(3.0f, 0.3f)),
    SPEAR         ("spear",            2,  1.2f,  4.2f,  true,  Category.POLEARM, ab().pierce(2.0f)),
    HALBERD       ("halberd",          5,  0.8f,  4.5f,  true,  Category.POLEARM, ab().pierce(4.0f, 0.5f).breach().twoHanded(3.0f, 0.25f)),
    PIKE          ("pike",             4,  0.85f, 5.0f,  true,  Category.POLEARM, ab().pierce(2.0f).twoHanded(3.0f, 0.3f)),

    // --- Throwables: usable in melee and thrown like a trident (single, recoverable) ---
    // Throw damage is `base + tier bonus`, kept under the vanilla trident's flat 8 so the trident stays
    // the best thrower: javelin tops out at diamond 7 / netherite 8, hatchet 6/7, dagger 5/6.
    HATCHET       ("hatchet",          3,  1.0f,  3.0f,  false, Category.THROWN,  ab().versatile().throwable(3.0f, 1.8f, 10)),
    JAVELIN       ("javelin",          1,  1.1f,  4.0f,  true,  Category.THROWN,  ab().pierce(1.0f).throwable(4.0f, 2.5f, 15));

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
