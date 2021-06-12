package com.metalheart.model.game;

import com.metalheart.model.common.AABB2d;
import com.metalheart.model.common.Material;
import com.metalheart.model.common.Polygon2d;
import com.metalheart.model.common.Vector2d;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class GameObject implements Cloneable{

    private final long id;
    private final Polygon2d shape;
    private final Material material;

    private final float mass;
    private final float invMass;

    private Vector2d force;
    private Vector2d velocity;

    private Vector2d pos;

    public GameObject(long id, Polygon2d shape, Material material, Vector2d pos) {

        this.id = id;

        this.shape = shape;
        this.material = material;

        this.mass = calculateMass(shape, material);
        this.invMass = this.mass == 0 ? 0 : 1f / this.mass;

        this.force = Vector2d.ZERO_VECTOR;
        this.velocity = Vector2d.ZERO_VECTOR;

        this.pos = pos;
    }

    public Polygon2d getShapePositioned() {
        return shape.withOffset(pos);
    }

    private float calculateMass(Polygon2d shape, Material material) {

        AABB2d aabb = AABB2d.of(shape.getPoints());
        Vector2d max = aabb.getMax();
        Vector2d min = aabb.getMin();
        float area = (max.getD0() - min.getD0()) * (max.getD1() - min.getD1());
        float mass = material.getDensity() * area / 50;
        return mass;
    }

    public GameObject clone() {
        GameObject gameObject = new GameObject(id, shape, material, pos);
        gameObject.setForce(force);
        gameObject.setVelocity(velocity);
        return gameObject;
    }

    public GameObjectType getType() {
        return GameObjectType.NONE;
    };
}