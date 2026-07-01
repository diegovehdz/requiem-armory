package io.github.diegovehdz.requiemarmory.weapon;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

/**
 * The material a weapon is made of. For now the six vanilla tiers; modded materials can be added
 * later. Durability, enchantability and the base attack-damage bonus all come from the wrapped
 * {@link Tier}, so higher tiers hit harder and last longer automatically.
 */
public enum WeaponMaterial {
    WOODEN("wooden", Tiers.WOOD, false),
    STONE("stone", Tiers.STONE, false),
    IRON("iron", Tiers.IRON, false),
    GOLDEN("golden", Tiers.GOLD, false),
    DIAMOND("diamond", Tiers.DIAMOND, false),
    NETHERITE("netherite", Tiers.NETHERITE, true);

    /** Lowercase id used in registry names, textures and lang keys (e.g. {@code iron}). */
    public final String id;
    public final Tier tier;
    public final boolean fireResistant;

    WeaponMaterial(String id, Tier tier, boolean fireResistant) {
        this.id = id;
        this.tier = tier;
        this.fireResistant = fireResistant;
    }

    /** Applies material-wide item properties (netherite survives fire/lava). */
    public Item.Properties decorate(Item.Properties properties) {
        return fireResistant ? properties.fireResistant() : properties;
    }
}
