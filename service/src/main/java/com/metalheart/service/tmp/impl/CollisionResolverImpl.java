package com.metalheart.service.tmp.impl;

import com.metalheart.model.common.Vector2d;
import com.metalheart.service.tmp.Body;
import com.metalheart.service.tmp.CollisionResolver;
import com.metalheart.service.tmp.Manifold;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CollisionResolverImpl implements CollisionResolver {

    @Override
    public void resolve(Set<Manifold> manifolds) {
        for (Manifold manifold : manifolds) {
            resolve(manifold);
        }
    }

    public void resolve(Manifold manifold) {

        Body a = manifold.getA();
        Body b = manifold.getB();
        Vector2d normal = manifold.getNormal();
        float depth = manifold.getPenetration();

        // Вычисляем относительную скорость
        Vector2d rv = b.getVelocity().minus(a.getVelocity());

        // Вычисляем относительную скорость относительно направления нормали
        float velAlongNormal = rv.dotProduct(normal);

        // Не выполняем вычислений, если скорости разделены
        if(velAlongNormal > 0)
            return;

        // Вычисляем упругость
        float e = Math.min(a.getMaterial().getRestitution(), b.getMaterial().getRestitution());

        // Вычисляем скаляр импульса силы
        float j = -(1 + e) * velAlongNormal;
        j /= a.getInvMass() + b.getInvMass();

        // Прикладываем импульс силы
        float massSum = a.getMass() + b.getMass();
        float ratio = a.getMass() / massSum;
        Vector2d impulse = normal.scale(j * ratio);
        a.setVelocity(a.getVelocity().minus(impulse.scale(a.getInvMass())));
        b.setVelocity(b.getVelocity().plus(impulse.scale(b.getInvMass())));

        // position correction
        float percent = 0.2f; // обычно от 20% до 80%
        float slop = 0.01f; // обычно от 0.01 до 0.1
        Vector2d correction = normal.scale(Math.max( depth - slop, 0.0f ) / (a.getInvMass() + b.getInvMass()) * percent);
        a.setPos(a.getPos().minus(correction.scale(a.getInvMass())));
        b.setPos(b.getPos().plus(correction.scale(b.getInvMass())));
    }
}
