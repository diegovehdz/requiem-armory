package io.github.diegovehdz.requiemarmory.registry;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Holds every item registered by the mod.
 *
 * <p>Right now this only contains two utility items. Once the weapon system is in place, the
 * generated weapon items will be added/collected here so the creative tab and data generators can
 * iterate over them.</p>
 */
public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RequiemArmory.MOD_ID);

    /** Icon shown on the creative tab (placeholder art until weapon assets are added). */
    public static final DeferredItem<Item> ICON = ITEMS.registerSimpleItem("icon");

    /** Structural ingredient for polearm-style recipes — mirrors the original mod's {@code pole}. */
    public static final DeferredItem<Item> POLE = ITEMS.registerSimpleItem("pole");
}
