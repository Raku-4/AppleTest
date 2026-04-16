package com.raku.apple_test.double_constructor.pro.food.vegetable.vegetables;

import com.raku.apple_test.double_constructor.pro.AllEntity;
import com.raku.apple_test.double_constructor.pro.EntityType;
import com.raku.apple_test.double_constructor.pro.Level;
import com.raku.apple_test.double_constructor.pro.food.vegetable.VegetableCatalog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Onion extends AllEntity {

    private String kind;
    private @Nullable String color;
    private int weight;

    public Onion(@NotNull EntityType<? extends AllEntity> type, @NotNull Level level) {
        // 入力: type=例 VegetableCatalog.ONION, level=例 field
        super(type, level);
        this.kind = "unknown";
        this.color = null;
        this.weight = 0;
    }

    public Onion(@NotNull Level level, String kind, @Nullable String color, int weight) {
        // 入力: level/kind/color/weight を受け取り、内部で ONION 型を使って初期化する。
        this(VegetableCatalog.ONION, level);
        this.kind = kind;
        this.color = color;
        this.weight = weight;
    }

    public String getKind() {
        // 戻り値: 現在の種類文字列。例 "エシャロット"
        return kind;
    }

    public void setKind(@NotNull String kind) {
        if (kind.isBlank()) throw  new IllegalArgumentException("種類は何もなしにはできません。");
        this.kind = kind;
    }

    public void setColor(@NotNull String color) {
        this.color = color;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public @NotNull String describe() {
        // 戻り値: type/level/color/weight/kind を含む表示用 String
        return "Onion{" +
                "type=" + getType().getLabeledId() +
                ", level=" + getLevel().name() +
                ", color='" + color + '\'' +
                ", weight=" + weight +
                ", kind='" + kind + '\'' +
                '}';
    }
}
