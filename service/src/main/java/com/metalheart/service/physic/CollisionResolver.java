package com.metalheart.service.physic;

import com.metalheart.model.common.Manifold;
import java.util.Set;

public interface CollisionResolver {

    void resolve(Set<Manifold> manifolds);
}
