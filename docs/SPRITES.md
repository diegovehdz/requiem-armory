# Requiem Armory — Sprite Checklist

All item textures the mod needs. **7 vanilla materials** each: `wooden, stone, copper, iron, golden, diamond, netherite`, plus the two optional metals `silver, steel`.
(Copper, silver and steel currently reuse the iron art as placeholders and need their own pass.)

**Legend**
- **ICON** = flat inventory icon, always **16×16** (`<material>_<type>_icon.png`).
- **HAND** = in-hand / flying model texture (`<material>_<type>_handheld.png`).
- **SINGLE** = one texture for both inventory and in-hand (small weapons), **16×16** (`<material>_<type>.png`).

> **Throwable weapons:** the dagger, hatchet and javelin are all throwable (they reuse their own
> textures when flying — no extra sprite). The dagger and hatchet stay in Swords/Axes visually.

## Swords

| Weapon | Textures per material | Size | Files (×7) |
|---|---|---|---|
| dagger | `<mat>_dagger.png` (SINGLE) | 16×16 | 7 |
| rapier | `<mat>_rapier_icon.png` + `<mat>_rapier_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| saber | `<mat>_saber_icon.png` + `<mat>_saber_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| katana | `<mat>_katana_icon.png` + `<mat>_katana_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| greatsword | `<mat>_greatsword_icon.png` + `<mat>_greatsword_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| longsword | `<mat>_longsword_icon.png` + `<mat>_longsword_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |

## Bludgeons

| Weapon | Textures per material | Size | Files (×7) |
|---|---|---|---|
| warhammer | `<mat>_warhammer_icon.png` + `<mat>_warhammer_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| mace | `<mat>_mace_icon.png` + `<mat>_mace_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |

## Polearms

| Weapon | Textures per material | Size | Files (×7) |
|---|---|---|---|
| glaive | `<mat>_glaive_icon.png` + `<mat>_glaive_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| spear | `<mat>_spear_icon.png` + `<mat>_spear_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| halberd | `<mat>_halberd_icon.png` + `<mat>_halberd_handheld.png` | ICON 16×16 · HAND 64×64 | 14 |
| pike | `<mat>_pike_icon.png` + `<mat>_pike_handheld.png` | ICON 16×16 · HAND 64×64 | 14 |
| scythe | `<mat>_scythe_icon.png` + `<mat>_scythe_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |

## Axes

