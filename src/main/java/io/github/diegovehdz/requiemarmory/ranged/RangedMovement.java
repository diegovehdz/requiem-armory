package io.github.diegovehdz.requiemarmory.ranged;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Tiered movement while drawing a ranged weapon. Vanilla already slows a player to ~20% speed while
 * using a bow/crossbow; this layers a small per-tier multiplier on top so lighter (lower-tier) weapons
 * keep more of your speed and heavier (higher-tier) ones slow you further.
 *
 * <p>Applied as a transient {@code MOVEMENT_SPEED} modifier added while drawing and removed the moment
 * you stop. Runs on both sides (the event fires on both) so client movement prediction and the server
 * stay in agreement. For crossbows this only applies <em>while charging</em> — a loaded crossbow held
 * ready does not slow you.</p>
 */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID)
public final class RangedMovement {
    private RangedMovement() {}

    private static final ResourceLocation DRAW_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, "ranged_draw_speed");

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        float modifier = drawMovementModifier(player);
        AttributeModifier current = speed.getModifier(DRAW_SPEED_ID);
        if (modifier != 0.0f) {
            if (current == null || Math.abs(current.amount() - modifier) > 1.0E-6) {
                speed.removeModifier(DRAW_SPEED_ID);
                speed.addTransientModifier(new AttributeModifier(
                        DRAW_SPEED_ID, modifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        } else if (current != null) {
            speed.removeModifier(DRAW_SPEED_ID);
        }
    }

    /** The move modifier for the ranged weapon the player is currently drawing, or 0 if none. */
    private static float drawMovementModifier(Player player) {
        if (!player.isUsingItem()) {
            return 0.0f;
        }
        ItemStack using = player.getUseItem();
        if (using.getItem() instanceof RangedWeapon ranged) {
            return ranged.stats().moveModifier();
        }
        if (using.is(Items.BOW)) {
            return RangedStats.of(RangedType.BOW, RangedTier.WOODEN).moveModifier();
        }
        if (using.is(Items.CROSSBOW)) {
            return RangedStats.of(RangedType.CROSSBOW, RangedTier.WOODEN).moveModifier();
        }
        return 0.0f;
    }
}
