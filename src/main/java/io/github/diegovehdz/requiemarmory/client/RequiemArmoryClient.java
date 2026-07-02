package io.github.diegovehdz.requiemarmory.client;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.entity.ModEntities;
import io.github.diegovehdz.requiemarmory.registry.ModItems;
import io.github.diegovehdz.requiemarmory.weapon.ThrowableWeaponItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Client-only setup: entity renderers and the throwables' {@code throwing} model property. */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID, value = Dist.CLIENT)
public final class RequiemArmoryClient {
    private RequiemArmoryClient() {}

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THROWN_WEAPON.get(), ThrownWeaponRenderer::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ResourceLocation throwing = ResourceLocation.fromNamespaceAndPath(RequiemArmory.MOD_ID, "throwing");
        event.enqueueWork(() -> ModItems.WEAPONS.values().forEach(holder -> {
            if (holder.get() instanceof ThrowableWeaponItem item) {
                // 1.0 while the player is charging a throw, so the model can switch to a throwing pose.
                ItemProperties.register(item, throwing, (stack, level, entity, seed) ->
                        entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
            }
        }));
    }
}
