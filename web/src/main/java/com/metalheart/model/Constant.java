package com.metalheart.model;

public final class Constant {

    public static final String APP_NAME = "/game";

    public static final String INPUT_BROKER = "/frontend";
    public static final String INPUT_PLAYER_STATE ="/update";

    public static final String OUTPUT_BROKER = "/backend";
    public static final String OUTPUT_PLAYER_STATE = OUTPUT_BROKER + "/player/list";

    private Constant() {
        throw new UnsupportedOperationException();
    }
}
