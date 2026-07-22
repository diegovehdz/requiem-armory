package io.github.diegovehdz.requiemarmory;

import com.mojang.logging.LogUtils;
import io.github.diegovehdz.requiemarmory.client.ClientConfigScreen;
import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import io.github.diegovehdz.requiemarmory.entity.ModEntities;
import io.github.diegovehdz.requiemarmory.ranged.RangedVanillaTweaks;
import io.github.diegovehdz.requiemarmory.registry.ModConditions;
import io.github.diegovehdz.requiemarmory.registry.ModCreativeTabs;
import io.github.diegovehdz.requiemarmory.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * Main entry point for Requiem Armory.
 *
 * <p>The mod is intended to be <b>data-driven</b>: weapons are described by data and registered
 * from those definitions, mirroring the original Dixta's Armory design. For now this only wires up
 * the base registries; the weapon system lands in a later phase.</p>
 */
@Mod(RequiemArmory.MOD_ID)
public class RequiemArmory {
    /** Must match {@code mod_id} in gradle.properties and the neoforge.mods.toml template. */
    public static final String MOD_ID = "requiem_armory";

    public static final Logger LOGGER = LogUtils.getLogger();

    public RequiemArmory(IEventBus modBus, ModContainer modContainer) {
        // Bind deferred registers to the mod event bus so the game registers their contents.
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModConditions.CONDITION_CODECS.register(modBus);

        // Retune the vanilla ranged items into this mod's wooden tier (mod-bus event).
        modBus.addListener(RangedVanillaTweaks::modifyDefaultComponents);

        modContainer.registerConfig(ModConfig.Type.CLIENT, RequiemArmoryConfig.CLIENT_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, RequiemArmoryConfig.COMMON_SPEC);
        // Keep the parsed disabled-weapon set in step with the file on load and on every edit.
        modBus.addListener(ModConfigEvent.Loading.class, event -> RequiemArmoryConfig.refreshDisabledWeapons());
        modBus.addListener(ModConfigEvent.Reloading.class, event -> RequiemArmoryConfig.refreshDisabledWeapons());
        if (FMLEnvironment.dist.isClient()) {
            ClientConfigScreen.register(modContainer);
        }

        LOGGER.info("[{}] Loading Requiem Armory", MOD_ID);
    }
}
