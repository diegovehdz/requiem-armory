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

    @SubscribeEvent
    static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        // Nerf vanilla axes' attack damage by 1 so they sit alongside our weapon balance.
        Item item = event.getItemStack().getItem();
        if (!(item instanceof AxeItem)
                || !BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft")) {
            return;
        }
        for (ItemAttributeModifiers.Entry entry : event.getDefaultModifiers().modifiers()) {
            if (entry.modifier().id().equals(Item.BASE_ATTACK_DAMAGE_ID)) {
                AttributeModifier modifier = entry.modifier();
                event.replaceModifier(entry.attribute(),
                        new AttributeModifier(modifier.id(), modifier.amount() - 1.0, modifier.operation()),
                        entry.slot());
            }
        }
    }
}
