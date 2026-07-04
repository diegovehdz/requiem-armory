package io.github.diegovehdz.requiemarmory.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.diegovehdz.requiemarmory.ranged.RangedMechanics;
import io.github.diegovehdz.requiemarmory.ranged.RangedStats;
import io.github.diegovehdz.requiemarmory.ranged.RangedTier;
import io.github.diegovehdz.requiemarmory.ranged.RangedType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;

/**
 * Makes the vanilla bow behave as Requiem Armory's wooden tier: a softer, shorter-ranged but
 * faster-drawing baseline sitting below the mod's iron bow (which is pinned to the vanilla reference).
 *
 * <p>Only the literal {@code minecraft:bow} is affected. The mod's own tiered bows override
 * {@code releaseUsing} entirely and never reach this injection; other mods' plain bows are guarded out
 * by the {@link ItemStack#is(Item)} check. The mixin extends {@link ProjectileWeaponItem} purely to
 * reach the inherited {@code draw}/{@code shoot} helpers, so ammo, infinity and multishot keep
 * working — its constructor is never invoked.</p>
 */
@Mixin(BowItem.class)
public abstract class BowItemMixin extends ProjectileWeaponItem {
    private BowItemMixin() {
        super(null); // unreachable: mixins are never instantiated. Present only to satisfy javac.
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    private void requiem_armory$fireAsWoodenTier(ItemStack stack, Level level, LivingEntity entity, int timeLeft, CallbackInfo ci) {
        if (!stack.is(Items.BOW)) {
            return; // leave modded/other bows untouched
        }
        ci.cancel();
        if (!(entity instanceof Player player)) {
            return;
        }
        ItemStack ammo = player.getProjectile(stack);
        if (ammo.isEmpty()) {
            return;
        }

        // BowItem#getUseDuration is a constant 72000, so the elapsed charge is 72000 - timeLeft.
        int charge = 72000 - timeLeft;
        charge = net.neoforged.neoforge.event.EventHooks.onArrowLoose(stack, level, player, charge, true);
        if (charge < 0) {
            return;
        }

        RangedStats stats = RangedStats.of(RangedType.BOW, RangedTier.WOODEN);
        float power = RangedMechanics.powerForCharge(charge, stats.drawTicks());
        if (power < 0.1F) {
            return;
        }

        List<ItemStack> projectiles = draw(stack, ammo, player);
        if (level instanceof ServerLevel serverLevel && !projectiles.isEmpty()) {
            this.shoot(serverLevel, player, player.getUsedItemHand(), stack, projectiles,
                    power * stats.velocity(), 1.0F, power == 1.0F, null);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
    }
}
