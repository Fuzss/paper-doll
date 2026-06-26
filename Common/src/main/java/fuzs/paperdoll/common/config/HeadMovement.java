package fuzs.paperdoll.common.config;

public enum HeadMovement {
    YAW,
    PITCH,
    BOTH;

    public boolean yaw() {
        return this == YAW || this == BOTH;
    }

    public boolean pitch() {
        return this == PITCH || this == BOTH;
    }
}
