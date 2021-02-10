package com.metalheart.service.physic;

import com.metalheart.model.game.GameObject;
import com.metalheart.model.common.Manifold;
import java.util.Collection;
import java.util.Set;

public interface CollisionDetector {

    Set<Manifold> findCollision(Collection<GameObject> bodies);
}
