package fuzs.paperdoll.client.handler;

import fuzs.paperdoll.PaperDoll;
import fuzs.paperdoll.client.util.PaperDollRenderer;
import fuzs.paperdoll.config.ClientConfig;
import fuzs.paperdoll.config.DisplayAction;
import fuzs.puzzleslib.api.client.gui.v2.AnchorPoint;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class PaperDollHandler {
    private static final float MAX_ROTATION_DEGREES = 30.0F;
    private static final float DEFAULT_ROTATION_DEGREES = MAX_ROTATION_DEGREES / 2.0F;
    private static final float SPIN_BACK_SPEED = 10.0F;

    private static int remainingDisplayTicks;
    private static int remainingRidingTicks;
    private static float yRotOffset;
    private static float yRotOffsetO;

    public static void onEndClientTick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.isPaused()) {
            return;
        }

        // update display ticks
        int displayTime = PaperDoll.CONFIG.get(ClientConfig.class).displayTime;
        if (DisplayAction.isActive(minecraft.player, remainingRidingTicks)) {
            remainingDisplayTicks = displayTime;
        } else if (remainingDisplayTicks > 0) {
            remainingDisplayTicks--;
        }

        // reset rotation when no longer shown
        if (remainingDisplayTicks > 0 || displayTime == 0) {
            tickYRotOffset(minecraft.player);
        } else {
            yRotOffset = yRotOffsetO = 0;
        }

        // don't show paper doll in sneaking position after unmounting a vehicle / mount
        if (minecraft.player.isPassenger()) {
            remainingRidingTicks = Math.max(0, displayTime - 2);
        } else if (remainingRidingTicks > 0) {
            remainingRidingTicks--;
        }
    }

    private static void tickYRotOffset(Player player) {
        yRotOffsetO = yRotOffset;
        // apply rotation change from entity
        yRotOffset = Mth.clamp(yRotOffset + (player.yHeadRot - player.yHeadRotO) * 0.5F,
                -MAX_ROTATION_DEGREES,
                MAX_ROTATION_DEGREES);

        // rotate back to origin, never overshoot 0
        float nextYRotOffset = yRotOffset - yRotOffset / SPIN_BACK_SPEED;
        if (yRotOffset < 0.0F) {
            yRotOffset = Math.min(0, nextYRotOffset);
        } else if (yRotOffset > 0.0F) {
            yRotOffset = Math.max(0, nextYRotOffset);
        } else {
            yRotOffset = 0.0F;
        }
    }

    public static void renderPaperDoll(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Profiler.get().push("paperDoll");
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && minecraft.player != null && !minecraft.player.isInvisible()
                && !minecraft.player.isSpectator()) {
            ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);
            if (minecraft.options.getCameraType().isFirstPerson() || !config.firstPersonOnly) {
                if (remainingDisplayTicks > 0 || config.displayTime == 0) {
                    float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
                    int scale = config.scale * 5;
                    int size = scale * 7 / 2;
                    AnchorPoint.Positioner positioner = config.anchorPoint.createPositioner(guiGraphics.guiWidth(),
                            guiGraphics.guiHeight(),
                            size,
                            size);
                    ScreenRectangle rectangle = positioner.getRectangle(config.offsetX, config.offsetY);
                    PaperDollRenderer.renderEntityInInventory(guiGraphics,
                            rectangle.left(),
                            rectangle.top(),
                            rectangle.right(),
                            rectangle.bottom(),
                            scale,
                            0.0F,
                            minecraft.player,
                            partialTick);
                }
            }
        }

        Profiler.get().pop();
    }

    public static float getDefaultRotationDegrees() {
        boolean isRight = PaperDoll.CONFIG.get(ClientConfig.class).anchorPoint.isRight();
        return isRight ? DEFAULT_ROTATION_DEGREES : -DEFAULT_ROTATION_DEGREES;
    }

    public static float getEntityXRot(LivingEntityRenderState livingEntityRenderState) {
        // head rotation is used for doll rotation as it updates a lot more precisely than the body rotation
        if (PaperDoll.CONFIG.get(ClientConfig.class).headMovement == ClientConfig.HeadMovement.YAW
                || livingEntityRenderState.pose == Pose.FALL_FLYING) {
            return 7.5F;
        } else {
            return 0.0F;
        }
    }

    public static float getEntityYRot(float partialTick) {
        if (PaperDoll.CONFIG.get(ClientConfig.class).headMovement != ClientConfig.HeadMovement.PITCH) {
            return Mth.rotLerp(partialTick, yRotOffsetO, yRotOffset);
        } else {
            return 0.0F;
        }
    }
}
