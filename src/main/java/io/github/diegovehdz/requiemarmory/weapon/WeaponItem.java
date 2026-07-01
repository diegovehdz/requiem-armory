package io.github.diegovehdz.requiemarmory.weapon;

import java.util.List;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.registry.ModDamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * Base class for every melee weapon. Extends {@link SwordItem} for sword mining rules and the tool
 * component. Damage/speed/reach come from {@link #buildAttributes}; the special combat behaviours
 * come from the weapon's {@link WeaponAbilities}.
 *
 * <p>Throwing and the two-handed system are layered on in later phases.</p>
 */
public class WeaponItem extends SwordItem {
    /** Id for our custom reach modifier (distinct from vanilla's attack ids). */
    public static final ResourceLocation REACH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, "weapon_reach");

    private final WeaponType type;
    private final WeaponMaterial material;
    private final WeaponAbilities abilities;

    public WeaponItem(WeaponType type, WeaponMaterial material, Item.Properties properties) {
        super(material.tier, properties);
        this.type = type;
        this.material = material;
        this.abilities = type.abilities;
    }

    public WeaponType type() { return this.type; }
    public WeaponMaterial material() { return this.material; }

    // ------------------------------------------------------------------ attributes

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

    // ------------------------------------------------------------------ combat

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // NeoForge keeps the attack-strength ticker accurate through hurtEnemy, so we can read
        // whether this swing was fully charged here (mobs count as always charged).
        boolean charged = !(attacker instanceof Player player) || player.getAttackStrengthScale(0.5f) > 0.9f;
        boolean crit = charged && attacker instanceof Player p && isCriticalStrike(p);

        if (!target.level().isClientSide) {
            if (abilities.hasArmorPierce()
                    && charged
                    && target.getRandom().nextFloat() <= abilities.armorPierceChance) {
                float dmg = abilities.armorPierceAmount * (crit ? 1.5f : 1.0f);
                DamageSource source = attacker.damageSources().source(ModDamageTypes.ARMOR_PIERCING, attacker);
                target.invulnerableTime = 0;
                target.hurt(source, dmg);
            } else if (abilities.hasUnarmoredBonus() && charged && isUnarmored(target)) {
                float dmg = abilities.unarmoredBonus * (crit ? 1.5f : 1.0f);
                DamageSource source = attacker instanceof Player p2
                        ? attacker.damageSources().playerAttack(p2)
                        : attacker.damageSources().mobAttack(attacker);
                target.invulnerableTime = 0;
                target.hurt(source, dmg);
            }
        }

        boolean result = super.hurtEnemy(stack, target, attacker);

        // Quick / slow strike: shrink or stretch the target's post-hit invulnerability window.
        if (abilities.invincibilityTicks != WeaponAbilities.DEFAULT_INVINCIBILITY) {
            target.invulnerableTime = abilities.invincibilityTicks;
        }
        return result;
    }

    /** Custom sweep area (scaled by radius); optionally deals flat sweep damage itself. */
    @Override
    public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        float r = abilities.sweepRadius;
        AABB box = target.getBoundingBox().inflate(1.0 * r, 0.25 * r, 1.0 * r);

        if (abilities.sweepDamage <= 0.0f) {
            return box; // vanilla applies its own reduced sweep damage inside this box
        }

        if (!player.level().isClientSide) {
            float yaw = player.getYRot() * Mth.DEG_TO_RAD;
            for (LivingEntity nearby : player.level().getEntitiesOfClass(LivingEntity.class, box)) {
                if (nearby == player || nearby == target || player.isAlliedTo(nearby)) continue;
                if (nearby instanceof ArmorStand stand && stand.isMarker()) continue;
                nearby.knockback(0.4, Mth.sin(yaw), -Mth.cos(yaw));
                nearby.hurt(player.damageSources().playerAttack(player), abilities.sweepDamage);
            }
        }
        // We already dealt the sweep damage ourselves, so give vanilla an empty box.
        return target.getBoundingBox();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        if (ability == ItemAbilities.SWORD_SWEEP) {
            return abilities.canSweep;
        }
        if (abilities.versatile
                && (ability == ItemAbilities.AXE_DIG
                || ability == ItemAbilities.AXE_STRIP
                || ability == ItemAbilities.AXE_SCRAPE
                || ability == ItemAbilities.AXE_WAX_OFF)) {
            return true;
        }
        return super.canPerformAction(stack, ability);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return abilities.breach || super.canDisableShield(stack, shield, entity, attacker);
    }

    // ------------------------------------------------------------------ tooltip

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (abilities.versatile) tooltip.add(ability("versatile"));
        if (abilities.breach) tooltip.add(ability("breach"));
        if (abilities.hasArmorPierce()) tooltip.add(ability("armor_piercing"));
        if (abilities.hasUnarmoredBonus()) tooltip.add(ability("unarmored_bonus"));
        if (abilities.hasQuickStrike()) tooltip.add(ability("quick_strike"));
        if (abilities.hasSlowStrike()) tooltip.add(ability("slow_strike"));
        if (abilities.showsSweep()) tooltip.add(ability("sweeping"));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    private static Component ability(String key) {
        return Component.translatable("tooltip." + RequiemArmory.MOD_ID + "." + key)
                .withStyle(ChatFormatting.GOLD);
    }

    // ------------------------------------------------------------------ helpers

    private static boolean isUnarmored(LivingEntity entity) {
        for (ItemStack piece : entity.getArmorSlots()) {
            if (!piece.isEmpty()) return false;
        }
        return true;
    }

    private static boolean isCriticalStrike(Player p) {
        return p.fallDistance > 0.0f && !p.onGround() && !p.onClimbable() && !p.isInWater()
                && !p.hasEffect(MobEffects.BLINDNESS) && !p.isPassenger() && !p.isSprinting();
    }
}
