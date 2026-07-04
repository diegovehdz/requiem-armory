package io.github.diegovehdz.requiemarmory.ranged;

import java.util.List;
import java.util.Locale;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Shared hover-text for the tiered ranged weapons: draw/charge time, range, approximate damage and
 * the consistency bonus, revealed on Shift like the melee weapons. Range and damage both derive from
 * velocity, but are shown separately because they read differently to a player (reach vs hit).
 */
public final class RangedTooltip {
    private RangedTooltip() {}

    public static void append(List<Component> tooltip, RangedStats stats, RangedType type) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip." + RequiemArmory.MOD_ID + ".hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        boolean crossbow = type.family == RangedType.Family.CROSSBOW;
        tooltip.add(desc(crossbow ? "ranged.charge" : "ranged.draw", seconds(stats.drawTicks())));
        tooltip.add(desc("ranged.range", Math.round(stats.velocity() / type.baseVelocity * 100.0f)));
        tooltip.add(desc("ranged.damage", Math.round(stats.velocity() * 2.0f)));

        int consistency = Math.round((1.0f - stats.variance()) * 100.0f);
        if (consistency > 0) {
            tooltip.add(desc("ranged.consistency", consistency));
        }
    }

    private static Component desc(String key, Object arg) {
        return Component.translatable("tooltip." + RequiemArmory.MOD_ID + "." + key, arg)
                .withStyle(ChatFormatting.GRAY);
    }

    private static String seconds(int ticks) {
        return String.format(Locale.ROOT, "%.2f", ticks / 20.0f);
    }
}
