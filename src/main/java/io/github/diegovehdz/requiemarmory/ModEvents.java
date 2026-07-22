package io.github.diegovehdz.requiemarmory;

import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
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
        // Retune vanilla axes so they sit alongside our weapon balance: a little weaker, a little
        // faster. Trading a point of damage for a faster swing keeps the axe the DPS ceiling for heavy
        // weapons while making it far less clunky (iron: 8 dmg × 1.1 = 8.8 DPS, up from 9 × 0.9 = 8.1
        // and still under the sword's 9.6). Configurable — see requiem_armory-common.toml.
        if (!RequiemArmoryConfig.COMMON_SPEC.isLoaded() || !RequiemArmoryConfig.COMMON.retuneVanillaAxes.get()) {
            return;
        }
        Item item = event.getItemStack().getItem();
        if (!(item instanceof AxeItem)
                || !BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft")) {
            return;
        }
        double damagePenalty = RequiemArmoryConfig.COMMON.vanillaAxeDamagePenalty.get();
        double speedBonus = RequiemArmoryConfig.COMMON.vanillaAxeSpeedBonus.get();
        for (ItemAttributeModifiers.Entry entry : event.getDefaultModifiers().modifiers()) {
            AttributeModifier modifier = entry.modifier();
            double delta;
            if (modifier.id().equals(Item.BASE_ATTACK_DAMAGE_ID)) {
                delta = -damagePenalty;
            } else if (modifier.id().equals(Item.BASE_ATTACK_SPEED_ID)) {
                delta = speedBonus;
            } else {
                continue;
            }
            event.replaceModifier(entry.attribute(),
                    new AttributeModifier(modifier.id(), modifier.amount() + delta, modifier.operation()),
                    entry.slot());
        }
    }
}
