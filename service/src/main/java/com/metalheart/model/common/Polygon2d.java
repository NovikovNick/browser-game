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

    public static Polygon2d rectangle() {
        return new Polygon2d(
            new Vector2d(0, 0),
            new Vector2d(100, 0),
            new Vector2d(100, 100),
            new Vector2d(0, 100)
        );
    }

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
