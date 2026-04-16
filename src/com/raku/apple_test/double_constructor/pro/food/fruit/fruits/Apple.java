package com.raku.apple_test.double_constructor.pro.food.fruit.fruits;

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import com.raku.apple_test.double_constructor.pro.AllEntity;
import com.raku.apple_test.double_constructor.pro.EntityType;
import com.raku.apple_test.double_constructor.pro.Level;
import com.raku.apple_test.double_constructor.pro.food.fruit.FruitCatalog;
import org.jetbrains.annotations.NotNull;

/**
 * リンゴのクラス。AllEntity を継承しているため、ゲームロジックで必要な共通の動作を実装できる。
 * 様々な種類のリンゴを追加したいときは、このクラスを用いてインスタンス化する。
 */
public class Apple extends AllEntity {
    private AppleEnumColor color;
    private int sugarBrix;

    // Forge/MC の EntityFactory 互換シグネチャを意識した必須コンストラクタ
    // Main クラス側でこのコンストラクタを呼び出すとき、EntityType と Level を渡す必要がある。
    // これも、ゲームロジック側で必要な共通の動作を実装するためのもの。
    // ちなみに type とは、ゲーム内でのエンティティの種類を識別するためのもので、
    // Level とは、ゲーム内でのエンティティの存在する場所を示すもの。
    public Apple(@NotNull EntityType<? extends Apple> type, @NotNull Level level) {
        super(type, level);
        this.color = AppleEnumColor.NULL;
        this.sugarBrix = 0;
    }

    // ゲームロジック側で使いやすい、追加の要素を持っているコンストラクタ（要素を追加するところは、mod の醍醐味）
    public Apple(@NotNull Level level, @NotNull AppleEnumColor color, int sugarBrix) {
        this(FruitCatalog.APPLE, level);
        setColor(color);
        setSugarBrix(sugarBrix);
    }

    public AppleEnumColor getColor() {
        return color;
    }

    public int getSugarBrix() {
        return sugarBrix;
    }

    public void setColor(@NotNull AppleEnumColor color) {
        this.color = color;
    }

    public void setSugarBrix(int sugarBrix) {
        if (sugarBrix < 0) {
            throw new IllegalArgumentException("sugarBrix must be >= 0");
        }
        this.sugarBrix = sugarBrix;
    }

    @Override
    public @NotNull String describe() {
        return "Apple{" +
                "type=" + getType().getLabeledId() +
                ", level=" + getLevel().name() +
                ", color='" + color + '\'' +
                ", sugarBrix=" + sugarBrix +
                '}';
    }
}
