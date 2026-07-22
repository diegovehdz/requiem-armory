package io.github.diegovehdz.requiemarmory.registry;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import io.github.diegovehdz.requiemarmory.weapon.WeaponMaterial;
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
                        // Two reasons a weapon is registered but not shown: the config switched it
                        // off, or its material's ingredient tag is empty because the mod providing
                        // that metal is absent. Tab contents are rebuilt after datapacks load, so the
                        // tag check sees the real answer.
                        ModItems.WEAPONS.forEach((name, weapon) -> {
                            if (isShown(name)) output.accept(weapon);
                        });
                        ModItems.RANGED.forEach((name, ranged) -> {
                            if (isShown(name)) output.accept(ranged);
                        });
                    })
                    .build());

    /** Whether a weapon should appear: enabled in the config, and its material actually obtainable. */
    private static boolean isShown(String path) {
        if (!RequiemArmoryConfig.isWeaponEnabled(path)) {
            return false;
        }
        return WeaponMaterial.all().stream()
                .filter(m -> path.startsWith(m.id + "_"))
                .findFirst()
                .map(WeaponMaterial::isAvailable)
                .orElse(true);
    }
}
