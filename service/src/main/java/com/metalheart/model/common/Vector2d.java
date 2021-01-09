package com.metalheart.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import static java.lang.Math.sqrt;

@Data
@AllArgsConstructor
public class Vector2d {

    public static final Vector2d ZERO_VECTOR = new Vector2d(0, 0);
    public static final Vector2d UNIT_VECTOR_D0 = new Vector2d(1, 0);
    public static final Vector2d UNIT_VECTOR_D1 = new Vector2d(0, 1);

    private final float d0, d1;

    public Vector2d normalize() {
        return scale(1 / magnitude());
    }

    public Vector2d plus(Vector2d v) {
        return new Vector2d(d0 + v.d0, d1 + v.d1);
    }

    public Vector2d scale(float s) {
        return new Vector2d(d0 * s, d1 * s);
    }

    public Vector2d reversed() {
        return scale(-1);
    }

    public float magnitude() {
        return (float) sqrt(d0 * d0 + d1 * d1);
    }
}
