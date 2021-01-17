package com.metalheart.maze;

public enum MazeDoorDirection {
    TOP, RIGHT, BOTTOM, LEFT;

    public MazeDoorDirection getOpposite() {
        switch (this) {
            case TOP:
                return BOTTOM;
            case LEFT:
                return RIGHT;
            case BOTTOM:
                return TOP;
            case RIGHT:
                return LEFT;
            default:
                throw new IllegalStateException();
        }
    }
}
