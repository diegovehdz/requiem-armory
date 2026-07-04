package io.github.diegovehdz.requiemarmory.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.diegovehdz.requiemarmory.ranged.RangedStats;
import io.github.diegovehdz.requiemarmory.ranged.RangedTier;
import io.github.diegovehdz.requiemarmory.ranged.RangedType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

/**
 * Makes the vanilla crossbow behave as Requiem Armory's wooden tier: it charges faster than the iron
 * (= vanilla-reference) crossbow but launches arrows slower (shorter range, lower damage). Only the
 * literal {@code minecraft:crossbow} is affected — the mod's own tiers and other mods' crossbows are
 * guarded out by the item check.
 */
@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends ProjectileWeaponItem {
    private CrossbowItemMixin() {
        super(null); // never invoked; only present to satisfy the compiler for the extends.
    }

    /** Wooden crossbow charges faster (static method, so all callers — use/animation — stay in sync). */
    @Inject(method = "getChargeDuration", at = @At("HEAD"), cancellable = true)
    private static void requiem_armory$woodenChargeTime(ItemStack stack, LivingEntity shooter, CallbackInfoReturnable<Integer> cir) {
        if (!stack.is(Items.CROSSBOW)) {
            return;
        }
        RangedStats stats = RangedStats.of(RangedType.CROSSBOW, RangedTier.WOODEN);
        float seconds = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, stats.drawTicks() / 20.0F);
        cir.setReturnValue(Math.max(1, Mth.floor(seconds * 20.0F)));
    }

    /** Wooden crossbow launches arrows at reduced velocity (fireworks keep vanilla speed). */
    @Inject(method = "performShooting", at = @At("HEAD"), cancellable = true)
    private void requiem_armory$woodenShoot(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon,
                                            float velocity, float inaccuracy, LivingEntity target, CallbackInfo ci) {
        if (!weapon.is(Items.CROSSBOW)) {
            return;
        }
        ci.cancel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (shooter instanceof Player player
                && net.neoforged.neoforge.event.EventHooks.onArrowLoose(weapon, level, player, 1, true) < 0) {
            return;
        }
        ChargedProjectiles charged = weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (charged == null || charged.isEmpty()) {
            return;
        }
        float v = charged.contains(Items.FIREWORK_ROCKET) ? velocity : velocity * RangedTier.WOODEN.velocityMult;
        this.shoot(serverLevel, shooter, hand, weapon, charged.getItems(), v, inaccuracy, shooter instanceof Player, target);
        if (shooter instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayer, weapon);
            serverPlayer.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
        }
    }
}
