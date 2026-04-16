package com.raku.apple_test.capstone;

public enum FoodCategory {
    FRUIT("[F]"),
    VEGETABLE("[V]");

    private final String mark;

    FoodCategory(String mark) {
        this.mark = mark;
    }

    public String mark() {
        return mark;
    }
}

