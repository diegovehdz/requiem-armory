package io.github.diegovehdz.requiemarmory.config;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.diegovehdz.requiemarmory.weapon.WeaponMaterial;
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
        public final ModConfigSpec.ConfigValue<List<? extends String>> disabledWeapons;

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

            builder.comment("Which of this mod's weapons exist in new worlds.").push("weapons");
            disabledWeapons = builder
                    .comment("Weapons to disable: hidden from the creative tab and stripped of their recipes.",
                            "Entries may be a whole type (\"warhammer\", \"heavy_crossbow\") or a single item",
                            "(\"netherite_warhammer\"). Already-crafted copies keep working.",
                            "Requires a /reload or world rejoin for the recipe side to apply.",
                            "Example: [\"warhammer\", \"golden_dagger\"]")
                    .defineListAllowEmpty("disabled", List.of(), () -> "", RequiemArmoryConfig::isValidWeaponEntry);
            builder.pop();
        }
    }

    static {
        var pair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    // ------------------------------------------------------------------ disabled-weapon lookup

    /** Parsed {@link Common#disabledWeapons}, rebuilt whenever the config (re)loads. */
    private static volatile Set<String> disabled = Set.of();

    private static boolean isValidWeaponEntry(Object value) {
        return value instanceof String s && !s.isBlank();
    }

    /** Re-reads the disabled list into the lookup set. Called from the config load/reload events. */
    public static void refreshDisabledWeapons() {
        disabled = COMMON_SPEC.isLoaded()
                ? COMMON.disabledWeapons.get().stream()
                        .map(s -> s.trim().toLowerCase(Locale.ROOT))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toUnmodifiableSet())
                : Set.of();
    }

    /**
     * Whether a weapon is enabled, by registry path (e.g. {@code "iron_warhammer"}). Matches either the
     * whole name or the shape with its material prefix stripped, so {@code "warhammer"} disables all six
     * materials at once. The prefix is stripped exactly rather than by suffix-matching, so an entry of
     * {@code "axe"} does not accidentally take out every {@code battle_axe}.
     */
    public static boolean isWeaponEnabled(String path) {
        Set<String> off = disabled;
        if (off.isEmpty()) {
            return true;
        }
        String name = path.toLowerCase(Locale.ROOT);
        if (off.contains(name)) {
            return false;
        }
        for (WeaponMaterial material : WeaponMaterial.values()) {
            String prefix = material.id + "_";
            if (name.startsWith(prefix)) {
                return !off.contains(name.substring(prefix.length()));
            }
        }
        return true;
    }
}
