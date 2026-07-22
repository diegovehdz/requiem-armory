package io.github.diegovehdz.requiemarmory.client;

import java.util.ArrayList;
import java.util.List;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import io.github.diegovehdz.requiemarmory.ranged.RangedStats;
import io.github.diegovehdz.requiemarmory.ranged.RangedTier;
import io.github.diegovehdz.requiemarmory.ranged.RangedTooltip;
import io.github.diegovehdz.requiemarmory.ranged.RangedType;
import io.github.diegovehdz.requiemarmory.weapon.WeaponAbilities;
import io.github.diegovehdz.requiemarmory.weapon.WeaponTooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Gives the <em>vanilla</em> weapons the same ability tooltips the mod's own weapons carry, so a
 * player comparing an iron sword against an iron saber reads both in the same language. Purely
 * cosmetic — it describes behaviour vanilla (or this mod's retuning of it) already has.
 *
 * <p>Our items render their lines from {@code appendHoverText}, which lands directly under the item
 * name; {@link ItemTooltipEvent} instead fires at the very end, so the lines are spliced back into
 * that same position.</p>
 */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID, value = Dist.CLIENT)
public final class VanillaTooltips {
    private VanillaTooltips() {}

    /** Vanilla axes chop wood and break shields — exactly the versatile + breach pair. */
    private static final WeaponAbilities AXE = WeaponAbilities.builder().versatile().breach().build();

    /** The vanilla sword's sweep is the reference every sweeping weapon in the mod is scaled against. */
    private static final WeaponAbilities SWORD = WeaponAbilities.builder().sweep().build();

    /** The trident throws for a flat 8 at force 2.5 after a 10-tick charge (vanilla {@code TridentItem}). */
    private static final WeaponAbilities TRIDENT = WeaponAbilities.builder().throwable(8.0f, 2.5f, 10).build();

    @SubscribeEvent
    static void onItemTooltip(ItemTooltipEvent event) {
        if (!RequiemArmoryConfig.CLIENT_SPEC.isLoaded() || !RequiemArmoryConfig.CLIENT.showVanillaTooltips.get()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        // Our own weapons already print these lines themselves; only annotate minecraft: items.
        if (!BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft")) {
            return;
        }

        List<Component> lines = new ArrayList<>();
        if (item instanceof AxeItem) {
            WeaponTooltip.append(lines, AXE, 0.0f);
        } else if (item instanceof SwordItem) {
            WeaponTooltip.append(lines, SWORD, 0.0f);
        } else if (item instanceof TridentItem) {
            // The trident's throw damage is flat, not material-scaled, so no tier bonus.
            WeaponTooltip.append(lines, TRIDENT, 0.0f);
        } else if (item instanceof BowItem) {
            // The vanilla bow and crossbow *are* the wooden tier (see the mixins).
            RangedTooltip.append(lines, RangedStats.of(RangedType.BOW, RangedTier.WOODEN), RangedType.BOW);
        } else if (item instanceof CrossbowItem) {
            RangedTooltip.append(lines, RangedStats.of(RangedType.CROSSBOW, RangedTier.WOODEN), RangedType.CROSSBOW);
        }

        if (!lines.isEmpty()) {
            List<Component> tooltip = event.getToolTip();
            tooltip.addAll(Math.min(1, tooltip.size()), lines);
        }
    }
}
