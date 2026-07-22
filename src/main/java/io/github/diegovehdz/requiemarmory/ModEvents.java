package io.github.diegovehdz.requiemarmory;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

/** Game-bus event handlers (modpack balance tweaks). */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID)
public final class ModEvents {
    private ModEvents() {}

    /** Attack damage taken off every vanilla axe, so other heavy weapons stay viable next to them. */
    private static final double AXE_DAMAGE_PENALTY = 1.0;

    /** Attack speed added to every vanilla axe. Trading a point of damage for a faster swing keeps
     *  the axe the DPS ceiling for heavy weapons while making it far less clunky to actually use
     *  (iron: 8 dmg × 1.1 = 8.8 DPS, up from 9 × 0.9 = 8.1 and still under the sword's 9.6). */
    private static final double AXE_SPEED_BONUS = 0.2;

    @SubscribeEvent
    static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        // Retune vanilla axes so they sit alongside our weapon balance: a little weaker, a little faster.
        Item item = event.getItemStack().getItem();
        if (!(item instanceof AxeItem)
                || !BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft")) {
            return;
        }
        for (ItemAttributeModifiers.Entry entry : event.getDefaultModifiers().modifiers()) {
            AttributeModifier modifier = entry.modifier();
            double delta;
            if (modifier.id().equals(Item.BASE_ATTACK_DAMAGE_ID)) {
                delta = -AXE_DAMAGE_PENALTY;
            } else if (modifier.id().equals(Item.BASE_ATTACK_SPEED_ID)) {
                delta = AXE_SPEED_BONUS;
            } else {
                continue;
            }
            event.replaceModifier(entry.attribute(),
                    new AttributeModifier(modifier.id(), modifier.amount() + delta, modifier.operation()),
                    entry.slot());
        }
    }
}
