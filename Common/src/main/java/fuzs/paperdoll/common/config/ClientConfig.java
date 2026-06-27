package fuzs.paperdoll.common.config;

import fuzs.puzzleslib.common.api.client.gui.v2.AnchorPoint;
import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = "Display the paper doll while performing these actions.")
    public final DisplayActions displayActions = new DisplayActions();
    @Config(description = "Scale of the paper doll. Also affected by video settings GUI scale.")
    @Config.IntRange(min = 1, max = 24)
    public int scale = 4;
    @Config(description = "Offset on x-axis from the original doll position.")
    public int offsetX = 0;
    @Config(description = "Offset on y-axis from the original doll position.")
    public int offsetY = 0;
    @Config(description = "Amount of ticks the paper doll will be kept on screen after its display conditions are no longer met. Set to 0 to always display the doll.")
    @Config.IntRange(min = 0)
    public int displayTime = 15;
    @Config(description = "Define a position on the screen to align the paper doll display to.")
    public AnchorPoint anchorPoint = AnchorPoint.TOP_LEFT;
    @Config(description = "Only show the paper doll when in first-person mode.")
    public boolean firstPersonOnly = true;
    @Config(description = "Define how the paper doll head is allowed to rotate while moving around.")
    public final HeadMovement headMovement = new HeadMovement();

    public boolean isAlwaysDisplayed() {
        return this.displayTime == 0;
    }

    public static class DisplayActions implements ConfigCore {
        @Config(description = "Display paper doll while sprinting.")
        public boolean sprinting = true;
        @Config(description = "Display paper doll while swimming in water.")
        public boolean swimming = true;
        @Config(description = "Display paper doll while crawling in a tight space.")
        public boolean crawling = true;
        @Config(description = "Display paper doll while crouching.")
        public boolean crouching = true;
        @Config(description = "Display paper doll while flying in creative mode.")
        public boolean flying = true;
        @Config(description = "Display paper doll while gliding using an elytra.")
        public boolean gliding = true;
        @Config(description = "Display paper doll while riding a vehicle.")
        public boolean riding = false;
        @Config(description = "Display paper doll while spin attacking with a trident.")
        public boolean spinAttacking = false;
        @Config(description = "Display paper doll while using an item like a bow.")
        public boolean usingItem = false;
    }

    public static final class HeadMovement implements ConfigCore {
        @Config(description = "Set the axis the player head may follow movement on.")
        public HeadRotation rotationAxis = HeadRotation.YAW;
        @Config(description = "The default position in degrees the paper doll head will slowly rotate back towards after movement.")
        @Config.DoubleRange(min = 0.0, max = 45.0)
        public double defaultRotation = 15.0;
        @Config(description = "The maximum position in degrees on the y-axis away from looking straight the paper doll head can be moved to.")
        @Config.DoubleRange(min = 0.0, max = 90.0)
        public double maxRotationYaw = 30.0;
        @Config(description = "The maximum position in degrees on the x-axis away from looking straight the paper doll head can be moved to.")
        @Config.DoubleRange(min = 0.0, max = 90.0)
        public double maxRotationPitch = 30.0;
    }
}
