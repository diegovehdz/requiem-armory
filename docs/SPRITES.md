# Requiem Armory — Sprite Checklist

All item textures the mod needs. **6 materials** each: `wooden, stone, iron, golden, diamond, netherite`.

**Legend**
- **GUI** = flat inventory icon, always **16×16** (`<material>_<type>_gui.png`).
- **HAND** = in-hand / flying model texture (`<material>_<type>_handheld.png`).
- **SINGLE** = one texture for both inventory and in-hand (small weapons), **16×16** (`<material>_<type>.png`).

> **Throwable weapons:** the dagger, hatchet and javelin are all throwable (they reuse their own
> textures when flying — no extra sprite). The dagger and hatchet stay in Swords/Axes visually.

## Swords

| Weapon | Textures per material | Size | Files (×6) |
|---|---|---|---|
| dagger | `<mat>_dagger.png` (SINGLE) | 16×16 | 6 |
| rapier | `<mat>_rapier_gui.png` + `<mat>_rapier_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| saber | `<mat>_saber_gui.png` + `<mat>_saber_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| katana | `<mat>_katana_gui.png` + `<mat>_katana_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| greatsword | `<mat>_greatsword_gui.png` + `<mat>_greatsword_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| longsword | `<mat>_longsword_gui.png` + `<mat>_longsword_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| twinblade | `<mat>_twinblade_gui.png` + `<mat>_twinblade_handheld.png` | GUI 16×16 · HAND 64×64 | 12 |

## Bludgeons

| Weapon | Textures per material | Size | Files (×6) |
|---|---|---|---|
| warhammer | `<mat>_warhammer_gui.png` + `<mat>_warhammer_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| mace | `<mat>_mace_gui.png` + `<mat>_mace_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |

## Polearms

| Weapon | Textures per material | Size | Files (×6) |
|---|---|---|---|
| glaive | `<mat>_glaive_gui.png` + `<mat>_glaive_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| spear | `<mat>_spear_gui.png` + `<mat>_spear_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| halberd | `<mat>_halberd_gui.png` + `<mat>_halberd_handheld.png` | GUI 16×16 · HAND 64×64 | 12 |
| pike | `<mat>_pike_gui.png` + `<mat>_pike_handheld.png` | GUI 16×16 · HAND 64×64 | 12 |

## Axes

| Weapon | Textures per material | Size | Files (×6) |
|---|---|---|---|
| battle_axe | `<mat>_battle_axe_gui.png` + `<mat>_battle_axe_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |
| hatchet | `<mat>_hatchet.png` (SINGLE) | 16×16 | 6 |

## Throwable

| Weapon | Textures per material | Size | Files (×6) |
|---|---|---|---|
| javelin | `<mat>_javelin_gui.png` + `<mat>_javelin_handheld.png` | GUI 16×16 · HAND 32×32 | 12 |

## Components / other

| Item | Textures | Size |
|---|---|---|
| pole (pole handle) | `pole_gui.png` + `pole_handheld.png` | 16×16 · 32×32 |
| handle | `handle.png` | 16×16 |

## Ranged (in-game, **placeholder art**)

