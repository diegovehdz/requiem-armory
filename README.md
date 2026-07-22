# Requiem Armory

A NeoForge **1.21.1** weapons mod that expands the arsenal with a wide selection of
vanilla-styled weapons such as daggers, longswords, katanas, spears, glaives, warhammers, throwables and
more. Each with its own stats, reach and abilities so that no single weapon is best in every
situation. It also **retunes vanilla's own weapons** so they sit inside the same balance chart
instead of next to it.

Inspired by, and a from-scratch reconstruction of, **[Dixta's Armory](https://modrinth.com/mod/dixtas-armory)** (originally for Forge 1.20.1).
Built to work **standalone**, with **optional [Better Combat](https://modrinth.com/mod/better-combat)**
integration for movesets, reach and attack animations.

## Status

✅ **Playable!** All weapons, ranged weapons, abilities, crafting and combat are implemented and
building against NeoForge 21.1.234. Development is currently focused on **final artwork** (the in-game
textures are temporary development placeholders) and the world-integration features below.

## Content

### Melee & throwables

**15 weapon types × 6 vanilla materials** (wood, stone, iron, gold, diamond, netherite) = **90 weapons.**

| Category | Weapons |
|---|---|
| **Swords** | dagger · rapier · saber · katana · greatsword · longsword |
| **Bludgeons** | warhammer · mace |
| **Polearms** | glaive · spear · halberd · pike |
| **Axes** | battle axe · hatchet |
| **Throwables** | dagger · javelin · hatchet |

### Ranged

**4 ranged types × 5 tiers** (wood, iron, gold, diamond, netherite — no stone). The **wooden bow and
crossbow are the vanilla items**, retuned in place; every other combination is a new item.

| Type | Character |
|---|---|
| **Bow** | the baseline — vanilla's bow is its wooden tier |
| **Longbow** | slower draw, more range and damage |
| **Crossbow** | the baseline — vanilla's crossbow is its wooden tier |
| **Heavy crossbow** | much longer charge, more range and damage, sturdier, arrows only (no fireworks) |

Range and damage ride on a **single lever** (launch velocity), so tiers step about `+1` damage like
vanilla melee instead of compounding. Higher tiers also draw slower, slow you down more while aiming,
and land their shots **more consistently** (less random spread around the same average).

### Balance

Every weapon has hand-tuned damage, attack speed and reach, balanced against two anchors: **no weapon
out-DPSes the same-tier vanilla sword**, and the heavy weapons cap out at the **vanilla axe**. Vanilla
axes are themselves retuned (−1 damage, +0.2 attack speed) so they stay that ceiling while being far
less clunky to swing. Combat overall is paced a notch slower than vanilla so hits read as weighty.

The full per-weapon chart lives in the header comment of
[`WeaponType.java`](src/main/java/io/github/diegovehdz/requiemarmory/weapon/WeaponType.java).

### Weapon abilities

- **Two-Handed** — reduced damage & speed while the off-hand is occupied (with Better Combat the
  off-hand is disabled for these, so it never triggers). Stats update live in the tooltip.
- **Armor Piercing** — bonus armor-ignoring damage on a fully charged hit (some weapons chance-based).
- **Unarmored Bonus** — bonus damage against foes wearing no armor.
- **Quick / Slow Strike** — shortens or lengthens the target's post-hit invulnerability.
- **Sweeping** — custom sweep radius and/or bonus sweep damage.
- **Breach** — disables shields on hit.
- **Versatile** — can be used to chop wood as well as a weapon.
- **Throwable** — thrown like a trident: charge, release, and pick it back up (no ammo — one throw,
  recoverable). Throw damage scales with material but stays under the vanilla trident, which remains
  the best thrower. Supports **Loyalty**.

Hold **Shift** on any weapon to expand its abilities into detailed descriptions.

### Vanilla integration

The mod treats vanilla's weapons as part of its own roster rather than leaving them outside the chart:

- **Axes** are retuned (−1 damage, +0.2 attack speed) into the heavy-weapon DPS ceiling.
- **The bow and crossbow are the wooden tier** of the ranged ladder — renamed *Wooden Bow* and
  *Wooden Crossbow*, with matching durability, draw speed and range.
- **Tooltips carry over**: axes read as *Versatile* + *Breach*, swords as *Sweeping*, the trident as
  *Throwable*, and the bow/crossbow show draw time, range and damage — same format as this mod's own.

### Crafting

Weapons are crafted from their material plus two shaft components:

- **Handle** — short grip (stick + leather).
- **Pole** — long polearm shaft (stick + handle + stick).

More powerful weapons cost more material (e.g. a warhammer needs far more than a dagger). Netherite
variants are **smithing-table upgrades** from the diamond version, like vanilla tools.

### Better Combat integration (optional)

If Better Combat is installed, every weapon gains a fitting moveset via `weapon_attributes` — native
Better Combat presets for most types, plus custom combos for the **greatsword**, **glaive**,
**longsword**, **warhammer** and **spear**. Bows and crossbows — this mod's *and* vanilla's — get
Better Combat's ranged poses, with the heavier shapes using the heavy variants.

When Better Combat is absent, the mod handles reach itself and everything still works.

### Configuration

Editable in-game from **Mods → Requiem Armory → Config** (NeoForge's built-in screen — no Cloth Config
or other dependency needed), or by hand in `config/`:

| File | Options |
|---|---|
| `requiem_armory-client.toml` | Turn off the ability tooltips, the ones added to vanilla weapons, or the ranged stat lines. |
| `requiem_armory-common.toml` | Turn the vanilla-axe retune off or tune its two numbers; disable individual weapons. |

Disabling a weapon hides it from the creative tab and removes its recipes. Entries may name a whole
shape (`warhammer`, `heavy_crossbow`) or a single item (`netherite_warhammer`):

```toml
[weapons]
    disabled = ["warhammer", "golden_dagger"]
```

Items can't be removed from the registry without breaking saves, so anything already crafted keeps
working. Recipe changes apply on the next `/reload` or world rejoin.

## Roadmap

**Done**

- [x] Project skeleton (NeoForge 1.21.1, ModDevGradle), registries, creative tab
- [x] 15 weapon types × 6 materials with per-weapon stats
- [x] Abilities: two-handed, armor piercing, unarmored bonus, quick/slow strike, sweeping, breach, versatile
- [x] Two-handed dynamic damage/speed penalty
- [x] Trident-style throwable weapons (throw + recover, no ammo) + Loyalty support
- [x] Crafting recipes (handle & pole components) + netherite smithing upgrades
- [x] Optional Better Combat movesets (incl. custom presets)
- [x] Shift-expandable tooltips, on this mod's weapons and vanilla's
- [x] Ranged category — bows, longbows, crossbows & heavy crossbows across 5 tiers
- [x] DPS balancing pass (vanilla weapons included)
- [x] Config file — tooltip switches, per-weapon disabling, vanilla-axe retune knobs

**Planned**

- [ ] **World generation & trades** — every weapon has a chance to appear in loot chests and villager
      trades in place of the vanilla item of the same tier and category
- [ ] **Armed mobs** — a small, difficulty-scaled chance for mobs to spawn wielding these weapons:
      zombies (stone/iron basics), skeletons (bows), pillagers (crossbows), vindicators (axes),
      wither skeletons (stone polearms alongside their swords), piglins and their zombified variants
      (gold), piglin brutes (heavy weapons — battle axe, mace, warhammer)
- [ ] **Crafting rebalance** — recipe costs need another pass
- [ ] Final artwork — see [`docs/SPRITES.md`](docs/SPRITES.md) (256 sprites)
- [ ] Data generation (recipes, tags, models, lang)

**Scrapped**

- ~~Two-handed moveset switching (1H ↔ 2H) with Better Combat~~ — not worth the complexity it added.

> **Note on textures:** the current in-game sprites are temporary development placeholders derived
> from Dixta's Armory and are **not** part of the published mod — final original art is in progress.

## Building

Requires a **JDK 21** build environment.

```bash
./gradlew build          # produces build/libs/requiem_armory-<version>.jar
./gradlew runClient      # launches the dev client
```

## License

[MIT](LICENSE) © diegovehdz
