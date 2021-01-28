package com.metalheart.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import static java.lang.Math.sqrt;

@Data
@AllArgsConstructor
public class Vector2d {

    public static final Vector2d ZERO_VECTOR = Vector2d.of(0, 0);
    public static final Vector2d UNIT_VECTOR_D0 = Vector2d.of(1, 0);
    public static final Vector2d UNIT_VECTOR_D1 = Vector2d.of(0, 1);

    private final float d0, d1;

    public static Vector2d of(float d0, float d1) {
        return new Vector2d(d0, d1);
    }

    public Vector2d normalize() {
        if(d0 == 0 && d1 == 0) return ZERO_VECTOR;
        return scale(1 / magnitude());
    }

    public Vector2d plus(Vector2d v) {
        return new Vector2d(d0 + v.d0, d1 + v.d1);
    }

    public float dotProduct(Vector2d v) {
        return d0 * v.d0 +  d1 * v.d1;
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

    @Override
    public String toString() {
        return String.format("[%.3f, %.3f]", d0, d1);
    }
}
