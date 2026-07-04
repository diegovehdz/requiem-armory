# Requiem Armory

A NeoForge **1.21.1** weapons mod that expands the arsenal with a wide selection of
vanilla-styled weapons such as daggers, longswords, katanas, spears, glaives, warhammers, throwables and
more. Each with its own stats, reach and abilities so that no single weapon is best in every
situation.

Inspired by, and a from-scratch reconstruction of, **[Dixta's Armory](https://modrinth.com/mod/dixtas-armory)** (originally for Forge 1.20.1).
Built to work **standalone**, with **optional [Better Combat](https://modrinth.com/mod/better-combat)**
integration for movesets, reach and attack animations.

## Status

✅ **Playable!** All weapons, abilities, crafting and combat are implemented and building against
NeoForge 21.1.234. Development is currently focused on **final artwork** (the in-game textures are
temporary development placeholders) and a few remaining systems.

## Content

**16 weapon types × 6 vanilla materials** (wood, stone, iron, gold, diamond, netherite) = **96 weapons.**

| Category | Weapons |
|---|---|
| **Swords** | dagger · rapier · saber · katana · greatsword · longsword · twinblade |
| **Bludgeons** | warhammer · mace |
| **Polearms** | glaive · spear · halberd · pike |
| **Axes** | battle axe · hatchet |
| **Throwables** | dagger · javelin · hatchet |

Each weapon has hand-tuned damage, attack speed and reach, balanced so DPS stays in a fair band
while trade-offs (reach, speed, two-handedness) give every weapon a distinct role.

### Weapon abilities

- **Two-Handed** — reduced damage & speed while the off-hand is occupied (with Better Combat the
  off-hand is disabled for these, so it never triggers). Stats update live in the tooltip. The katana
  additionally swaps between one- and two-handed movesets based on the off-hand.
- **Armor Piercing** — bonus armor-ignoring damage on a fully charged hit (some weapons chance-based).
- **Unarmored Bonus** — bonus damage against foes wearing no armor.
- **Quick / Slow Strike** — shortens or lengthens the target's post-hit invulnerability.
- **Sweeping** — custom sweep radius and/or bonus sweep damage.
- **Breach** — disables shields on hit.
- **Versatile** — usable as an axe as well as a weapon.
- **Throwable** — thrown like a trident: charge, release, and pick it back up (no ammo — one throw,
  recoverable).

Hold **Shift** on any weapon to expand its abilities into detailed descriptions.

### Crafting

Weapons are crafted from their material plus two shaft components:

- **Handle** — short grip (stick + leather).
- **Pole** — long polearm shaft (stick + handle + stick).

More powerful weapons cost more material (e.g. a warhammer needs far more than a dagger). Netherite
variants are **smithing-table upgrades** from the diamond version, like vanilla tools.

### Better Combat integration (optional)

If Better Combat is installed, every weapon gains a fitting moveset via `weapon_attributes`. Native
Better Combat presets for most types, plus faithful ports of Dixta's custom movesets for the
greatsword, twinblade, glaive, katana and battle axe. When Better Combat is absent, the mod handles
reach itself and everything still works.

## Roadmap

- [x] Project skeleton (NeoForge 1.21.1, ModDevGradle), registries, creative tab
- [x] 17 weapon types × 6 materials with per-weapon stats
- [x] Abilities: two-handed, armor piercing, unarmored bonus, quick/slow strike, sweeping, breach, versatile
- [x] Two-handed dynamic damage/speed penalty
- [x] Trident-style throwable weapons (throw + recover, no ammo)
- [x] Crafting recipes (handle & pole components) + netherite smithing upgrades
- [x] Optional Better Combat movesets (incl. Dixta's custom presets)
- [x] Shift-expandable tooltips
- [x] DPS balancing pass
- [ ] Final artwork — see [`docs/SPRITES.md`](docs/SPRITES.md) (189 sprites)
- [ ] Two-handed moveset switching (1H ↔ 2H) with Better Combat
- [ ] Ranged category (reinforced bows & crossbows variants / rework)
- [ ] Optional Loyalty for throwables
- [ ] Data generation (recipes, tags, models, lang)

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
