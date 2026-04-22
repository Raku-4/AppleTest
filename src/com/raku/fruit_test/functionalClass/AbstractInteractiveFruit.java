package com.raku.fruit_test.functionalClass;

import com.raku.fruit_test.FruitInterface;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * `AppleInteractive` / `BananaInteractive` に共通する
 * 色・重さ・摂取量の実装をまとめた基底クラス。
 */
public abstract class AbstractInteractiveFruit<E> implements FruitInterface<E> {
    private E color;
    private long weight;
    private long ate;

    protected AbstractInteractiveFruit(@NotNull E color, long weight, long ate) {
        setColor(color);
        setWeight(weight);
        setAte(ate);
    }

    protected abstract @NotNull String colorLabel(@NotNull E color);

    @Override
    public void setColor(@NotNull E color) {
        this.color = Objects.requireNonNull(color, "色はnull にできません。");
    }

    @Override
    public void setAte(long ate) {
        this.ate = ate;
    }

    @Override
    public void setWeight(long weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("重さは正の数でなければいけません");
        }
        this.weight = weight;
    }

    @Override
    public long getRemainingWeight() {
        return weight - ate;
    }

    @Override
    public long getAte() {
        return ate;
    }

    @Override
    public @NotNull String getColor() {
        return colorLabel(color);
    }
}

