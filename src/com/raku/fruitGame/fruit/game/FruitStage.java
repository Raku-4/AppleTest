package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;

public enum FruitStage implements CharSequence {
    EMPTY(),
    UNRIPE(),
    RIPE(),
    PERFECT();

    public static FruitStage fromString(String string) {
        for (FruitStage fruitStage : FruitStage.values()) {
            if (fruitStage.toString().equals(string)) return fruitStage;

        }
        return EMPTY;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return EMPTY;
    }
}

