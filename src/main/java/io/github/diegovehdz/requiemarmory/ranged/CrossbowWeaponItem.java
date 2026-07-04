package io.github.diegovehdz.requiemarmory.ranged;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

/**
 * A tiered crossbow (iron, gold, diamond, netherite). The wooden tier is the vanilla crossbow itself,
 * adjusted via mixin, so it is <em>not</em> registered as one of these.
 *
 * <p>Unlike a bow, a crossbow's launch velocity is fixed per shot (it either loads or it doesn't), so
 * the "draw" lever is <b>charge time</b> ({@link #chargeDurationTicks}) and the range/damage lever is
 * the fixed arrow velocity, retuned per tier in {@link #shootProjectile}. Loading (charge) is
 * reimplemented in {@link #releaseUsing} against the tier charge time; firing keeps vanilla's
 * charged-projectile machinery untouched. Fireworks keep their vanilla velocity.</p>
 */
public class CrossbowWeaponItem extends CrossbowItem implements RangedWeapon {
    private final RangedType type;
    private final RangedTier tier;
    private final RangedStats stats;

    public CrossbowWeaponItem(RangedType type, RangedTier tier, Item.Properties properties) {
        super(properties);
        this.type = type;
        this.tier = tier;
        this.stats = RangedStats.of(type, tier);
    }

    public RangedType type() { return this.type; }
    public RangedTier tier() { return this.tier; }
    @Override public RangedStats stats() { return this.stats; }

    // ------------------------------------------------------------------ charging

    /** Ticks to fully charge this crossbow: the tier base scaled by the Quick Charge enchantment. */
    public int chargeDurationTicks(ItemStack stack, LivingEntity shooter) {
        float seconds = stats.drawTicks() / 20.0F;
        seconds = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, seconds);
        return Math.max(1, Mth.floor(seconds * 20.0F));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return chargeDurationTicks(stack, entity) + 3;
    }

    private float powerForCharge(int charge, ItemStack stack, LivingEntity entity) {
        float f = (float) charge / chargeDurationTicks(stack, entity);
        return f > 1.0F ? 1.0F : f;
    }

    /** Mirrors vanilla loading, but completes at this tier's charge time. */
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        int charge = this.getUseDuration(stack, entity) - timeLeft;
        float power = powerForCharge(charge, stack, entity);
        if (power >= 1.0F && !CrossbowItem.isCharged(stack) && loadProjectiles(entity, stack)) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.CROSSBOW_LOADING_END, entity.getSoundSource(),
                    1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    private boolean loadProjectiles(LivingEntity shooter, ItemStack stack) {
        List<ItemStack> list = draw(stack, shooter.getProjectile(stack), shooter);
        if (list.isEmpty()) {
            return false;
        }
        stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
        return true;
    }

    // ------------------------------------------------------------------ firing

    /** Replaces the fixed arrow velocity with this tier's (range + damage); fireworks stay vanilla. */
    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity,
                                   float inaccuracy, float angle, LivingEntity target) {
        float v = projectile instanceof AbstractArrow ? stats.velocity() : velocity;
        super.shootProjectile(shooter, projectile, index, v, inaccuracy, angle, target);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return type.supportsFireworks ? ARROW_OR_FIREWORK : ARROW_ONLY;
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
}
