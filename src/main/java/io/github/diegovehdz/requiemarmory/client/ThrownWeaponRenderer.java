package io.github.diegovehdz.requiemarmory.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import io.github.diegovehdz.requiemarmory.entity.ThrownWeaponEntity;
import io.github.diegovehdz.requiemarmory.weapon.WeaponItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Renders a thrown weapon as its item model, oriented along its flight and spinning in the air. */
public class ThrownWeaponRenderer extends EntityRenderer<ThrownWeaponEntity> {
    private final ItemRenderer itemRenderer;

    public ThrownWeaponRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ThrownWeaponEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ItemStack stack = entity.getRenderStack();
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            // Orient the weapon along its flight path (point-first), like a trident. No endless spin.
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
            // Larger weapons use 32px "handheld" textures; scale them up so the flying model matches
            // the in-hand size instead of rendering tiny at the base (NONE) scale.
            if (stack.getItem() instanceof WeaponItem weapon && weapon.type().separateModel) {
                poseStack.scale(2.0f, 2.0f, 2.0f);
            }
            this.itemRenderer.renderStatic(stack, ItemDisplayContext.NONE, packedLight,
                    OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownWeaponEntity entity) {
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }
}
