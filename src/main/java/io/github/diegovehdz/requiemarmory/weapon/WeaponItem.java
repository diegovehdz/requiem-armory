package io.github.diegovehdz.requiemarmory.weapon;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Base class for every melee weapon. Extends {@link SwordItem} so we inherit sword mining rules,
 * the tool component and (later) sweeping. Damage, attack speed and reach are supplied through the
 * {@code ATTRIBUTE_MODIFIERS} component built by {@link #buildAttributes}.
 *
 * <p>Throwing, two-handed penalties and the other abilities will be added on top of this class in
 * subsequent phases; for now it is a straightforward, correctly-statted weapon.</p>
 */
public class WeaponItem extends SwordItem {
    /** Id for our custom reach modifier (distinct from vanilla's attack ids). */
    public static final ResourceLocation REACH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, "weapon_reach");

    private final WeaponType type;
    private final WeaponMaterial material;

    public WeaponItem(WeaponType type, WeaponMaterial material, Item.Properties properties) {
        super(material.tier, properties);
        this.type = type;
        this.material = material;
    }

    public WeaponType type() {
        return this.type;
    }

    public WeaponMaterial material() {
        return this.material;
    }

    /**
     * Attack damage + attack speed (as a sword would have) plus our reach modifier. Damage adds the
     * material's tier bonus so it scales with material.
     */
    public static ItemAttributeModifiers buildAttributes(WeaponType type, WeaponMaterial material) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                                type.attackDamageModifier + material.tier.getAttackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                type.attackSpeedModifier(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND);

        float reach = type.reachModifier();
        if (reach != 0.0f) {
            builder.add(Attributes.ENTITY_INTERACTION_RANGE,
                    new AttributeModifier(REACH_MODIFIER_ID, reach, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }
        return builder.build();
    }
}
