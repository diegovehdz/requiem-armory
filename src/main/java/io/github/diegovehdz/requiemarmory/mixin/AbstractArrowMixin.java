package io.github.diegovehdz.requiemarmory.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.diegovehdz.requiemarmory.ranged.RangedWeapon;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

/**
 * Implements the ranged "consistency" mechanic: higher-tier bows/crossbows produce more predictable
 * damage. A fully-charged arrow's vanilla crit bonus is {@code random.nextInt(damage/2 + 2)} — this
 * squeezes that random value toward its mean by the firing weapon's {@code variance} factor, so a
 * netherite shot barely wobbles while a wooden/vanilla shot keeps full randomness. The <em>mean</em>
 * is preserved, so average damage is unchanged; only the spread tightens.
 */
@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Redirect(
        method = "onHitEntity",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I")
    )
    private int requiem_armory$tightenCritSpread(RandomSource random, int bound) {
        int raw = random.nextInt(bound);
        ItemStack weapon = ((AbstractArrow) (Object) this).getWeaponItem();
        float variance = RangedWeapon.varianceOf(weapon);
        if (variance >= 1.0F) {
            return raw; // vanilla behaviour (wooden/vanilla bow, other mods' bows, etc.)
        }
        double mean = (bound - 1) / 2.0;
        long tightened = Math.round(mean + (raw - mean) * variance);
        return (int) Math.max(0L, tightened);
    }
}
