# Reference: how other weapon mods balance

Comparison data pulled from three weapon mods, to inform Requiem Armory's own balance. Everything
below was **extracted from the actual jars** (decompiled with Vineflower / read out of their data
packs), not from wikis — so the numbers are what those mods really ship.

| Mod | Version read | Where its stats live |
|---|---|---|
| **Dixta's Armory** | 1.3.0 · 1.20.1 | `DefaultConfigs.DEFAULT_CONFIGS` — the whole balance table as an embedded JSON config |
| **Spartan Weaponry** | 3.2.1 · 1.20.1 Forge | `util/WeaponFactory` — one lambda per weapon shape |
| **Simply Swords** | 1.63.0 · 1.21.1 NeoForge | `config/WeaponAttributesConfig` + `registry/ItemsRegistry` |

Only **vanilla-tier melee** is covered. Runic tiers, unique/named weapons, oil coatings, bolt systems
and modded-metal tiers are out of scope.

---

## 1. The damage formulas

Each mod scales damage with material differently, and that choice matters more than any single number.

**Requiem Armory (ours)** — flat per-shape offset, uniform scaling:
```
damage = 1 + attackDamageModifier + tierBonus
```

**Dixta's Armory** — no formula at all. Every material × shape is a hand-written number in JSON
(180 entries). Total freedom, no consistency guarantee.

**Spartan Weaponry** — per-shape *scaling multiplier*:
```
damage = tierBonus × damageMultiplier + baseDamage
```
The multiplier is the interesting part: dagger ×1.0, longsword ×1.5, battleaxe/hammer ×2.0. **Heavy
weapons gain more from better materials than light ones do.** A netherite battle hammer pulls away
from a netherite dagger far faster than ours does. We have no equivalent lever.

**Simply Swords** — flat, and material-blind:
```
damage = 1 + int(3.0 + typeModifier) + tierBonus
```
All four materials use the same `3.0`, so a shape's *identity* is entirely `typeModifier` + speed.
Note it has **no wood or stone tier** — it starts at iron.

---

## 2. Iron-tier stats, side by side

Damage is the number shown in the tooltip; speed is attacks/second; DPS is damage × speed.
Vanilla reference: **iron sword 6 dmg × 1.6 = 9.6 DPS**, **iron axe 9 × 0.9 = 8.1**.

### Dixta's Armory
| Shape | Dmg | Speed | Reach | DPS | Traits |
|---|---|---|---|---|---|
| dagger | 3.5 | 3.5 | 1.8 | **12.25** | sweep 0.25, i-frames 5 |
| twinblade | 6.2 | 1.9 | 3.5 | **11.78** | 2H-II |
| shortsword | 5.3 | 2.2 | 2.25 | **11.66** | sweep 0.75 |
| katana | 6.1 | 1.8 | 3.25 | **10.98** | sweep 1.25 +2 dmg, 2H-I |
| longsword | 7.8 | 1.3 | 3.5 | **10.14** | sweep 2.0, 2H-II |
| stiletto | 3.6 | 2.5 | 2.0 | 9.0 | pierce 3 @25%, i-frames 7 |
| throwing_knife | 3.0 | 3.0 | 1.8 | 9.0 | throwable, i-frames 5 |
| glaive | 8.4 | 1.0 | 4.0 | 8.4 | 2H-II |
| greatsword | 6.7 | 1.2 | 3.5 | 8.04 | sweep 1.5 +4 dmg, 2H-II |
| rapier | 4.0 | 2.0 | 3.0 | 8.0 | unarmored +2.2 |
| chakram | 4.0 | 2.0 | 2.25 | 8.0 | throwable |
| battle_axe | 12.3 | 0.6 | 3.25 | 7.38 | 2H-I |
| spear | 5.0 | 1.3 | 4.2 | 6.5 | pierce 0.8, 2H-I |
| zweihander | 6.4 | 1.0 | 4.0 | 6.4 | sweep 2.5 +2 dmg, 2H-II |
| halberd | 8.2 | 0.7 | 4.5 | 5.74 | pierce 3 @50%, breach, 2H-II |
| pike | 7.0 | 0.8 | 5.0 | 5.6 | pierce 1, 2H-II |
| javelin | 4.0 | 1.2 | 4.0 | 4.8 | pierce 0.8, throwable |