All ranged sprites are **16×16 single textures** and are currently **Archeries placeholders** (to be
replaced with original art). The wooden **bow** and **crossbow** reuse the vanilla item textures (they
*are* the vanilla item), so they need no sprite. The wooden **longbow**/**heavy crossbow** are real
new items but currently reuse the vanilla bow/crossbow art — they need their own sprites eventually.

| Weapon | Textures per tier | Tiers needing art | Files |
|---|---|---|---|
| bow | `<mat>_bow` + `_bow_pulling_0/1/2` | iron, golden, diamond, netherite | 16 |
| crossbow | `<mat>_crossbow_standby` + `_pulling_0/1/2` + `_arrow` + `_firework` | iron, golden, diamond, netherite | 24 |
| longbow | `<mat>_longbow` + `_longbow_pulling_0/1/2` | wooden, iron, golden, diamond, netherite | 20 |
| heavy_crossbow | `<mat>_heavy_crossbow_standby` + `_pulling_0/1/2` + `_arrow` (no firework) | wooden, iron, golden, diamond, netherite | 25 |

**Total ranged sprites: 85.** Distinct shapes wanted so longbow ≠ bow and heavy crossbow ≠ crossbow
(they currently share art as placeholders).

### Arrow-pull overlays (compat feature)
Overlay sprites layered onto a bow's pull sprite to show the nocked arrow, by convention
`<namespace>:textures/arrow_pull/<arrow>_pulling.png` (mirrors Archery Expansion so other mods opt in
by shipping their own). Ours to provide: `minecraft:arrow_pull/arrow_pulling`,
`spectral_arrow_pulling`, and `tipped_arrow_shaft_pulling` + `tipped_arrow_tip_pulling` (tip tinted by
potion colour). ~4 base overlays.

## Not yet in the mod (future)

- **Shields**: not in the roster yet.

---

# Full file checklist

### Swords
- [ ] `wooden_dagger.png` (16×16)
- [ ] `stone_dagger.png` (16×16)
- [ ] `iron_dagger.png` (16×16)
- [ ] `golden_dagger.png` (16×16)
- [ ] `diamond_dagger.png` (16×16)
- [ ] `netherite_dagger.png` (16×16)
- [ ] `wooden_rapier_gui.png` (16×16)
- [ ] `wooden_rapier_handheld.png` (32×32)
- [ ] `stone_rapier_gui.png` (16×16)
- [ ] `stone_rapier_handheld.png` (32×32)
- [ ] `iron_rapier_gui.png` (16×16)
- [ ] `iron_rapier_handheld.png` (32×32)
- [ ] `golden_rapier_gui.png` (16×16)
- [ ] `golden_rapier_handheld.png` (32×32)
- [ ] `diamond_rapier_gui.png` (16×16)
- [ ] `diamond_rapier_handheld.png` (32×32)
- [ ] `netherite_rapier_gui.png` (16×16)
- [ ] `netherite_rapier_handheld.png` (32×32)
- [ ] `wooden_saber_gui.png` (16×16)
- [ ] `wooden_saber_handheld.png` (32×32)
- [ ] `stone_saber_gui.png` (16×16)
- [ ] `stone_saber_handheld.png` (32×32)
- [ ] `iron_saber_gui.png` (16×16)
- [ ] `iron_saber_handheld.png` (32×32)
- [ ] `golden_saber_gui.png` (16×16)
- [ ] `golden_saber_handheld.png` (32×32)
- [ ] `diamond_saber_gui.png` (16×16)
- [ ] `diamond_saber_handheld.png` (32×32)
- [ ] `netherite_saber_gui.png` (16×16)
- [ ] `netherite_saber_handheld.png` (32×32)
- [ ] `wooden_katana_gui.png` (16×16)
- [ ] `wooden_katana_handheld.png` (32×32)
- [ ] `stone_katana_gui.png` (16×16)
- [ ] `stone_katana_handheld.png` (32×32)
- [ ] `iron_katana_gui.png` (16×16)
- [ ] `iron_katana_handheld.png` (32×32)
- [ ] `golden_katana_gui.png` (16×16)
- [ ] `golden_katana_handheld.png` (32×32)
- [ ] `diamond_katana_gui.png` (16×16)
- [ ] `diamond_katana_handheld.png` (32×32)
- [ ] `netherite_katana_gui.png` (16×16)
- [ ] `netherite_katana_handheld.png` (32×32)
- [ ] `wooden_greatsword_gui.png` (16×16)
- [ ] `wooden_greatsword_handheld.png` (32×32)
- [ ] `stone_greatsword_gui.png` (16×16)
- [ ] `stone_greatsword_handheld.png` (32×32)
- [ ] `iron_greatsword_gui.png` (16×16)
- [ ] `iron_greatsword_handheld.png` (32×32)
- [ ] `golden_greatsword_gui.png` (16×16)
- [ ] `golden_greatsword_handheld.png` (32×32)
- [ ] `diamond_greatsword_gui.png` (16×16)
- [ ] `diamond_greatsword_handheld.png` (32×32)
- [ ] `netherite_greatsword_gui.png` (16×16)
- [ ] `netherite_greatsword_handheld.png` (32×32)
- [ ] `wooden_longsword_gui.png` (16×16)
- [ ] `wooden_longsword_handheld.png` (32×32)
- [ ] `stone_longsword_gui.png` (16×16)
- [ ] `stone_longsword_handheld.png` (32×32)
- [ ] `iron_longsword_gui.png` (16×16)
- [ ] `iron_longsword_handheld.png` (32×32)
- [ ] `golden_longsword_gui.png` (16×16)
- [ ] `golden_longsword_handheld.png` (32×32)
- [ ] `diamond_longsword_gui.png` (16×16)
- [ ] `diamond_longsword_handheld.png` (32×32)
- [ ] `netherite_longsword_gui.png` (16×16)
- [ ] `netherite_longsword_handheld.png` (32×32)
- [ ] `wooden_twinblade_gui.png` (16×16)
- [ ] `wooden_twinblade_handheld.png` (64×64)
- [ ] `stone_twinblade_gui.png` (16×16)
- [ ] `stone_twinblade_handheld.png` (64×64)
- [ ] `iron_twinblade_gui.png` (16×16)
- [ ] `iron_twinblade_handheld.png` (64×64)
- [ ] `golden_twinblade_gui.png` (16×16)
- [ ] `golden_twinblade_handheld.png` (64×64)
- [ ] `diamond_twinblade_gui.png` (16×16)
- [ ] `diamond_twinblade_handheld.png` (64×64)
- [ ] `netherite_twinblade_gui.png` (16×16)
- [ ] `netherite_twinblade_handheld.png` (64×64)

### Bludgeons
- [ ] `wooden_warhammer_gui.png` (16×16)
- [ ] `wooden_warhammer_handheld.png` (32×32)
- [ ] `stone_warhammer_gui.png` (16×16)
- [ ] `stone_warhammer_handheld.png` (32×32)
- [ ] `iron_warhammer_gui.png` (16×16)
- [ ] `iron_warhammer_handheld.png` (32×32)
- [ ] `golden_warhammer_gui.png` (16×16)
- [ ] `golden_warhammer_handheld.png` (32×32)
- [ ] `diamond_warhammer_gui.png` (16×16)
- [ ] `diamond_warhammer_handheld.png` (32×32)
- [ ] `netherite_warhammer_gui.png` (16×16)
- [ ] `netherite_warhammer_handheld.png` (32×32)
- [ ] `wooden_mace_gui.png` (16×16)
- [ ] `wooden_mace_handheld.png` (32×32)
- [ ] `stone_mace_gui.png` (16×16)
- [ ] `stone_mace_handheld.png` (32×32)
- [ ] `iron_mace_gui.png` (16×16)
- [ ] `iron_mace_handheld.png` (32×32)
- [ ] `golden_mace_gui.png` (16×16)
- [ ] `golden_mace_handheld.png` (32×32)
- [ ] `diamond_mace_gui.png` (16×16)
- [ ] `diamond_mace_handheld.png` (32×32)
- [ ] `netherite_mace_gui.png` (16×16)
- [ ] `netherite_mace_handheld.png` (32×32)

### Polearms
- [ ] `wooden_glaive_gui.png` (16×16)
- [ ] `wooden_glaive_handheld.png` (32×32)
- [ ] `stone_glaive_gui.png` (16×16)
- [ ] `stone_glaive_handheld.png` (32×32)
- [ ] `iron_glaive_gui.png` (16×16)
- [ ] `iron_glaive_handheld.png` (32×32)
- [ ] `golden_glaive_gui.png` (16×16)
- [ ] `golden_glaive_handheld.png` (32×32)
- [ ] `diamond_glaive_gui.png` (16×16)
- [ ] `diamond_glaive_handheld.png` (32×32)
- [ ] `netherite_glaive_gui.png` (16×16)
- [ ] `netherite_glaive_handheld.png` (32×32)
- [ ] `wooden_spear_gui.png` (16×16)
- [ ] `wooden_spear_handheld.png` (32×32)
- [ ] `stone_spear_gui.png` (16×16)
- [ ] `stone_spear_handheld.png` (32×32)
- [ ] `iron_spear_gui.png` (16×16)
- [ ] `iron_spear_handheld.png` (32×32)
- [ ] `golden_spear_gui.png` (16×16)
- [ ] `golden_spear_handheld.png` (32×32)
- [ ] `diamond_spear_gui.png` (16×16)
- [ ] `diamond_spear_handheld.png` (32×32)
- [ ] `netherite_spear_gui.png` (16×16)
- [ ] `netherite_spear_handheld.png` (32×32)
- [ ] `wooden_halberd_gui.png` (16×16)
- [ ] `wooden_halberd_handheld.png` (64×64)
- [ ] `stone_halberd_gui.png` (16×16)
- [ ] `stone_halberd_handheld.png` (64×64)
- [ ] `iron_halberd_gui.png` (16×16)
- [ ] `iron_halberd_handheld.png` (64×64)
- [ ] `golden_halberd_gui.png` (16×16)
- [ ] `golden_halberd_handheld.png` (64×64)
- [ ] `diamond_halberd_gui.png` (16×16)
- [ ] `diamond_halberd_handheld.png` (64×64)
- [ ] `netherite_halberd_gui.png` (16×16)
- [ ] `netherite_halberd_handheld.png` (64×64)
- [ ] `wooden_pike_gui.png` (16×16)
- [ ] `wooden_pike_handheld.png` (64×64)
- [ ] `stone_pike_gui.png` (16×16)
- [ ] `stone_pike_handheld.png` (64×64)
- [ ] `iron_pike_gui.png` (16×16)
- [ ] `iron_pike_handheld.png` (64×64)
- [ ] `golden_pike_gui.png` (16×16)
- [ ] `golden_pike_handheld.png` (64×64)
- [ ] `diamond_pike_gui.png` (16×16)
- [ ] `diamond_pike_handheld.png` (64×64)
- [ ] `netherite_pike_gui.png` (16×16)
- [ ] `netherite_pike_handheld.png` (64×64)

### Axes
- [ ] `wooden_battle_axe_gui.png` (16×16)
- [ ] `wooden_battle_axe_handheld.png` (32×32)
- [ ] `stone_battle_axe_gui.png` (16×16)
- [ ] `stone_battle_axe_handheld.png` (32×32)
- [ ] `iron_battle_axe_gui.png` (16×16)
- [ ] `iron_battle_axe_handheld.png` (32×32)
- [ ] `golden_battle_axe_gui.png` (16×16)
- [ ] `golden_battle_axe_handheld.png` (32×32)
- [ ] `diamond_battle_axe_gui.png` (16×16)
- [ ] `diamond_battle_axe_handheld.png` (32×32)
- [ ] `netherite_battle_axe_gui.png` (16×16)
- [ ] `netherite_battle_axe_handheld.png` (32×32)
- [ ] `wooden_hatchet.png` (16×16)
- [ ] `stone_hatchet.png` (16×16)
- [ ] `iron_hatchet.png` (16×16)
- [ ] `golden_hatchet.png` (16×16)
- [ ] `diamond_hatchet.png` (16×16)
- [ ] `netherite_hatchet.png` (16×16)

### Throwable
- [ ] `wooden_javelin_gui.png` (16×16)
- [ ] `wooden_javelin_handheld.png` (32×32)
- [ ] `stone_javelin_gui.png` (16×16)
- [ ] `stone_javelin_handheld.png` (32×32)
- [ ] `iron_javelin_gui.png` (16×16)
- [ ] `iron_javelin_handheld.png` (32×32)
- [ ] `golden_javelin_gui.png` (16×16)
- [ ] `golden_javelin_handheld.png` (32×32)
- [ ] `diamond_javelin_gui.png` (16×16)
- [ ] `diamond_javelin_handheld.png` (32×32)
- [ ] `netherite_javelin_gui.png` (16×16)
- [ ] `netherite_javelin_handheld.png` (32×32)

### Components
- [ ] `pole_gui.png` (16×16)
- [ ] `pole_handheld.png` (32×32)
- [ ] `handle.png` (16×16)

**Total sprite files: 183**

All textures go in `src/main/resources/assets/requiem_armory/textures/item/`.
