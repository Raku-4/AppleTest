package com.raku.apple_test.for_sentence;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record FruitRecord(String fruitName, String color, long weight, int ID) {

    public FruitRecord {
        validateColor(color);
        validateWeight(weight);
    }

    @NotNull Map<Record, Integer> FruitMap(Record fruit, int id) {
        Map<Record, Integer> map = new HashMap<>();
        map.put(fruit, id);
        return map;
    }


    public void validateColor(Object color) {
        if (color instanceof String Color && !Color.isEmpty()) {
            return;
        }

        throw new IllegalArgumentException("Color must be a string.");
    }

    public void validateWeight(Object weight) {
        if (weight instanceof Long Weight && Weight > 0) {
            return;
        }

        throw new IllegalArgumentException("Weight must be a long and positive integer.");
    }

    public int getID() {
        return ID;
    }
}