### Spartan Weaponry
| Shape | Base | ×Mult | Speed | Dmg | DPS |
|---|---|---|---|---|---|
| dagger | 2.5 | 1.0 | 2.5 | 4.5 | **11.25** |
| longsword | 4.5 | 1.5 | 1.4 | 7.5 | **10.5** |
| greatsword | 4.0 | 1.5 | 1.4 | 7.0 | **9.8** |
| halberd | 5.0 | 1.5 | 1.2 | 8.0 | 9.6 |
| spear | 5.5 | 0.5 | 1.4 | 6.5 | 9.1 |
| katana | 3.5 | 0.5 | 2.0 | 4.5 | 9.0 |
| pike | 4.0 | 1.0 | 1.4 | 6.0 | 8.4 |
| quarterstaff | 3.0 | 1.5 | 1.4 | 6.0 | 8.4 |
| battleaxe | 4.0 | 2.0 | 1.0 | 8.0 | 8.0 |
| warhammer | 4.0 | 1.5 | 1.1 | 7.0 | 7.7 |
| saber | 3.5 | 0.5 | 1.6 | 4.5 | 7.2 |
| rapier | 2.0 | 0.5 | 2.4 | 3.0 | 7.2 |
| battle_hammer | 5.0 | 2.0 | 0.8 | 9.0 | 7.2 |
| flanged_mace | 3.0 | 1.5 | 1.2 | 6.0 | 7.2 |
| glaive | 4.0 | 1.5 | 1.0 | 7.0 | 7.0 |
| scythe | 5.0 | 1.0 | 1.0 | 7.0 | 7.0 |
| lance | 4.0 | 1.0 | 1.0 | 6.0 | 6.0 |

### Simply Swords
| Shape | typeMod | Speed | Dmg | DPS |
|---|---|---|---|---|
| cutlass | 0.0 | 2.0 | 6 | **12.0** |
| katana | 0.0 | 2.0 | 6 | **12.0** |
| twinblade | 0.0 | 2.0 | 6 | **12.0** |
| rapier | −1.0 | 2.2 | 5 | **11.0** |
| halberd | +3.0 | 1.2 | 9 | **10.8** |
| warglaive | 0.0 | 1.8 | 6 | **10.8** |
| claymore | +2.0 | 1.2 | 8 | 9.6 |
| longsword | 0.0 | 1.6 | 6 | 9.6 |
| scythe | +1.0 | 1.3 | 7 | 9.1 |
| glaive | 0.0 | 1.4 | 6 | 8.4 |
| greataxe | +3.0 | 0.9 | 9 | 8.1 |
| greathammer | +4.0 | 0.8 | 10 | 8.0 |
| spear | 0.0 | 1.3 | 6 | 7.8 |
| sai | −3.0 | 2.5 | 3 | 7.5 |
| chakram | −1.0 | 1.0 | 5 | 5.0 |

---

## 3. Where we sit

Ours against theirs, matching shapes, iron tier. "Eff." folds in our armour-piercing hit.

| Shape | **Ours** | Dixta | Spartan | Simply |
|---|---|---|---|---|
| dagger | **6.0** | 12.25 | 11.25 | — |
| rapier | **6.8** | 8.0 | 7.2 | 11.0 |
| saber | **8.0** | — | 7.2 | 12.0 *(cutlass)* |
| katana | **8.4** | 10.98 | 9.0 | 12.0 |
| longsword | **8.4** | 10.14 | 10.5 | 9.6 |
| greatsword | **8.0** | 8.04 | 9.8 | 9.6 *(claymore)* |
| battle axe | **7.65** | 7.38 | 8.0 | 8.1 *(greataxe)* |
| warhammer | **7.0** *(eff 8.4)* | — | 7.7 | 8.0 *(greathammer)* |
| mace | **6.9** *(eff 8.6)* | — | 7.2 *(flanged)* | — |
| glaive | **6.65** | 8.4 | 7.0 | 8.4 |
| spear | **6.0** *(eff 8.4)* | 6.5 | 9.1 | 7.8 |
| halberd | **6.4** *(eff 8.0)* | 5.74 | 9.6 | 10.8 |
| pike | **5.95** *(eff 7.65)* | 5.6 | 8.4 | — |
| javelin | **4.4** *(eff 5.5)* | 4.8 | — | — |

**The gap is not uniform.** On heavy and polearm shapes we are competitive — our halberd, pike and
battle axe beat Dixta's outright. The shortfall is concentrated in the **fast, light** shapes:

- dagger: **6.0 vs 11–12** — half
- katana: 8.4 vs 9.0–12.0
- rapier: 6.8 vs 7.2–11.0

That is a direct consequence of the "never exceed the vanilla sword" rule. All three reference mods
break it happily, and they break it *specifically* on light weapons — where high DPS is the whole
identity, paid for with low damage per hit.

### Why low per-hit damage is a real cost (and why that rule was over-cautious)

A hit sets the target's invulnerability to 20 ticks, but the "no damage at all" part of that window is
only the first **10 ticks**. So against a single target, **anything above 2.0 attacks/second is
wasted** unless the weapon shortens i-frames.

That means nominal DPS overstates fast weapons, and every mod here knows it:

- Dixta's dagger runs 3.5 att/s but sets i-frames to **5**, so all of it lands. Deliberate.
- Spartan's dagger runs 2.5 att/s with no i-frame change — its real ceiling is 4.5 × 2.0 = **9.0**, not 11.25.
- Simply's sai runs 2.5 att/s with no change — real ceiling 3 × 2.0 = **6.0**, not 7.5.

