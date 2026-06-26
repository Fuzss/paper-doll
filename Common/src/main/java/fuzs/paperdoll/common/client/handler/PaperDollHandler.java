package fuzs.paperdoll.common.client.handler;

import fuzs.paperdoll.common.PaperDoll;
import fuzs.paperdoll.common.client.util.PaperDollRenderer;
import fuzs.paperdoll.common.config.ClientConfig;
import fuzs.paperdoll.common.config.DisplayAction;
import fuzs.puzzleslib.common.api.client.gui.v2.AnchorPoint;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class PaperDollHandler {
    private static final float SPIN_BACK_SPEED = 10.0F;

    private static int remainingDisplayTicks;
    private static int ticksSinceLastRidden;
    private static float yRotOffset;
    private static float yRotOffsetO;

    public static void onEndClientTick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.isPaused()) {
            return;
        }

        // update display ticks
        boolean alwaysDisplayed = PaperDoll.CONFIG.get(ClientConfig.class).isAlwaysDisplayed();
        if (!alwaysDisplayed) {
            int displayTime = PaperDoll.CONFIG.get(ClientConfig.class).displayTime;
            int maxTicksSinceLastRidden = Math.max(0, displayTime - 1);
            if (DisplayAction.isActive(minecraft.player, ticksSinceLastRidden < maxTicksSinceLastRidden)) {
                remainingDisplayTicks = displayTime;
            } else if (remainingDisplayTicks > 0) {
                remainingDisplayTicks--;
            }

            // don't show paper doll in sneaking position after unmounting a vehicle / mount
            if (minecraft.player.isPassenger()) {
                ticksSinceLastRidden = 0;
            } else if (ticksSinceLastRidden < maxTicksSinceLastRidden) {
                ticksSinceLastRidden++;
            }
        }

        // reset rotation when no longer shown
        if (remainingDisplayTicks > 0 || alwaysDisplayed) {
            tickYRotOffset(minecraft.player);
        } else {
            yRotOffset = yRotOffsetO = 0;
        }
    }

    private static void tickYRotOffset(Player player) {
        yRotOffsetO = yRotOffset;
        // Apply rotation change from original entity.
        float maxRotationDegrees = (float) PaperDoll.CONFIG.get(ClientConfig.class).headMovement.maximumRotation;
        yRotOffset = Mth.clamp(yRotOffset + (player.yHeadRot - player.yHeadRotO) * 0.5F,
                -maxRotationDegrees,
                maxRotationDegrees);

        // Rotate back to origin; never overshoot zero.
        if (yRotOffset != 0.0F) {
            float nextYRotOffset = yRotOffset - (yRotOffset / SPIN_BACK_SPEED);
            if (yRotOffset < 0.0F) {
                yRotOffset = Math.min(0.0F, nextYRotOffset);
            } else if (yRotOffset > 0.0F) {
                yRotOffset = Math.max(0.0F, nextYRotOffset);
            }
        }
    }

    public static void extractPaperDoll(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Profiler.get().push("paperDoll");
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.hideGui && minecraft.player != null && !minecraft.player.isInvisible()
                && !minecraft.player.isSpectator()) {
            ClientConfig config = PaperDoll.CONFIG.get(ClientConfig.class);
            if (minecraft.options.getCameraType().isFirstPerson() || !config.firstPersonOnly) {
                if (remainingDisplayTicks > 0 || config.isAlwaysDisplayed()) {
                    float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
                    int scale = config.scale * 5;
                    int size = scale * 7 / 2;
                    AnchorPoint.Positioner positioner = config.anchorPoint.createPositioner(guiGraphics.guiWidth(),
                            guiGraphics.guiHeight(),
                            size,
                            size);
                    ScreenRectangle rectangle = positioner.getRectangle(config.offsetX, config.offsetY);
                    PaperDollRenderer.extractEntityInInventory(guiGraphics,
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
        float defaultRotationDegrees = (float) PaperDoll.CONFIG.get(ClientConfig.class).headMovement.defaultRotation;
        return isRight ? defaultRotationDegrees : -defaultRotationDegrees;
    }

    public static float getEntityXRot(LivingEntityRenderState state) {
        if (!PaperDoll.CONFIG.get(ClientConfig.class).headMovement.rotationAxis.pitch()
                || state.pose == Pose.FALL_FLYING) {
            return 7.5F;
        } else {
            return state.xRot;
        }
    }

    public static float getEntityYRot(float partialTick) {
        // The head rotation is used for doll rotation as it updates a lot more precisely than the body rotation.
        if (PaperDoll.CONFIG.get(ClientConfig.class).headMovement.rotationAxis.yaw()) {
            return Mth.rotLerp(partialTick, yRotOffsetO, yRotOffset);
        } else {
            return 0.0F;
        }
    }
}
