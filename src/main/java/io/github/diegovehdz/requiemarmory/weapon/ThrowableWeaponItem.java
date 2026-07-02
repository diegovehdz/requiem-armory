package io.github.diegovehdz.requiemarmory.weapon;

import java.util.List;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.entity.ThrownWeaponEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * A melee weapon that can also be thrown like a trident: hold to charge, release to launch a single
 * projectile that can be picked back up. No ammo.
 */
public class ThrowableWeaponItem extends WeaponItem {
    public ThrowableWeaponItem(WeaponType type, WeaponMaterial material, Item.Properties properties) {
        super(type, material, properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isTooDamagedToThrow(stack)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }
        int chargeTicks = getUseDuration(stack, entity) - timeLeft;
        if (chargeTicks < abilities().throwChargeTicks || isTooDamagedToThrow(stack)) {
            return;
        }

        if (!level.isClientSide) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entity.getUsedItemHand()));

            ThrownWeaponEntity thrown = new ThrownWeaponEntity(level, player, stack.copyWithCount(1));
            thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, abilities().throwPower, 1.0f);
            if (player.hasInfiniteMaterials()) {
                thrown.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.addFreshEntity(thrown);
            level.playSound(null, thrown, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, 1.0f);

            if (!player.hasInfiniteMaterials()) {
                player.getInventory().removeItem(stack);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    private static boolean isTooDamagedToThrow(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        WeaponAbilities a = abilities();
        tooltip.add(Component.translatable("tooltip." + RequiemArmory.MOD_ID + ".throwable").withStyle(ChatFormatting.GOLD));
        if (Screen.hasShiftDown()) {
            tooltip.add(abilityDesc("throwable.desc.damage", fmt(a.throwDamage)));
            tooltip.add(abilityDesc("throwable.desc.force", fmt(a.throwPower)));
            tooltip.add(abilityDesc("throwable.desc.charge", fmt(a.throwChargeTicks / 20.0f)));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