Our own quick-strike weapons are fine here (the dagger's 15-tick setting allows 4 hits/s, well above
its 2.0 speed). The takeaway is the opposite one: **we have headroom we aren't using.** Our fast
weapons could go meaningfully faster *and* keep their identity, because the i-frame floor already
stops them from becoming degenerate.

---

## 4. Crafting costs (iron tier)

| Shape | Ours | Dixta | Spartan | Simply |
|---|---|---|---|---|
| dagger | 1 ingot + handle | 1 ingot + stick | 1 ingot + handle | — |
| longsword | 3 ingots + handle | 4 ingots + stick | 4 ingots + handle | 2 ingots + stick |
| katana | 3 ingots + handle | 2 ingots + stick | 2 ingots + handle | 2 ingots + stick |
| greatsword | 4 ingots + handle | 4 ingots + stick | 6 ingots + handle | 4 nuggets + 2 ingots *(claymore)* |
| battle axe | 4 ingots + handle | 4 ingots + 3 sticks | 5 ingots + stick + handle | 3 ingots + 2 nuggets |
| warhammer | 5 ingots + handle | — | 3 ingots + handle | 3 ingots + 3 nuggets |
| spear | 1 ingot + pole | 1 ingot + stick + pole | 1 ingot + pole | 1 ingot + 2 sticks |
| halberd | 3 ingots + pole | 3 ingots + stick + pole | 4 ingots + pole | 3 ingots + 2 sticks + nugget |
| pike | 2 ingots + pole | 1 ingot + 2 poles | 1 ingot + 2 poles | — |
| glaive | 2 ingots + pole | 2 ingots + pole | 2 ingots + pole | 2 ingots + 2 sticks |

Everyone converges on the same shape language: **a haft component** (our handle/pole, Spartan's
handle/pole, Dixta's stick/pole) plus 1–6 ingots. Ours sits mid-range — **Simply is consistently
cheapest** (and uses nuggets for fine detail, which is a nice touch we could borrow for daggers).
Spartan is the most expensive.

---

## 5. Moveset approaches

| | Approach |
|---|---|
| **Dixta** | 19 hand-written Better Combat presets, one per shape, plus separate `_one_handed`/`_two_handed` variants for battle axe, katana and spear that get swapped at runtime. |
| **Spartan** | 23 `base/` presets that mostly just re-parent to stock Better Combat presets. Minimal customisation. |
| **Simply** | Per-item files, several fully hand-written with 3-attack combos and custom poses. |
| **Ours** | 5 custom presets (greatsword, glaive, spear, longsword, warhammer), rest re-parent to stock. |

We sit between Spartan and Dixta. Nothing here suggests we need more — Spartan ships a well-regarded
mod on almost no custom movesets.

---

## 6. Shapes we don't have

Discounting things we deliberately dropped (twinblade, shuriken, chakram) and out-of-scope systems:

| Shape | In | Identity worth stealing |
|---|---|---|
| **Scythe** | Spartan, Simply | Wide horizontal sweep, medium-slow. Distinct silhouette, no overlap with ours. |
| **Quarterstaff** | Spartan | Fast two-handed blunt, low damage, good reach. Fills a real gap — we have no fast polearm. |
| **Shortsword / cutlass** | Dixta, Simply | Below the saber: cheap, fast, short reach. An early-game weapon we lack. |
| **Lance** | Spartan, Dixta | Mounted-charge weapon. Big reach, punishing on foot. Needs a mounted-damage mechanic. |
| **Parrying dagger / sai** | Spartan, Simply | Defensive off-hand weapon. Would need a block/parry system — biggest new mechanic here. |
| **Battle hammer** | Spartan | Distinct from warhammer: even slower, even heavier (9 dmg @ 0.8). We could split warhammer into two. |
| **Warglaive** | Simply | Fast one-handed polearm, 1.8 att/s at sword damage. |

Best fits for a vanilla+ feel, in order: **scythe**, **quarterstaff**, **shortsword**.

---

## 7. What to change

1. **Raise the fast end.** Lift the "never beat the vanilla sword" rule for light weapons — dagger,
   rapier, saber, katana. All three references put these at 9–12 DPS at iron; we're at 6–8.4. The
   i-frame floor at 2.0 att/s is the real safety net, not the sword's DPS.
2. **Keep the heavy end.** Battle axe, warhammer, halberd and pike are already in line with, or above,
   Dixta. Don't touch them.
3. **Consider a per-shape material multiplier** (Spartan's `damageMultiplier`). It is one float per row
   in `WeaponType` and gives heavy weapons a reason to be upgraded to netherite.
4. **Per-hit damage is the honest currency.** Fast weapons should stay low-damage-per-hit; that is what
   makes them worse against armour and knockback, which is the real trade-off — not DPS on paper.
