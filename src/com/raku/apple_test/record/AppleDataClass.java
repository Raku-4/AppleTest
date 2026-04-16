package com.raku.apple_test.record;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * recordとの比較用: 同じ情報を通常classで書くと冗長になりやすい例。
 */
public final class AppleDataClass {
    private final @NotNull String name;
    private final int sweetness;
    private final @NotNull String origin;

    public AppleDataClass(@NotNull String name, int sweetness, @Nullable String origin) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (sweetness < 0 || sweetness > 100) {
            throw new IllegalArgumentException("sweetness must be 0..100");
        }
        this.name = name;
        this.sweetness = sweetness;
        this.origin = (origin == null || origin.isBlank()) ? "unknown" : origin;
    }

    public @NotNull String name() {
        return name;
    }

    public int sweetness() {
        return sweetness;
    }

    public @NotNull String origin() {
        return origin;
    }

    public @NotNull String tasteLabel() {
        if (sweetness >= 80) return "very sweet";
        if (sweetness >= 50) return "balanced";
        return "tart";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppleDataClass that)) return false;
        return sweetness == that.sweetness
                && Objects.equals(name, that.name)
                && Objects.equals(origin, that.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sweetness, origin);
    }

    @Override
    public @NotNull String toString() {
        return "AppleDataClass{" +
                "name='" + name + '\'' +
                ", sweetness=" + sweetness +
                ", origin='" + origin + '\'' +
                '}';
    }
}
