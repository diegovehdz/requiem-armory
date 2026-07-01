package io.github.diegovehdz.requiemarmory.registry;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

/**
 * Damage-type keys. The actual {@link DamageType} definitions live in the data pack
 * ({@code data/requiem_armory/damage_type/}); these keys let code build damage sources for them.
 */
public final class ModDamageTypes {
    private ModDamageTypes() {}

    /** Armour-ignoring damage dealt by the armor-piercing weapon ability. */
    public static final ResourceKey<DamageType> ARMOR_PIERCING = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, "armor_piercing"));
}
