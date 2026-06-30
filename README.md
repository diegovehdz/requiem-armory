# Requiem Armory

A NeoForge **1.21.1** weapons mod that expands the medieval arsenal with a wide selection of
vanilla-styled weapons — daggers, longswords, katanas, spears, glaives, throwables and more — each
with unique stats and abilities.

Inspired by, and a from-scratch reconstruction of, **Dixta's Armory** (originally Forge 1.20.1).
Built **standalone**, with **optional Better Combat** integration for movesets, reach and animations.

## Status

🚧 Early scaffold. The project compiles and runs with a base creative tab; the weapon system is
being ported incrementally.

### Roadmap

- [x] Project skeleton (NeoForge 1.21.1, ModDevGradle), base registries, creative tab
- [ ] Data-driven weapon definitions (stats + abilities)
- [ ] Weapon item + abilities: two-handed, armor piercing, unarmored bonus, quick/slow strike,
      breach, custom sweeping
- [ ] Throwable weapons (trident-like projectile, ammo system, loyalty/riptide/channeling)
- [ ] 18 weapon types × 6 vanilla materials, models/textures, recipes
- [ ] Optional Better Combat `weapon_attributes` + two-handed move-set switching
- [ ] Data generation (recipes, tags, lang, models)

## Building

Requires a **JDK 21** build environment (Minecraft 1.21.1 targets Java 21).

```bash
./gradlew build          # produces build/libs/requiem_armory-<version>.jar
./gradlew runClient      # launches the dev client
./gradlew runData        # runs data generators
```

## License

[MIT](LICENSE) © diegovehdz
