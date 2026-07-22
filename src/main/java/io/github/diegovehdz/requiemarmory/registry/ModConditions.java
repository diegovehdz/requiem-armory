package io.github.diegovehdz.requiemarmory.registry;

import com.mojang.serialization.MapCodec;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.config.WeaponEnabledCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Registers this mod's datapack conditions (currently just the config-driven weapon toggle). */
public final class ModConditions {
    private ModConditions() {}

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, RequiemArmory.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<WeaponEnabledCondition>>
            WEAPON_ENABLED = CONDITION_CODECS.register("weapon_enabled", () -> WeaponEnabledCondition.CODEC);
}
