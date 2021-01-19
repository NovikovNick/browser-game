package com.metalheart.model.struct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LimitedList<T> {

    private final int limit;

    private Object[] data;
    private int currentIndex;

    public LimitedList(int limit) {
        this.limit = limit;
        init();
    }

    public void add(T elem) {
        data[currentIndex++ % limit] = elem;
    }

    public List<T> toList() {
        int index = currentIndex;
        List<T> res = new ArrayList<>(limit);
        for (int i = 0; i < limit; i++) {

            if (index > 0) {
                Object item = data[--index % limit];
                if (item != null) {
                    res.add((T) item);
                }
            }
        }
        return res;
    }

    public List<T> pollAll() {
        List<T> res = toList();
        init();
        return res;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    private void init() {
        this.currentIndex = 0;
        this.data = new Object[limit];
    }
}