| Weapon | Textures per material | Size | Files (×7) |
|---|---|---|---|
| battle_axe | `<mat>_battle_axe_icon.png` + `<mat>_battle_axe_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |
| hatchet | `<mat>_hatchet.png` (SINGLE) | 16×16 | 7 |

## Throwable

| Weapon | Textures per material | Size | Files (×7) |
|---|---|---|---|
| javelin | `<mat>_javelin_icon.png` + `<mat>_javelin_handheld.png` | ICON 16×16 · HAND 32×32 | 14 |

## Components / other

| Item | Textures | Size |
|---|---|---|
| pole (pole handle) | `pole_icon.png` + `pole_handheld.png` | 16×16 · 32×32 |
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

## Not yet in the mod (future)

- **Shields**: not in the roster yet.

---

# Full file checklist

### Swords
- [ ] `wooden_dagger.png` (16×16)
- [ ] `stone_dagger.png` (16×16)
- [ ] `iron_dagger.png` (16×16)
- [ ] `copper_dagger.png` (16×16)
- [ ] `silver_dagger.png` (16×16)
- [ ] `steel_dagger.png` (16×16)
- [ ] `golden_dagger.png` (16×16)
- [ ] `diamond_dagger.png` (16×16)
- [ ] `netherite_dagger.png` (16×16)
- [ ] `wooden_rapier_icon.png` (16×16)
- [ ] `wooden_rapier_handheld.png` (32×32)
- [ ] `stone_rapier_icon.png` (16×16)
- [ ] `stone_rapier_handheld.png` (32×32)
- [ ] `iron_rapier_icon.png` (16×16)
- [ ] `copper_rapier_icon.png` (16×16)
- [ ] `silver_rapier_icon.png` (16×16)
- [ ] `steel_rapier_icon.png` (16×16)
- [ ] `iron_rapier_handheld.png` (32×32)
- [ ] `copper_rapier_handheld.png` (32×32)
- [ ] `silver_rapier_handheld.png` (32×32)
- [ ] `steel_rapier_handheld.png` (32×32)
- [ ] `golden_rapier_icon.png` (16×16)
- [ ] `golden_rapier_handheld.png` (32×32)
- [ ] `diamond_rapier_icon.png` (16×16)
- [ ] `diamond_rapier_handheld.png` (32×32)
- [ ] `netherite_rapier_icon.png` (16×16)
- [ ] `netherite_rapier_handheld.png` (32×32)
- [ ] `wooden_saber_icon.png` (16×16)
- [ ] `wooden_saber_handheld.png` (32×32)
- [ ] `stone_saber_icon.png` (16×16)
- [ ] `stone_saber_handheld.png` (32×32)
- [ ] `iron_saber_icon.png` (16×16)
- [ ] `copper_saber_icon.png` (16×16)
- [ ] `silver_saber_icon.png` (16×16)
- [ ] `steel_saber_icon.png` (16×16)
- [ ] `iron_saber_handheld.png` (32×32)
- [ ] `copper_saber_handheld.png` (32×32)
- [ ] `silver_saber_handheld.png` (32×32)
- [ ] `steel_saber_handheld.png` (32×32)
- [ ] `golden_saber_icon.png` (16×16)
- [ ] `golden_saber_handheld.png` (32×32)
- [ ] `diamond_saber_icon.png` (16×16)
- [ ] `diamond_saber_handheld.png` (32×32)
- [ ] `netherite_saber_icon.png` (16×16)
- [ ] `netherite_saber_handheld.png` (32×32)
- [ ] `wooden_katana_icon.png` (16×16)
- [ ] `wooden_katana_handheld.png` (32×32)
- [ ] `stone_katana_icon.png` (16×16)
- [ ] `stone_katana_handheld.png` (32×32)
- [ ] `iron_katana_icon.png` (16×16)
- [ ] `copper_katana_icon.png` (16×16)
- [ ] `silver_katana_icon.png` (16×16)
- [ ] `steel_katana_icon.png` (16×16)
- [ ] `iron_katana_handheld.png` (32×32)
- [ ] `copper_katana_handheld.png` (32×32)
- [ ] `silver_katana_handheld.png` (32×32)
- [ ] `steel_katana_handheld.png` (32×32)
- [ ] `golden_katana_icon.png` (16×16)
- [ ] `golden_katana_handheld.png` (32×32)
- [ ] `diamond_katana_icon.png` (16×16)
- [ ] `diamond_katana_handheld.png` (32×32)
- [ ] `netherite_katana_icon.png` (16×16)
- [ ] `netherite_katana_handheld.png` (32×32)
- [ ] `wooden_greatsword_icon.png` (16×16)
- [ ] `wooden_greatsword_handheld.png` (32×32)
- [ ] `stone_greatsword_icon.png` (16×16)
- [ ] `stone_greatsword_handheld.png` (32×32)
- [ ] `iron_greatsword_icon.png` (16×16)
- [ ] `copper_greatsword_icon.png` (16×16)
- [ ] `silver_greatsword_icon.png` (16×16)
- [ ] `steel_greatsword_icon.png` (16×16)
- [ ] `iron_greatsword_handheld.png` (32×32)
- [ ] `copper_greatsword_handheld.png` (32×32)
- [ ] `silver_greatsword_handheld.png` (32×32)
- [ ] `steel_greatsword_handheld.png` (32×32)
- [ ] `golden_greatsword_icon.png` (16×16)
- [ ] `golden_greatsword_handheld.png` (32×32)
- [ ] `diamond_greatsword_icon.png` (16×16)
- [ ] `diamond_greatsword_handheld.png` (32×32)
- [ ] `netherite_greatsword_icon.png` (16×16)
- [ ] `netherite_greatsword_handheld.png` (32×32)
- [ ] `wooden_longsword_icon.png` (16×16)
- [ ] `wooden_longsword_handheld.png` (32×32)
- [ ] `stone_longsword_icon.png` (16×16)
- [ ] `stone_longsword_handheld.png` (32×32)
- [ ] `iron_longsword_icon.png` (16×16)
- [ ] `copper_longsword_icon.png` (16×16)
- [ ] `silver_longsword_icon.png` (16×16)
- [ ] `steel_longsword_icon.png` (16×16)
- [ ] `iron_longsword_handheld.png` (32×32)
- [ ] `copper_longsword_handheld.png` (32×32)
- [ ] `silver_longsword_handheld.png` (32×32)
- [ ] `steel_longsword_handheld.png` (32×32)
- [ ] `golden_longsword_icon.png` (16×16)
- [ ] `golden_longsword_handheld.png` (32×32)
- [ ] `diamond_longsword_icon.png` (16×16)
- [ ] `diamond_longsword_handheld.png` (32×32)
- [ ] `netherite_longsword_icon.png` (16×16)
- [ ] `netherite_longsword_handheld.png` (32×32)

### Bludgeons
- [ ] `wooden_warhammer_icon.png` (16×16)
- [ ] `wooden_warhammer_handheld.png` (32×32)
- [ ] `stone_warhammer_icon.png` (16×16)
- [ ] `stone_warhammer_handheld.png` (32×32)
- [ ] `iron_warhammer_icon.png` (16×16)
- [ ] `copper_warhammer_icon.png` (16×16)
- [ ] `silver_warhammer_icon.png` (16×16)
- [ ] `steel_warhammer_icon.png` (16×16)
- [ ] `iron_warhammer_handheld.png` (32×32)
- [ ] `copper_warhammer_handheld.png` (32×32)
- [ ] `silver_warhammer_handheld.png` (32×32)
- [ ] `steel_warhammer_handheld.png` (32×32)
- [ ] `golden_warhammer_icon.png` (16×16)
- [ ] `golden_warhammer_handheld.png` (32×32)
- [ ] `diamond_warhammer_icon.png` (16×16)
- [ ] `diamond_warhammer_handheld.png` (32×32)
- [ ] `netherite_warhammer_icon.png` (16×16)
- [ ] `netherite_warhammer_handheld.png` (32×32)
- [ ] `wooden_mace_icon.png` (16×16)
- [ ] `wooden_mace_handheld.png` (32×32)
- [ ] `stone_mace_icon.png` (16×16)
- [ ] `stone_mace_handheld.png` (32×32)
- [ ] `iron_mace_icon.png` (16×16)
- [ ] `copper_mace_icon.png` (16×16)
- [ ] `silver_mace_icon.png` (16×16)
- [ ] `steel_mace_icon.png` (16×16)
- [ ] `iron_mace_handheld.png` (32×32)
- [ ] `copper_mace_handheld.png` (32×32)
- [ ] `silver_mace_handheld.png` (32×32)
- [ ] `steel_mace_handheld.png` (32×32)
- [ ] `golden_mace_icon.png` (16×16)
- [ ] `golden_mace_handheld.png` (32×32)
- [ ] `diamond_mace_icon.png` (16×16)
- [ ] `diamond_mace_handheld.png` (32×32)
- [ ] `netherite_mace_icon.png` (16×16)
- [ ] `netherite_mace_handheld.png` (32×32)

### Polearms
- [ ] `wooden_glaive_icon.png` (16×16)
- [ ] `wooden_glaive_handheld.png` (32×32)
- [ ] `stone_glaive_icon.png` (16×16)
- [ ] `stone_glaive_handheld.png` (32×32)
- [ ] `iron_glaive_icon.png` (16×16)
- [ ] `copper_glaive_icon.png` (16×16)
- [ ] `silver_glaive_icon.png` (16×16)
- [ ] `steel_glaive_icon.png` (16×16)
- [ ] `iron_glaive_handheld.png` (32×32)
- [ ] `copper_glaive_handheld.png` (32×32)
- [ ] `silver_glaive_handheld.png` (32×32)
- [ ] `steel_glaive_handheld.png` (32×32)
- [ ] `golden_glaive_icon.png` (16×16)
- [ ] `golden_glaive_handheld.png` (32×32)
- [ ] `diamond_glaive_icon.png` (16×16)
- [ ] `diamond_glaive_handheld.png` (32×32)
- [ ] `netherite_glaive_icon.png` (16×16)
- [ ] `netherite_glaive_handheld.png` (32×32)
- [ ] `wooden_spear_icon.png` (16×16)
- [ ] `wooden_spear_handheld.png` (32×32)
- [ ] `stone_spear_icon.png` (16×16)
- [ ] `stone_spear_handheld.png` (32×32)
- [ ] `iron_spear_icon.png` (16×16)
- [ ] `copper_spear_icon.png` (16×16)
- [ ] `silver_spear_icon.png` (16×16)
- [ ] `steel_spear_icon.png` (16×16)
- [ ] `iron_spear_handheld.png` (32×32)
- [ ] `copper_spear_handheld.png` (32×32)
- [ ] `silver_spear_handheld.png` (32×32)
- [ ] `steel_spear_handheld.png` (32×32)
- [ ] `golden_spear_icon.png` (16×16)
- [ ] `golden_spear_handheld.png` (32×32)
- [ ] `diamond_spear_icon.png` (16×16)
- [ ] `diamond_spear_handheld.png` (32×32)
- [ ] `netherite_spear_icon.png` (16×16)
- [ ] `netherite_spear_handheld.png` (32×32)
- [ ] `wooden_halberd_icon.png` (16×16)
- [ ] `wooden_halberd_handheld.png` (64×64)
- [ ] `stone_halberd_icon.png` (16×16)
- [ ] `stone_halberd_handheld.png` (64×64)
- [ ] `iron_halberd_icon.png` (16×16)
- [ ] `copper_halberd_icon.png` (16×16)
- [ ] `silver_halberd_icon.png` (16×16)
- [ ] `steel_halberd_icon.png` (16×16)
- [ ] `iron_halberd_handheld.png` (64×64)
- [ ] `copper_halberd_handheld.png` (64×64)
- [ ] `silver_halberd_handheld.png` (64×64)
- [ ] `steel_halberd_handheld.png` (64×64)
- [ ] `golden_halberd_icon.png` (16×16)
- [ ] `golden_halberd_handheld.png` (64×64)
- [ ] `diamond_halberd_icon.png` (16×16)
- [ ] `diamond_halberd_handheld.png` (64×64)
- [ ] `netherite_halberd_icon.png` (16×16)
- [ ] `netherite_halberd_handheld.png` (64×64)
- [ ] `wooden_pike_icon.png` (16×16)
- [ ] `wooden_pike_handheld.png` (64×64)
- [ ] `stone_pike_icon.png` (16×16)
- [ ] `stone_pike_handheld.png` (64×64)
- [ ] `iron_pike_icon.png` (16×16)
- [ ] `copper_pike_icon.png` (16×16)
- [ ] `silver_pike_icon.png` (16×16)
- [ ] `steel_pike_icon.png` (16×16)
- [ ] `iron_pike_handheld.png` (64×64)
- [ ] `copper_pike_handheld.png` (64×64)
- [ ] `silver_pike_handheld.png` (64×64)
- [ ] `steel_pike_handheld.png` (64×64)
- [ ] `golden_pike_icon.png` (16×16)
- [ ] `golden_pike_handheld.png` (64×64)
- [ ] `diamond_pike_icon.png` (16×16)
- [ ] `diamond_pike_handheld.png` (64×64)
- [ ] `netherite_pike_icon.png` (16×16)
- [ ] `netherite_pike_handheld.png` (64×64)
- [ ] `wooden_scythe_icon.png` (16×16)
- [ ] `wooden_scythe_handheld.png` (32×32)
- [ ] `stone_scythe_icon.png` (16×16)
- [ ] `stone_scythe_handheld.png` (32×32)
- [ ] `iron_scythe_icon.png` (16×16)
- [ ] `copper_scythe_icon.png` (16×16)
- [ ] `silver_scythe_icon.png` (16×16)
- [ ] `steel_scythe_icon.png` (16×16)
- [ ] `iron_scythe_handheld.png` (32×32)
- [ ] `copper_scythe_handheld.png` (32×32)
- [ ] `silver_scythe_handheld.png` (32×32)
- [ ] `steel_scythe_handheld.png` (32×32)
- [ ] `golden_scythe_icon.png` (16×16)
- [ ] `golden_scythe_handheld.png` (32×32)
- [ ] `diamond_scythe_icon.png` (16×16)
- [ ] `diamond_scythe_handheld.png` (32×32)
- [ ] `netherite_scythe_icon.png` (16×16)
- [ ] `netherite_scythe_handheld.png` (32×32)

### Axes
- [ ] `wooden_battle_axe_icon.png` (16×16)
- [ ] `wooden_battle_axe_handheld.png` (32×32)
- [ ] `stone_battle_axe_icon.png` (16×16)
- [ ] `stone_battle_axe_handheld.png` (32×32)
- [ ] `iron_battle_axe_icon.png` (16×16)
- [ ] `copper_battle_axe_icon.png` (16×16)
- [ ] `silver_battle_axe_icon.png` (16×16)
- [ ] `steel_battle_axe_icon.png` (16×16)
- [ ] `iron_battle_axe_handheld.png` (32×32)
- [ ] `copper_battle_axe_handheld.png` (32×32)
- [ ] `silver_battle_axe_handheld.png` (32×32)
- [ ] `steel_battle_axe_handheld.png` (32×32)
- [ ] `golden_battle_axe_icon.png` (16×16)
- [ ] `golden_battle_axe_handheld.png` (32×32)
- [ ] `diamond_battle_axe_icon.png` (16×16)
- [ ] `diamond_battle_axe_handheld.png` (32×32)
- [ ] `netherite_battle_axe_icon.png` (16×16)
- [ ] `netherite_battle_axe_handheld.png` (32×32)
- [ ] `wooden_hatchet.png` (16×16)
- [ ] `stone_hatchet.png` (16×16)
- [ ] `iron_hatchet.png` (16×16)
- [ ] `copper_hatchet.png` (16×16)
- [ ] `silver_hatchet.png` (16×16)
- [ ] `steel_hatchet.png` (16×16)
- [ ] `golden_hatchet.png` (16×16)
- [ ] `diamond_hatchet.png` (16×16)
- [ ] `netherite_hatchet.png` (16×16)

### Throwable
- [ ] `wooden_javelin_icon.png` (16×16)
- [ ] `wooden_javelin_handheld.png` (32×32)
- [ ] `stone_javelin_icon.png` (16×16)
- [ ] `stone_javelin_handheld.png` (32×32)
- [ ] `iron_javelin_icon.png` (16×16)
- [ ] `copper_javelin_icon.png` (16×16)
- [ ] `silver_javelin_icon.png` (16×16)
- [ ] `steel_javelin_icon.png` (16×16)
- [ ] `iron_javelin_handheld.png` (32×32)
- [ ] `copper_javelin_handheld.png` (32×32)
- [ ] `silver_javelin_handheld.png` (32×32)
- [ ] `steel_javelin_handheld.png` (32×32)
- [ ] `golden_javelin_icon.png` (16×16)
- [ ] `golden_javelin_handheld.png` (32×32)
- [ ] `diamond_javelin_icon.png` (16×16)
- [ ] `diamond_javelin_handheld.png` (32×32)
- [ ] `netherite_javelin_icon.png` (16×16)
- [ ] `netherite_javelin_handheld.png` (32×32)

### Components
- [ ] `pole_icon.png` (16×16)
- [ ] `pole_handheld.png` (32×32)
- [ ] `handle.png` (16×16)

**Total melee/component sprite files: 273** (plus the 85 ranged sprites above = **358**). Silver and steel add 30 each on top of the 7-material rows below.

All textures go in `src/main/resources/assets/requiem_armory/textures/item/`.
