package fuzs.paperdoll.common.config;

import fuzs.paperdoll.common.PaperDoll;
import net.minecraft.world.entity.player.Player;

public enum DisplayAction {
    SPRINTING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.sprinting;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return player.canSpawnSprintParticle();
        }
    },
    SWIMMING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.swimming;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return !player.isVisuallyCrawling() && player.isVisuallySwimming() && player.getSwimAmount(1.0F) > 0.0F;
        }
    },
    CRAWLING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.crawling;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return player.isVisuallyCrawling();
        }
    },
    CROUCHING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.crouching;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return !isRiding && player.isCrouching();
        }
    },
    FLYING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.flying;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return player.getAbilities().flying;
        }
    },
    GLIDING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.gliding;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return player.isFallFlying();
        }
    },
    RIDING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.riding;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return player.isPassenger();
        }
    },
    SPIN_ATTACKING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.spinAttacking;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return player.isAutoSpinAttack();
        }
    },
    USING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.usingItem;
        }

        @Override
        boolean isPerformingAction(Player player, boolean isRiding) {
            return player.isUsingItem();
        }
    };

    private static final DisplayAction[] VALUES = values();

    abstract boolean isActionEnabled(ClientConfig.DisplayActions displayActions);

    abstract boolean isPerformingAction(Player player, boolean isRiding);

    public static boolean isActive(Player player, boolean isRiding) {
        ClientConfig.DisplayActions displayActions = PaperDoll.CONFIG.get(ClientConfig.class).displayActions;
        for (DisplayAction displayAction : VALUES) {
            if (displayAction.isActionEnabled(displayActions) && displayAction.isPerformingAction(player, isRiding)) {
                return true;
            }
        }

        return false;
    }
}
