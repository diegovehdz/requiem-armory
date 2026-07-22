package io.github.diegovehdz.requiemarmory.weapon;

import java.util.List;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Renders a set of {@link WeaponAbilities} as tooltip lines: a gold name per ability, expanded into
 * gray descriptions while Shift is held.
 *
 * <p>Split out of {@link WeaponItem} so the same lines can be attached to <em>vanilla</em> weapons
 * (axes, swords, the trident) from an {@code ItemTooltipEvent} — see {@code client.VanillaTooltips}.
 * Client-only in practice: tooltips are only ever built on the client.</p>
 */
public final class WeaponTooltip {
    private WeaponTooltip() {}

    /**
     * Appends every ability line for {@code abilities}, followed by the "hold Shift" hint.
     *
     * @param throwDamageBonus added to the advertised throw damage — the material tier bonus for our
     *                         weapons (throw damage scales with material), {@code 0} for the vanilla
     *                         trident, whose 8 damage is flat.
     */
    public static void append(List<Component> tooltip, WeaponAbilities abilities, float throwDamageBonus) {
        boolean shift = Screen.hasShiftDown();
        boolean any = false;

        if (abilities.isThrowable()) {
            tooltip.add(name("throwable"));
            if (shift) {
                tooltip.add(desc("throwable.desc.damage", fmt(abilities.throwDamage + throwDamageBonus)));
                tooltip.add(desc("throwable.desc.force", fmt(abilities.throwPower)));
                tooltip.add(desc("throwable.desc.charge", fmt(abilities.throwChargeTicks / 20.0f)));
            }
            any = true;
        }
        if (abilities.isTwoHanded()) {
            tooltip.add(name("two_handed"));
            if (shift) tooltip.add(desc("two_handed.desc"));
            any = true;
        }
        if (abilities.versatile) {
            tooltip.add(name("versatile"));
            if (shift) tooltip.add(desc("versatile.desc"));
            any = true;
        }
        if (abilities.breach) {
            tooltip.add(name("breach"));
            if (shift) tooltip.add(desc("breach.desc"));
            any = true;
        }
        if (abilities.hasArmorPierce()) {
            tooltip.add(name("armor_piercing"));
            if (shift) {
                if (abilities.armorPierceChance >= 1.0f) {
                    tooltip.add(desc("armor_piercing.desc", fmt(abilities.armorPierceAmount)));
                } else {
                    tooltip.add(desc("armor_piercing.desc.chance",
                            Math.round(abilities.armorPierceChance * 100) + "%", fmt(abilities.armorPierceAmount)));
                }
            }
            any = true;
        }
        if (abilities.hasUnarmoredBonus()) {
            tooltip.add(name("unarmored_bonus"));
            if (shift) tooltip.add(desc("unarmored_bonus.desc", fmt(abilities.unarmoredBonus)));
            any = true;
        }
        if (abilities.hasQuickStrike()) {
            tooltip.add(name("quick_strike"));
            if (shift) tooltip.add(desc("quick_strike.desc", fmt((abilities.invincibilityTicks - 10) / 20.0f)));
            any = true;
        }
        if (abilities.hasSlowStrike()) {
            tooltip.add(name("slow_strike"));
            if (shift) tooltip.add(desc("slow_strike.desc", fmt((abilities.invincibilityTicks - 10) / 20.0f)));
            any = true;
        }
        if (abilities.canSweep) {
            tooltip.add(name("sweeping"));
            if (shift) {
                // A plain sweep (sword-sized, no bonus damage) has nothing to quantify, so describe it.
                if (abilities.sweepDamage <= 0.0f && abilities.sweepRadius == 1.0f) {
                    tooltip.add(desc("sweeping.desc"));
                }
                if (abilities.sweepDamage > 0.0f) tooltip.add(desc("sweeping.desc.damage", fmt(abilities.sweepDamage)));
                if (abilities.sweepRadius != 1.0f) tooltip.add(desc("sweeping.desc.radius", fmt(abilities.sweepRadius)));
            }
            any = true;
        }

        if (any && !shift) {
            tooltip.add(Component.translatable("tooltip." + RequiemArmory.MOD_ID + ".hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    /** Gold ability name line. */
    public static Component name(String key) {
        return Component.translatable("tooltip." + RequiemArmory.MOD_ID + "." + key).withStyle(ChatFormatting.GOLD);
    }

    /** Gray, plain-text description line shown when Shift is held. */
    public static Component desc(String key, Object... args) {
        return Component.translatable("tooltip." + RequiemArmory.MOD_ID + "." + key, args)
                .withStyle(ChatFormatting.GRAY);
    }

    /** Formats a float, dropping a trailing ".0" for whole numbers. */
    public static String fmt(float value) {
        return value == Math.rint(value) ? String.valueOf((int) value) : String.valueOf(value);
    }
}
