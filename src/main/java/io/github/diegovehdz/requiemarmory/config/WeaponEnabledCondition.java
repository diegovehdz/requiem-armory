package io.github.diegovehdz.requiemarmory.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Datapack condition that keeps a recipe out of the recipe manager when its weapon is switched off in
 * {@code requiem_armory-common.toml}. Every weapon recipe carries one:
 *
 * <pre>{@code
 * "neoforge:conditions": [{ "type": "requiem_armory:weapon_enabled", "weapon": "requiem_armory:iron_warhammer" }]
 * }</pre>
 *
 * <p>Conditions are evaluated while datapacks load, which is after configs are read — so the answer is
 * always current as of the last {@code /reload}. Disabling a weapon mid-session therefore needs a
 * reload before its recipe actually disappears.</p>
 */
public record WeaponEnabledCondition(ResourceLocation weapon) implements ICondition {
    public static final MapCodec<WeaponEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(ResourceLocation.CODEC.fieldOf("weapon").forGetter(WeaponEnabledCondition::weapon))
            .apply(instance, WeaponEnabledCondition::new));

    @Override
    public boolean test(IContext context) {
        return RequiemArmoryConfig.isWeaponEnabled(weapon.getPath());
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "weapon_enabled(\"" + weapon + "\")";
    }
}
