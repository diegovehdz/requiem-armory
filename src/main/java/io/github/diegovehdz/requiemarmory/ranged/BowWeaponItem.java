package io.github.diegovehdz.requiemarmory.ranged;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A tiered bow (iron, gold, diamond, netherite). The wooden tier is the vanilla bow itself, adjusted
 * via mixin, so it is <em>not</em> registered as one of these.
 *
 * <p>Behaviour reuses vanilla's {@code ProjectileWeaponItem#draw}/{@code #shoot} machinery (ammo,
 * infinity, multishot) and only overrides the tier-dependent part: the draw/charge curve and launch
 * velocity in {@link #releaseUsing} (velocity carries both range and damage). Durability and
 * enchantability come from {@link RangedStats}.</p>
 */
public class BowWeaponItem extends BowItem implements RangedWeapon {
    private final RangedType type;
    private final RangedTier tier;
    private final RangedStats stats;

    public BowWeaponItem(RangedType type, RangedTier tier, Item.Properties properties) {
        super(properties);
        this.type = type;
        this.tier = tier;
        this.stats = RangedStats.of(type, tier);
    }

    public RangedType type() { return this.type; }
    public RangedTier tier() { return this.tier; }
    public RangedStats stats() { return this.stats; }

    // ------------------------------------------------------------------ firing

    /** Mirrors vanilla {@code BowItem#releaseUsing} but with this tier's draw curve and velocity. */
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }
        ItemStack ammo = player.getProjectile(stack);
        if (ammo.isEmpty()) {
            return;
        }

        int charge = this.getUseDuration(stack, entity) - timeLeft;
        charge = net.neoforged.neoforge.event.EventHooks.onArrowLoose(stack, level, player, charge, true);
        if (charge < 0) {
            return;
        }

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
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public int getDefaultProjectileRange() {
        return type.projectileRange;
    }

    // ------------------------------------------------------------------ enchanting

    @Override
    public int getEnchantmentValue() {
        return stats.enchantValue();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        RangedTooltip.append(tooltip, stats, type);
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
