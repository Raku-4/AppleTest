package com.raku.apple_test.double_constructor.pro.food.fruit.fruits;

import com.raku.apple_test.double_constructor.pro.AllEntity;
import com.raku.apple_test.double_constructor.pro.EntityType;
import com.raku.apple_test.double_constructor.pro.Level;
import com.raku.apple_test.double_constructor.pro.food.fruit.FruitCatalog;
import org.jetbrains.annotations.NotNull;

/**
 * バナナのクラス。AllEntity を継承しているため、リンゴと同様、ゲームロジックで必要な共通の動作を実装できる。
 * 様々な種類のバナナを追加したいときは、このクラスを用いてインスタンス化する。
 */
public class Banana extends AllEntity {
    private String color;
    private int sugarBrix;
    private int kcal;

    // Forge/MC の EntityFactory 互換シグネチャを意識した必須コンストラクタ
    public Banana(@NotNull EntityType<? extends Banana> type, @NotNull Level level) {
        super(type, level);
        // 以下の三つは後でsetter 経由で設定できる。
        this.color = "unknown";
        this.sugarBrix = 0;
        this.kcal = 0;
    }

    // ゲームロジック側で使いやすい、追加要素を持っているコンストラクタ（要素を追加するところは、mod の醍醐味。）
    public Banana(@NotNull Level level, @NotNull String color, int sugarBrix) {
        this(FruitCatalog.BANANA, level);
        setColor(color);
        setSugarBrix(sugarBrix);
    }

    public String getColor() {
        return color;
    }

    public int getSugarBrix() {
        return sugarBrix;
    }

    public int getKcal() {
        return kcal;
    }

    public void setColor(@NotNull String color) {
        if (color.isBlank()) {
            throw new IllegalArgumentException("color is blank");
        }
        this.color = color;
    }

    public void setSugarBrix(int sugarBrix) {
        if (sugarBrix < 0) throw new IllegalArgumentException("sugarBrix must be >= 0");

        this.sugarBrix = sugarBrix;
    }

    public void setKcal(int kcal) {
        if (kcal < 0) {
            throw new IllegalArgumentException("kcal must be >= 0");
        }
        this.kcal = kcal;
    }

    @Override
    public @NotNull String describe() {
        return "Banana{" +
                "type=" + getType().getLabeledId() +
                ", level=" + getLevel().name() +
                ", color='" + color + '\'' +
                ", sugarBrix=" + sugarBrix +
                ", kcal=" + kcal +
                '}';
    }
}
