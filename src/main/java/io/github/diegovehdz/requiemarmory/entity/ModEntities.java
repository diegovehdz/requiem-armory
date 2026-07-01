package io.github.diegovehdz.requiemarmory.entity;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Entity types registered by the mod. */
public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, RequiemArmory.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownWeaponEntity>> THROWN_WEAPON =
            ENTITY_TYPES.register("thrown_weapon", () -> EntityType.Builder
                    .<ThrownWeaponEntity>of(ThrownWeaponEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("thrown_weapon"));
}
