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

## Ranged weapons (bows & crossbows)
A **parallel** system to the melee one — ranged weapons are a different vanilla item family
(`BowItem`/`CrossbowItem`, both `ProjectileWeaponItem`), so they get their own package `ranged/`
instead of joining `WeaponType`. Uses **5 tiers** (wooden, iron, golden, diamond, netherite — *no
stone*), with **iron pinned to the vanilla reference** ("vanilla = iron").

- **`RangedType`** — the shape (bow, crossbow, longbow, heavy crossbow) with type-level base profile:
  `family (BOW/CROSSBOW)`, `vanillaWooden` (whether the wooden tier is the vanilla item),
  `baseDrawTicks` (charge ticks for crossbows), `baseVelocity`, `durabilityFactor`,
  `supportsFireworks`, `range`. **Longbow** (BOW family) and **heavy crossbow** (CROSSBOW family) reuse
  `BowWeaponItem`/`CrossbowWeaponItem` unchanged — they're just new rows with `vanillaWooden=false`, so
  all 5 tiers register as real items (no vanilla to back wooden). Longbow = slower draw, more
  velocity/range; heavy crossbow = longer charge, more velocity/range, sturdier, `supportsFireworks=false`
  (→ `ARROW_ONLY`). Adding a ranged weapon is one enum row + assets; the item classes, client
  predicates, movement and tooltips all generalise by `family`.
- **`RangedTier`** — the material progression and its per-material scaling (`drawMult`, `velocityMult`,
  `durability`, `moveModifier`, `enchantValue`, `variance`). **Single place to balance.** Gold is the
  fast/fragile/high-enchant outlier (no harder-hitting than iron); diamond/netherite are slower,
  sturdier and hit harder.
- **Damage = range.** Arrow damage is vanilla's `impactSpeed × baseDamage(2.0)`, so **`velocityMult`
  drives both range and damage** — deliberately kept on one lever so tiers step ~`+1` like vanilla
  melee (wooden 5 · iron 6 · gold 6 · diamond 7 · netherite 8 at full-charge point-blank) instead of
  compounding. No per-arrow *base-damage* code (an earlier version multiplied velocity × base ×
  consistency and hit ~16 — don't reintroduce that).
- **Consistency (`variance`).** A separate lever: `mixin/AbstractArrowMixin` (`@Redirect` on the
  `RandomSource.nextInt` inside `AbstractArrow#onHitEntity`) squeezes a full-charge shot's random crit
  bonus toward its *mean* by the firing weapon's variance (read via the `RangedWeapon` interface from
  `arrow.getWeaponItem()`), so higher tiers hit more consistently without changing average damage
  (wooden 1.0 = vanilla … netherite 0.35 = tightest). Non-ranged/vanilla weapons → 1.0, untouched.
- **Movement while drawing (`moveModifier`).** `ranged/RangedMovement` (game-bus `PlayerTickEvent.Post`)
  adds/removes a transient `MOVEMENT_SPEED` modifier (`ADD_MULTIPLIED_TOTAL`, id `ranged_draw_speed`)
  while the player is *using* a ranged weapon — on top of vanilla's ~20% draw slow. Lower tiers keep
  more speed (wooden +15%, gold +20%), higher tiers less (netherite −20%). Crossbows only slow you
  while charging (a loaded one held ready does not). Vanilla bow/crossbow resolve to the wooden value.
- **Tooltips.** `ranged/RangedTooltip` (called from each item's `appendHoverText`, Shift-gated like the
  melee weapons) shows draw/charge time, range (% of the iron reference), ~damage and the consistency
  bonus. The crossbow calls `super` afterwards so vanilla's charged-projectile line still shows.
- **`RangedStats`** — the resolved numbers for one `RangedType × RangedTier` (computed once per item).
- **`RangedMechanics`** — the tier-scaled charge curve (`powerForCharge`, a generalised
  `BowItem#getPowerForTime`). That's it — no damage manipulation.
