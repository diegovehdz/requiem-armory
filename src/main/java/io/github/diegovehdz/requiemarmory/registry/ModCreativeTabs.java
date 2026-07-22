package io.github.diegovehdz.requiemarmory.registry;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The "Armory" creative tab holding every weapon. */
public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RequiemArmory.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMORY = CREATIVE_MODE_TABS.register(
            "armory",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + RequiemArmory.MOD_ID + ".armory"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(ModItems.weapon("netherite_greatsword")))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HANDLE.get());
                        output.accept(ModItems.POLE.get());
                        // Config-disabled weapons stay registered (removing them would break saves)
                        // but drop out of the tab, alongside losing their recipes.
                        ModItems.WEAPONS.forEach((name, weapon) -> {
                            if (RequiemArmoryConfig.isWeaponEnabled(name)) output.accept(weapon.get());
                        });
                        ModItems.RANGED.forEach((name, ranged) -> {
                            if (RequiemArmoryConfig.isWeaponEnabled(name)) output.accept(ranged.get());
                        });
                    })
                    .build());
}
