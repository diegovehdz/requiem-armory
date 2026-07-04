package io.github.diegovehdz.requiemarmory.ranged;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

/**
 * Adjusts the default components of the vanilla ranged items so they match their new role as the
 * mod's <b>wooden</b> tier. Firing behaviour is handled separately by the mixins; this only covers
 * component-level stats (durability). Registered on the mod event bus from the main mod class.
 */
public final class RangedVanillaTweaks {
    private RangedVanillaTweaks() {}

    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        // Vanilla bow/crossbow = wooden tier: fewer uses than the iron (= vanilla-reference) versions.
        int bowDurability = RangedStats.of(RangedType.BOW, RangedTier.WOODEN).durability();
        event.modify(Items.BOW, builder -> builder.set(DataComponents.MAX_DAMAGE, bowDurability));

        int crossbowDurability = RangedStats.of(RangedType.CROSSBOW, RangedTier.WOODEN).durability();
        event.modify(Items.CROSSBOW, builder -> builder.set(DataComponents.MAX_DAMAGE, crossbowDurability));
    }
}
