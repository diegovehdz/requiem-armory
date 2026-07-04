package io.github.diegovehdz.requiemarmory.weapon;

import java.util.List;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.registry.ModDamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/**
 * Base class for every melee weapon. Extends {@link SwordItem} for sword mining rules and the tool
 * component. Damage/speed/reach come from {@link #buildAttributes}; the special combat behaviours
 * come from the weapon's {@link WeaponAbilities}.
 *
 * <p>Two-handed weapons dynamically swap their attribute modifiers based on whether the off-hand is
 * occupied (see {@link #inventoryTick}). Throwing lands in a later phase.</p>
 */
public class WeaponItem extends SwordItem {
    /** Id for our custom reach modifier (distinct from vanilla's attack ids). */
    public static final ResourceLocation REACH_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, "weapon_reach");

    private final WeaponType type;
    private final WeaponMaterial material;
    private final WeaponAbilities abilities;

    /** True for the generated two-handed-moveset copy of a switchable weapon (see the two-handed switch). */
    private final boolean twoHandedVariant;
    /** Switch partners for the two-handed moveset swap (set during common setup, null otherwise). */
    private Item oneHandedForm;
    private Item twoHandedForm;

    // Cached attribute variants for the two-handed penalty (null for one-handed weapons).
    private final ItemAttributeModifiers fullAttributes;
    private final ItemAttributeModifiers penalizedAttributes;

    public WeaponItem(WeaponType type, WeaponMaterial material, Item.Properties properties) {
        this(type, material, properties, false);
    }

    public WeaponItem(WeaponType type, WeaponMaterial material, Item.Properties properties, boolean twoHandedVariant) {
        super(material.tier, properties, toolFor(type, material));
        this.type = type;
        this.material = material;
        this.abilities = type.abilities;
        this.twoHandedVariant = twoHandedVariant;

        if (abilities.isTwoHanded()) {
            this.fullAttributes = buildAttributes(type, material);
            this.penalizedAttributes = buildAttributes(type, material,
                    abilities.twoHandedDamagePenalty, abilities.twoHandedSpeedPenalty);
        } else {
            this.fullAttributes = null;
            this.penalizedAttributes = null;
        }
    }

    /** Versatile weapons get an axe-like tool (so they mine wood etc.); others keep the sword tool. */
    private static Tool toolFor(WeaponType type, WeaponMaterial material) {
        if (!type.abilities.versatile) {
            return SwordItem.createToolProperties();
        }
        return new Tool(List.of(
                Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, material.tier.getSpeed()),
                Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)),
                1.0F, 2);
    }

    public WeaponType type() { return this.type; }
    public WeaponMaterial material() { return this.material; }
    public WeaponAbilities abilities() { return this.abilities; }
    public boolean isTwoHandedVariant() { return this.twoHandedVariant; }

    /** Links a switchable weapon to its one-handed and two-handed forms (called during setup). */
    public void setSwitchForms(Item oneHandedForm, Item twoHandedForm) {
        this.oneHandedForm = oneHandedForm;
        this.twoHandedForm = twoHandedForm;
    }

    // ------------------------------------------------------------------ attributes

    public static ItemAttributeModifiers buildAttributes(WeaponType type, WeaponMaterial material) {
        return buildAttributes(type, material, 0.0f, 0.0f);
    }

    /** Attack damage + speed (as a sword) plus reach, with an optional two-handed penalty subtracted. */
    public static ItemAttributeModifiers buildAttributes(WeaponType type, WeaponMaterial material,
                                                         float damagePenalty, float speedPenalty) {
        double damage = type.attackDamageModifier + material.tier.getAttackDamageBonus() - damagePenalty;
        double speed = type.attackSpeedModifier() - speedPenalty;

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND);

        // Better Combat manages reach through its weapon_attributes range_bonus, so only apply our
        // own reach modifier when Better Combat is absent (mirrors how Dixta's Armory behaved).
        float reach = type.reachModifier();
        if (reach != 0.0f && !ModList.get().isLoaded("bettercombat")) {
            builder.add(Attributes.ENTITY_INTERACTION_RANGE,
                    new AttributeModifier(REACH_MODIFIER_ID, reach, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }
        return builder.build();
    }

    // ------------------------------------------------------------------ two-handed

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!abilities.isTwoHanded() || level.isClientSide || !(entity instanceof Player player)) {
            return;
        }

        // Two-handed moveset switch: swap to the two-handed form when the off-hand frees up, and back
        // to the one-handed form when it is occupied. Only meaningful with Better Combat's movesets.
        if (abilities.twoHandedSwitch && oneHandedForm != null && twoHandedForm != null
                && player.getMainHandItem() == stack && ModList.get().isLoaded("bettercombat")) {
            boolean offHandEmpty = player.getOffhandItem().isEmpty();
            if (!twoHandedVariant && offHandEmpty) {
                player.setItemInHand(InteractionHand.MAIN_HAND, stack.transmuteCopy(twoHandedForm));
                return;
            }
            if (twoHandedVariant && !offHandEmpty) {
                player.setItemInHand(InteractionHand.MAIN_HAND, stack.transmuteCopy(oneHandedForm));
                return;
            }
        }

        boolean inMainHand = player.getMainHandItem() == stack;
        boolean inOffHand = player.getOffhandItem() == stack;

        // Unified two-handed penalty: reduced damage/speed whenever the other hand is occupied.
        ItemAttributeModifiers desired = fullAttributes;
        if (inMainHand || inOffHand) {
            ItemStack other = inMainHand ? player.getOffhandItem() : player.getMainHandItem();
            if (!other.isEmpty()) {
                desired = penalizedAttributes;
            }
        }

        if (!desired.equals(stack.get(DataComponents.ATTRIBUTE_MODIFIERS))) {
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, desired);
        }
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
        boolean shift = Screen.hasShiftDown();
        boolean any = false;

        if (abilities.isTwoHanded()) {
            tooltip.add(abilityName("two_handed"));
            if (shift) tooltip.add(abilityDesc("two_handed.desc"));
            any = true;
        }
        if (abilities.versatile) {
            tooltip.add(abilityName("versatile"));
            if (shift) tooltip.add(abilityDesc("versatile.desc"));
            any = true;
        }
        if (abilities.breach) {
            tooltip.add(abilityName("breach"));
            if (shift) tooltip.add(abilityDesc("breach.desc"));
            any = true;
        }
        if (abilities.hasArmorPierce()) {
            tooltip.add(abilityName("armor_piercing"));
            if (shift) {
                if (abilities.armorPierceChance >= 1.0f) {
                    tooltip.add(abilityDesc("armor_piercing.desc", fmt(abilities.armorPierceAmount)));
                } else {
                    tooltip.add(abilityDesc("armor_piercing.desc.chance",
                            Math.round(abilities.armorPierceChance * 100) + "%", fmt(abilities.armorPierceAmount)));
                }
            }
            any = true;
        }
        if (abilities.hasUnarmoredBonus()) {
            tooltip.add(abilityName("unarmored_bonus"));
            if (shift) tooltip.add(abilityDesc("unarmored_bonus.desc", fmt(abilities.unarmoredBonus)));
            any = true;
        }
        if (abilities.hasQuickStrike()) {
            tooltip.add(abilityName("quick_strike"));
            if (shift) tooltip.add(abilityDesc("quick_strike.desc", fmt((abilities.invincibilityTicks - 10) / 20.0f)));
            any = true;
        }
        if (abilities.hasSlowStrike()) {
            tooltip.add(abilityName("slow_strike"));
            if (shift) tooltip.add(abilityDesc("slow_strike.desc", fmt((abilities.invincibilityTicks - 10) / 20.0f)));
            any = true;
        }
        if (abilities.showsSweep()) {
            tooltip.add(abilityName("sweeping"));
            if (shift) {
                if (abilities.sweepDamage > 0.0f) tooltip.add(abilityDesc("sweeping.desc.damage", fmt(abilities.sweepDamage)));
                if (abilities.sweepRadius != 1.0f) tooltip.add(abilityDesc("sweeping.desc.radius", fmt(abilities.sweepRadius)));
            }
            any = true;
        }

        if (any && !shift) {
            tooltip.add(Component.translatable("tooltip." + RequiemArmory.MOD_ID + ".hold_shift")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    /** Gold ability name line. */
    protected static Component abilityName(String key) {
        return Component.translatable("tooltip." + RequiemArmory.MOD_ID + "." + key).withStyle(ChatFormatting.GOLD);
    }

    /** Gray, plain-text description line shown when Shift is held. */
    protected static Component abilityDesc(String key, Object... args) {
        return Component.translatable("tooltip." + RequiemArmory.MOD_ID + "." + key, args)
                .withStyle(ChatFormatting.GRAY);
    }

    /** Formats a float, dropping a trailing ".0" for whole numbers. */
    protected static String fmt(float value) {
        return value == Math.rint(value) ? String.valueOf((int) value) : String.valueOf(value);
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
