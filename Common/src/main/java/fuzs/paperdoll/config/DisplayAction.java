package fuzs.paperdoll.config;

import net.minecraft.world.entity.player.Player;

public enum DisplayAction {
    SPRINTING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.sprinting;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.canSpawnSprintParticle();
        }
    },
    SWIMMING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.swimming;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return !player.isVisuallyCrawling() && player.isVisuallySwimming() && player.getSwimAmount(1.0F) > 0.0F;
        }
    },
    CRAWLING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.crawling;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.isVisuallyCrawling();
        }
    },
    CROUCHING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.crouching;
        }

        @Override
        boolean isPerformingAction(Player player, int remainingRidingTicks) {
            return this.isPerformingAction(player);
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.isCrouching();
        }
    },
    FLYING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.flying;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.getAbilities().flying;
        }
    },
    GLIDING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.gliding;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.isFallFlying();
        }
    },
    RIDING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.riding;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.isPassenger();
        }
    },
    SPIN_ATTACKING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.spinAttacking;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.isAutoSpinAttack();
        }
    },
    USING {
        @Override
        boolean isActionEnabled(ClientConfig.DisplayActions displayActions) {
            return displayActions.usingItem;
        }

        @Override
        boolean isPerformingAction(Player player) {
            return player.isUsingItem();
        }
    };

    static final DisplayAction[] VALUES = values();

    abstract boolean isActionEnabled(ClientConfig.DisplayActions displayActions);

    abstract boolean isPerformingAction(Player player);

    boolean isPerformingAction(Player player, int remainingRidingTicks) {
        return remainingRidingTicks == 0 && this.isPerformingAction(player);
    }

    public static boolean isActive(ClientConfig.DisplayActions displayActions, Player player, int remainingRidingTicks) {
        for (DisplayAction displayAction : VALUES) {
            if (displayAction.isActionEnabled(displayActions) && displayAction.isPerformingAction(player,
                    remainingRidingTicks)) {
                return true;
            }
        }

        return false;
    }
}
