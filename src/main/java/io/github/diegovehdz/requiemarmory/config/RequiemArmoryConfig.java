package io.github.diegovehdz.requiemarmory.config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.diegovehdz.requiemarmory.ranged.RangedTier;
import io.github.diegovehdz.requiemarmory.ranged.RangedType;
import io.github.diegovehdz.requiemarmory.weapon.WeaponMaterial;
import io.github.diegovehdz.requiemarmory.weapon.WeaponType;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The mod's config. Two files:
 *
 * <ul>
 *   <li><b>client</b> ({@code requiem_armory-client.toml}) — purely cosmetic tooltip switches.</li>
 *   <li><b>common</b> ({@code requiem_armory-common.toml}) — gameplay: which weapons exist and how the
 *       vanilla axe retune behaves. Read by the creative tab and by the
 *       {@link WeaponEnabledCondition} that gates recipes.</li>
 * </ul>
 *
 * <p><b>What "disabling" a weapon means:</b> items must stay registered — removing a registry entry
 * breaks any save that contains one — so a disabled weapon is hidden from the creative tab and loses
 * its recipes. Copies already in a world keep working. Recipe changes need a {@code /reload} (or a
 * world rejoin) to take effect, since datapack conditions are evaluated at load time.</p>
 */
public final class RequiemArmoryConfig {
    private RequiemArmoryConfig() {}

    // ------------------------------------------------------------------ client

    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    public static final class Client {
        /** Master switch for the gold ability lines on this mod's own weapons. */
        public final ModConfigSpec.ConfigValue<Boolean> showAbilityTooltips;
        /** The ability lines this mod adds to vanilla axes, swords, the trident and the bow/crossbow. */
        public final ModConfigSpec.ConfigValue<Boolean> showVanillaTooltips;
        /** Draw/charge, range, damage and consistency lines on bows and crossbows. */
        public final ModConfigSpec.ConfigValue<Boolean> showRangedTooltips;

        private Client(ModConfigSpec.Builder builder) {
            builder.comment("Tooltip display. Purely cosmetic — none of this changes combat.").push("tooltips");
            showAbilityTooltips = builder
                    .comment("Show ability lines (Two-Handed, Breach, Armor Piercing, ...) on this mod's weapons.")
                    .define("showAbilityTooltips", true);
            showVanillaTooltips = builder
                    .comment("Also describe VANILLA weapons in the same format:",
                            "axes as Versatile + Breach, swords as Sweeping, the trident as Throwable,",
                            "and the bow/crossbow with their ranged stats.")
                    .define("showVanillaTooltips", true);
            showRangedTooltips = builder
                    .comment("Show draw time, range, damage and consistency on bows and crossbows.")
                    .define("showRangedTooltips", true);
            builder.pop();
        }
    }

    static {
        var pair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = pair.getLeft();
        CLIENT_SPEC = pair.getRight();
    }

