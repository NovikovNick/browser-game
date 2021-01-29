package com.metalheart.model.game;


public enum Material {

    ROCK(0.6f, 0.1f),
    WOOD(0.3f, 0.2f),
    METAL(1.2f, 0.05f),
    BOUNCY_BALL(0.3f, 0.8f),
    SUPER_BALL(0.3f, 0.95f),
    PILLOW(0.1f, 0.2f),
    STATIC(0.0f, 0.4f);

    private final float density;
    private final float restitution;

    Material(float density, float restitution) {
        this.density = density;
        this.restitution = restitution;
    }

    public float getDensity() {
        return density;
    }

    public float getRestitution() {
        return restitution;
    }
}