- **`BowWeaponItem extends BowItem`** — the iron+ bows. Overrides only `releaseUsing` (custom draw
  curve + `velocity`), `getDefaultProjectileRange`, `getEnchantmentValue`. **Reuses vanilla
  `draw`/`shoot`** so ammo/infinity/multishot just work.
- **`CrossbowWeaponItem extends CrossbowItem`** — the iron+ crossbows. A crossbow's velocity is fixed
  per shot (loaded or not), so the "draw" lever is **charge time** (`chargeDurationTicks`, respecting
  Quick Charge) and range/damage is the **fixed arrow velocity retuned in `shootProjectile`**
  (fireworks keep vanilla speed). `releaseUsing` reimplements loading against the tier charge time;
  firing keeps vanilla's `CHARGED_PROJECTILES`/`use()`/`performShooting` path untouched (so the
  private sound-timing fields are left alone). `getSupportedHeldProjectiles` gates fireworks by
  `type.supportsFireworks` (for the future heavy crossbow). Crossbow shots are always crit, so the
  consistency mechanic applies to them too.

### Vanilla override = wooden tier (mixins)
The **wooden tier is the vanilla item**, retuned in place (the user's chosen "replace vanilla"):
- `mixin/BowItemMixin` injects `BowItem#releaseUsing` (HEAD, cancellable) and re-fires with wooden
  stats. Guarded by `stack.is(Items.BOW)` so modded bows — and our own tiers, which override
  `releaseUsing` — are untouched. Extends `ProjectileWeaponItem` only to reach inherited
  `draw`/`shoot` (its ctor is never called). Wooden's lower velocity carries both its shorter range
  and lower (~5) damage.
- Mixins are wired via `requiem_armory.mixins.json` + `[[mixins]]` in `neoforge.mods.toml`. No refmap
  (NeoForge dev/prod are Mojang-named). **`@Shadow` of a *superclass* method fails to resolve without
  a refmap — `extends` the declaring class instead.** This is the mod's first mixin.
- `mixin/CrossbowItemMixin` does the same for `minecraft:crossbow`: `@Inject` on the static
  `getChargeDuration` (guarded by `Items.CROSSBOW`) → faster wooden charge, and `@Inject` (HEAD,
  cancellable) on `performShooting` → reduced wooden arrow velocity (fireworks untouched). Because
  `getChargeDuration` is the one static every caller (use/animation/`getUseDuration`) shares,
  retuning it there keeps the client charge animation in sync **with no client-side re-registration**.
- `ranged/RangedVanillaTweaks` (mod-bus `ModifyDefaultComponentsEvent`, registered in `RequiemArmory`)
  lowers `minecraft:bow` and `minecraft:crossbow` `MAX_DAMAGE` to their wooden durability.
- Client (`RequiemArmoryClient`): registers `pulling`/`pull` predicates for every `BowWeaponItem`
  (scaled to its `drawTicks`) and `pulling`/`pull`/`charged`/`firework` for every `CrossbowWeaponItem`,
  and **re-registers `pull`/`pulling` for `Items.BOW`** so the vanilla bow's draw animation matches
  wooden's faster draw (the vanilla crossbow needs none — see the mixin note above).

### Registration & assets (ranged)
`ModItems` runs a second cross-product `RangedType × RangedTier` into the `RANGED` map, skipping the
wooden tier of vanilla-backed types (chosen by `type.family` → `BowWeaponItem`/`CrossbowWeaponItem`).
Models follow the Archeries 1.21.1 format (`item/generated` + overrides): bows use `pulling`/`pull`
→ `_pulling_0/1/2`; crossbows add `charged` → `_arrow`, `charged`+`firework` → `_firework`, and a
`_standby` base texture. Textures are **Archeries placeholders** (crossbow `iron` reuses Archeries'
`copper` art). Bows/crossbows go in `#requiem_armory:bows`/`crossbows` →
`#minecraft:enchantable/bow`|`crossbow` + `/durability`. Recipes: craft iron/gold/diamond (ingots +
string, plus stick/tripwire_hook for crossbows), netherite via `smithing_transform`.

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