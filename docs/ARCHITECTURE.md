# Requiem Armory — Architecture & Handoff
Technical reference for continuing development (e.g. in a fresh session). For the roster and abilities
see the [README](../README.md); for the art to-do see [SPRITES.md](SPRITES.md).

## Stack & build
- **Minecraft 1.21.1 · NeoForge 21.1.234 · ModDevGradle 2.0.141 · Gradle 9.2.1 · Java 21**
- Build with **JDK 21** (Gradle 9.2.1 may not run on the JDK 25 also installed). Set
  `JAVA_HOME` to `C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot` before building.
- Commands:
  ```bash
  ./gradlew build       # build the jar (build/libs/requiem_armory-<version>.jar)
  ```
- modid `requiem_armory`, base package `io.github.diegovehdz.requiemarmory`.
- Optional runtime dep: **Better Combat** (2.3.2+1.21.1). The mod works standalone too.

## How weapons are defined (the core system)
Everything is driven from two enums + one item class in `weapon/`:

- **`WeaponMaterial`** — the 6 vanilla tiers (wood…netherite). Durability, enchantability and the
  base attack-damage bonus come from the wrapped vanilla `Tier`.
- **`WeaponType`** — the "book of weapons": one row per weapon shape with `id, attackDamageModifier,
  attackSpeedStat, reachStat, separateModel, category, abilities`. This is the single place to tune
  stats. **16 types** across categories `SWORD, BLUDGEON, POLEARM, THROWN`.
  - `attackDamageModifier` (int): total melee damage = `1 + attackDamageModifier + tier bonus`
    (scales with material like a vanilla sword).
  - `attackSpeedStat` (float): **attacks per second** (DPS = damage × attackSpeedStat). Converted to
    the vanilla attribute via `stat - 4.0`.
  - `reachStat` (float): desired reach; modifier is `reachStat - 3.0`. **Skipped when Better Combat
    is loaded** (BC manages reach via `range_bonus`).
  - `separateModel` (bool): true = distinct GUI (16px) + handheld (32/64px) textures via the
    `neoforge:separate_transforms` loader; false = one 16px texture.
- **`WeaponAbilities`** (+ fluent `Builder`) — per-type combat traits: `pierce`, `unarmored`,
  `invincibility` (quick/slow strike), `sweep`, `breach`, `versatile`, `twoHanded(dmg,spd)`,
  `throwable(damage,power,charge)`.
- **`WeaponItem extends SwordItem`** — the runtime item. Builds attribute modifiers
  (`buildAttributes`), applies abilities in `hurtEnemy`, custom sweep in `getSweepHitBox`, axe/sweep
  actions in `canPerformAction`, shield-disable in `canDisableShield`, tooltips in `appendHoverText`.
  Versatile weapons get an axe `Tool` component (mine `#minecraft:mineable/axe`). Two-handed logic
  lives in `inventoryTick`.

### Registration
`registry/ModItems` builds the cross-product `WeaponType × WeaponMaterial` (96 weapons) into the
`WEAPONS` map. Item class is chosen by `type.abilities.isThrowable()` → `ThrowableWeaponItem`, else
`WeaponItem`.
`ModCreativeTabs` builds the "Armory" tab; `ModEntities` registers the `thrown_weapon` entity;
`ModDamageTypes` holds the `armor_piercing` key. `ModEvents` (game bus) nerfs vanilla axes −1 attack.

### Two-handed (unified)
Any `twoHanded(dmg,spd)` weapon takes a flat damage/speed penalty while the off-hand is occupied
(`WeaponItem.inventoryTick` swaps the cached `ATTRIBUTE_MODIFIERS` component). With Better Combat the
off-hand is disabled for two-handed weapons, so the penalty only matters standalone.

### Throwables (trident-style)
`ThrowableWeaponItem` (charge with `use`, launch in `releaseUsing`) spawns `ThrownWeaponEntity
extends AbstractArrow` — flies, sticks, and is recoverable. The item stack is synced to the client
(`EntityDataSerializers.ITEM_STACK`) so the renderer draws the real weapon. Throw damage **scales
with material** (`base + tier bonus`). Supports vanilla **Loyalty** (return-to-owner) via the
`trident_return_acceleration` effect; Loyalty is isolated to throwables through an overridden
`data/minecraft/enchantment/loyalty.json` + `#requiem_armory:enchantable/loyalty` tag.
`ThrownWeaponRenderer` orients the item tip-first (`YP(yaw-90) + ZP(pitch-45)`, scale 2× for
`separateModel` weapons). The javelin has a `throwing` model predicate (charge pose).

## Data & assets
- **Models** (`assets/requiem_armory/models/item/`): split weapons use a `separate_transforms`
  wrapper → `_gui` (item/generated, 16px) + `_handheld` (points to a shared base model:
  `handheld_2x/4x`, `handheld_pole_2x/4x`, or shape bases `greatsword/battle_axe/twinblade/spear/
  halberd`). These base models carry the display transforms (copied from Dixta's Armory).
- **Textures** are current **dev placeholders derived from Dixta's Armory** — to be replaced with
  original art (see SPRITES.md, 183 files).
- **Recipes** (`data/requiem_armory/recipe/` — singular folder; result uses `"id"`): crafting from
  material + `handle`/`pole` components; netherite via `smithing_transform`.
- **Enchantability**: all weapons are in `#minecraft:swords` (full weapon enchant set); throwables
  additionally in `#requiem_armory:enchantable/loyalty`.
- Assets/recipes/lang are currently **hand-generated via node scripts** (not datagen). Datagen is a
  possible future refactor.

## Conventions & gotchas
- 1.21 folders are singular: `data/<ns>/recipe/`, `tags/item/`, `damage_type/`. Recipe results use
  `"id"`, not `"item"`.
- Better Combat `weapon_attributes` use `range_bonus` (relative), not `attack_range`. Match a file to
  an item by filename = item path. Presets referenced as `bettercombat:<preset>` or
  `requiem_armory:presets/<type>`.
- After the first successful build, verified NeoForge/MC sources are at
  `build/moddev/artifacts/neoforge-21.1.234-sources.jar` (unzip to read exact 1.21.1 APIs).
- The full turn-by-turn history lives in the assistant's project memory
  (`requiem-armory-project`), which auto-loads in new sessions.