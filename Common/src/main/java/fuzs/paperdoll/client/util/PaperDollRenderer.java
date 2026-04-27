package fuzs.paperdoll.client.util;

import fuzs.paperdoll.client.handler.PaperDollHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PaperDollRenderer {

    /**
     * @see net.minecraft.client.gui.screens.inventory.InventoryScreen#extractEntityInInventoryFollowsMouse(GuiGraphicsExtractor,
     *         int, int, int, int, int, float, float, float, LivingEntity)
     */
    public static void renderEntityInInventory(GuiGraphicsExtractor guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, LivingEntity livingEntity, float partialTick) {
        Quaternionf rotation = new Quaternionf().rotateZ(Mth.PI);
        Quaternionf overrideCameraAngle = new Quaternionf().rotateX(15.0F * Mth.DEG_TO_RAD);
        rotation.mul(overrideCameraAngle);
        LivingEntityRenderState livingEntityRenderState = extractRenderState(livingEntity, partialTick);
        livingEntityRenderState.bodyRot = 180.0F + PaperDollHandler.getDefaultRotationDegrees();
        livingEntityRenderState.xRot = PaperDollHandler.getEntityXRot(livingEntityRenderState);
        livingEntityRenderState.yRot = PaperDollHandler.getEntityYRot(partialTick);
        livingEntityRenderState.boundingBoxWidth =
                livingEntityRenderState.boundingBoxWidth / livingEntityRenderState.scale;
        livingEntityRenderState.boundingBoxHeight =
                livingEntityRenderState.boundingBoxHeight / livingEntityRenderState.scale;
        livingEntityRenderState.scale = 1.0F;
        Vector3f vector3f = new Vector3f(0.0F, livingEntityRenderState.boundingBoxHeight / 2.0F + yOffset, 0.0F);
        guiGraphics.entity(livingEntityRenderState, scale, vector3f, rotation, overrideCameraAngle, x1, y1, x2, y2);
    }

    /**
     * @see net.minecraft.client.gui.screens.inventory.InventoryScreen#extractRenderState(LivingEntity)
     */
    private static LivingEntityRenderState extractRenderState(LivingEntity livingEntity, float partialTick) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> entityRenderer = entityRenderDispatcher.getRenderer(livingEntity);
        LivingEntityRenderState livingEntityRenderState = (LivingEntityRenderState) entityRenderer.createRenderState(
                livingEntity,
                partialTick);
        livingEntityRenderState.lightCoords = 15728880;
        livingEntityRenderState.shadowPieces.clear();
        livingEntityRenderState.outlineColor = 0;
        return livingEntityRenderState;
    }
}
