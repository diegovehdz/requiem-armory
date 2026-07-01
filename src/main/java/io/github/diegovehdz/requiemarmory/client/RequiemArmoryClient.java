package io.github.diegovehdz.requiemarmory.client;

import io.github.diegovehdz.requiemarmory.RequiemArmory;
import io.github.diegovehdz.requiemarmory.entity.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Client-only setup (entity renderers, and later item model properties for throwables). */
@EventBusSubscriber(modid = RequiemArmory.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class RequiemArmoryClient {
    private RequiemArmoryClient() {}

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THROWN_WEAPON.get(), ThrownWeaponRenderer::new);
    }
}