    // ------------------------------------------------------------------ common

    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    public static final class Common {
        public final ModConfigSpec.ConfigValue<Boolean> retuneVanillaAxes;
        public final ModConfigSpec.ConfigValue<Double> vanillaAxeDamagePenalty;
        public final ModConfigSpec.ConfigValue<Double> vanillaAxeSpeedBonus;

        public final ModConfigSpec.ConfigValue<Boolean> armMobs;
        public final ModConfigSpec.ConfigValue<Double> armMobsChanceMultiplier;
        public final ModConfigSpec.ConfigValue<Boolean> weaponsInLoot;
        public final ModConfigSpec.ConfigValue<Double> weaponsInLootChance;
        public final ModConfigSpec.ConfigValue<Boolean> weaponsInTrades;

        /** One entry per registered weapon, keyed by registry path (e.g. {@code "iron_warhammer"}). */
        private final Map<String, Toggle> weapons = new LinkedHashMap<>();

        /** The pair of switches that decide a weapon's fate: its whole shape, and that single item. */
        private record Toggle(ModConfigSpec.ConfigValue<Boolean> shape, ModConfigSpec.ConfigValue<Boolean> item) {
            boolean on() { return shape.get() && item.get(); }
        }

        private Common(ModConfigSpec.Builder builder) {
            builder.comment("How this mod retunes VANILLA weapons to fit its balance chart.").push("vanillaBalance");
            retuneVanillaAxes = builder
                    .comment("Retune vanilla axes so they stay the DPS ceiling for heavy weapons",
                            "without being clunky. Turn off to leave vanilla axes completely alone.")
                    .define("retuneVanillaAxes", true);
            vanillaAxeDamagePenalty = builder
                    .comment("Attack damage subtracted from every vanilla axe.")
                    .defineInRange("damagePenalty", 1.0, 0.0, 10.0);
            vanillaAxeSpeedBonus = builder
                    .comment("Attack speed added to every vanilla axe (attacks per second).")
                    .defineInRange("speedBonus", 0.2, 0.0, 4.0);
            builder.pop();

            builder.comment("How this mod's weapons turn up in the world.").push("world");
            armMobs = builder
                    .comment("Let a small share of newly spawned mobs carry one of these weapons:",
                            "zombies and vindicators (stone/iron), skeletons (bows), pillagers (crossbows),",
                            "wither skeletons (stone polearms), piglins (gold) and brutes (heavy gold).",
                            "The chance scales with the chunk's difficulty, like vanilla's own armed mobs.")
                    .define("armMobs", true);
            armMobsChanceMultiplier = builder
                    .comment("Scales those chances. 1.0 keeps the tuned defaults (5% for common mobs,",
                            "10% for wither skeletons and brutes, before the difficulty multiplier).")
                    .defineInRange("armMobsChance", 1.0, 0.0, 10.0);
            weaponsInLoot = builder
                    .comment("Swap vanilla weapons found in chest loot for one of this mod's equivalents",
                            "at the same material — an iron sword may arrive as an iron katana.")
                    .define("weaponsInLoot", true);
            weaponsInLootChance = builder
                    .comment("Chance for any one such vanilla weapon in loot to be swapped.")
                    .defineInRange("weaponsInLootChance", 0.5, 0.0, 1.0);
            weaponsInTrades = builder
                    .comment("Let weaponsmiths, toolsmiths and fletchers offer these weapons alongside",
                            "the vanilla ones they stand in for.")
                    .define("weaponsInTrades", true);
            builder.pop();

            // One subsection per weapon shape, each with an "enabled" switch for the whole shape and a
            // switch per material. Every option is a plain boolean, so NeoForge's config screen renders
            // the lot as toggle buttons — nothing has to be typed by hand.
            builder.comment("Which of this mod's weapons exist. Turn off a whole shape with its",
                            "'enabled' switch, or a single material below it. Disabled weapons vanish",
                            "from the creative tab and lose their recipes; already-crafted copies keep",
                            "working. Recipes follow on the next /reload or world rejoin.")
                    .push("weapons");
            for (WeaponType type : WeaponType.values()) {
                defineShape(builder, type.id, WeaponMaterial.all().size(),
                        WeaponMaterial.all().stream().map(m -> m.id).toList());
            }
            for (RangedType type : RangedType.values()) {
                // The wooden tier of a vanilla-backed shape IS the vanilla item — nothing of ours to disable.
                List<String> materials = Arrays.stream(RangedTier.values())
                        .filter(tier -> !(tier == RangedTier.WOODEN && type.vanillaWooden))
                        .map(tier -> tier.material.id)
                        .toList();
                defineShape(builder, type.id, materials.size(), materials);
            }
            builder.pop();
        }

        private void defineShape(ModConfigSpec.Builder builder, String shapeId, int count, List<String> materials) {
            builder.push(shapeId);
            ModConfigSpec.ConfigValue<Boolean> shape = builder
                    .comment("Turn off to disable all " + count + " " + shapeId.replace('_', ' ') + " variants at once.")
                    .define("enabled", true);
            for (String material : materials) {
                weapons.put(material + "_" + shapeId, new Toggle(shape, builder.define(material, true)));
            }
            builder.pop();
        }

        /** Whether the weapon at this registry path is switched on. Unknown paths (handle, pole) are. */
        private boolean isEnabled(String path) {
            Toggle toggle = weapons.get(path);
            return toggle == null || toggle.on();
        }
    }

    static {
        var pair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    // ------------------------------------------------------------------ weapon lookup

    /**
     * Whether the weapon at this registry path (e.g. {@code "iron_warhammer"}) is switched on — false
     * when either its shape or its own material toggle is off. Answers {@code true} while the config
     * is still unread, so a weapon is never lost to a load-order accident.
     */
    public static boolean isWeaponEnabled(String path) {
        return !COMMON_SPEC.isLoaded() || COMMON.isEnabled(path);
    }
}
