package com.metalheart.model.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

import static java.util.stream.Collectors.toList;

@Data
public class Polygon2d {

    private final List<Vector2d> points;

    public Polygon2d(List<Vector2d> points) {
        this.points = Collections.unmodifiableList(points);
    }

    public Polygon2d(Vector2d... points) {
        this.points = Collections.unmodifiableList(Arrays.stream(points).collect(Collectors.toList()));
    }

    public Polygon2d withOffset(Vector2d offset) {
        return new Polygon2d(getPoints()
            .stream()
            .map(offset::plus)
            .collect(toList()));
    }
}
