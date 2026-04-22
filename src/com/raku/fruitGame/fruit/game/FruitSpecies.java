package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum FruitSpecies {
    APPLE("apple", "りんご", "赤"),
    BANANA("banana", "ばなな", "黄"),
    ORANGE("orange", "おれんじ", "橙"),
    GRAPE("grape", "ぶどう", "紫");

    private final String baseName;
    private final String japaneseName;
    private final String ripeColor;

    FruitSpecies(String baseName, String japaneseName, String ripeColor) {
        this.baseName = baseName;
        this.japaneseName = japaneseName;
        this.ripeColor = ripeColor;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getJapaneseName() {
        return japaneseName;
    }

    public String getRipeColor() {
        return ripeColor;
    }

    public boolean supportsPerfect() {
        return this == BANANA || this == ORANGE;
    }

    public @NotNull String iconPathFor(@NotNull FruitStage stage) {
        return switch (stage) {
            case UNRIPE -> "assets/fruitGame/textures/fruits/unripe_" + baseName + ".png";
            case RIPE -> "assets/fruitGame/textures/fruits/ripe_" + baseName + ".png";
            case PERFECT -> supportsPerfect()
                    ? "assets/fruitGame/textures/fruits/perfect_ripe_" + baseName + ".png"
                    : "assets/fruitGame/textures/fruits/ripe_" + baseName + ".png";
            case EMPTY -> "assets/fruitGame/textures/fruits/ripe_apple.png";
        };
    }

    public static @NotNull FruitSpecies fromName(@Nullable String name) {
        if (name == null) {
            return APPLE;
        }
        String key = name.toLowerCase();
        if (key.contains("banana") || key.contains("ばなな")) {
            return BANANA;
        }
        if (key.contains("orange") || key.contains("おれんじ") || key.contains("オレンジ")) {
            return ORANGE;
        }
        if (key.contains("grape") || key.contains("ぶどう") || key.contains("ブドウ")) {
            return GRAPE;
        }
        return APPLE;
    }
}

