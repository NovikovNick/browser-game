package com.metalheart.service.impl;

import com.metalheart.model.common.CollisionResult;
import com.metalheart.model.common.Line;
import com.metalheart.service.CollisionDetectionService;
import org.springframework.stereotype.Service;

@Service
public class CollisionDetectionServiceImpl implements CollisionDetectionService {

    @Override
    public CollisionResult detectCollision(Line a, Line b) {

        boolean sign = false;

        if (a.getStart() > a.getEnd()) {
            a = new Line(a.getEnd(), a.getStart());
        }

        if (b.getStart() > b.getEnd()) {
            b = new Line(b.getEnd(), b.getStart());
        }

        if ((b.getEnd() == a.getStart())
            || (a.getEnd() == b.getStart())
            || (a.getEnd() == b.getEnd())
            || (a.getStart() == b.getStart())) {
            return CollisionResult.builder().collide(true).depth(0).build();
        }

        if (a.getEnd() > b.getEnd()) {
            Line tmp = b;
            b = a;
            a = tmp;
            sign = true;
        }

        if (a.getEnd() > b.getStart()) {
            return CollisionResult.builder().collide(true).depth(a.getEnd() - b.getStart()).sign(sign).build();
        }

        return CollisionResult.builder().collide(false).build();
    }

}
