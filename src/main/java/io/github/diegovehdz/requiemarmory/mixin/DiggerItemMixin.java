package io.github.diegovehdz.requiemarmory.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.diegovehdz.requiemarmory.config.RequiemArmoryConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;

/**
 * Drops the extra durability cost vanilla charges a <b>tool</b> for being swung at something.
 * {@code DiggerItem#postHurtEnemy} spends 2 durability where a sword spends 1, which is a hidden tax
 * on using an axe as a weapon — and this mod treats the axe as a real weapon, the ceiling its own
 * heavy shapes are balanced against. Only the literal vanilla axes are touched: other diggers
 * (pickaxes, shovels, hoes) and every modded tool keep the vanilla penalty.
 *
 * <p>Follows the same on/off switch as the rest of the axe retune
 * ({@code requiem_armory-common.toml → vanillaBalance.retuneVanillaAxes}).</p>
 */
@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin {
    @Inject(method = "postHurtEnemy", at = @At("HEAD"), cancellable = true)
    private void requiem_armory$axeWeaponDurability(ItemStack stack, LivingEntity target,
                                                    LivingEntity attacker, CallbackInfo ci) {
        if (!(stack.getItem() instanceof AxeItem)
                || !BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("minecraft")) {
            return;
        }
        if (!RequiemArmoryConfig.COMMON_SPEC.isLoaded()
                || !RequiemArmoryConfig.COMMON.retuneVanillaAxes.get()) {
            return;
        }
        // A sword's cost, not a tool's.
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        ci.cancel();
    }
}
