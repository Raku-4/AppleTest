package com.raku.apple_test.constructor_reference;

import org.jetbrains.annotations.NotNull;

public record FApple(String color, int sugarBrix) {
    public FApple() {
        this("unknown", 0);
    }

    public FApple(String color) {
        this(color, 0);
    }
}

