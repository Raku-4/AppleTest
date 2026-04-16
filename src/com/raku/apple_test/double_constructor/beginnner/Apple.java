package com.raku.apple_test.double_constructor.beginnner;

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 次は、ダブルコンストラクタについて解説いたします。
 * ダブルコンストラクタとは、同じクラス内に複数のコンストラクタを定義することができる機能です。
 * 例えば、
 * 「この赤リンゴは、色と重さがあってほしいけど、
 * この青リンゴは、色と重さと甘さがわかってほしい。」
 * というようにリンゴの種類ごとに使う設計図を変えることができます。
 * ですが、こう思いますか。
 * 「それなら別々のクラスで、それぞれ違う設計図を一個持っていればいいのでは。」と。
 * しかし、赤リンゴと青リンゴのように、同じリンゴであるなら一つのクラスでまとまっていたほうが、
 * もしJava で大きなものを作っていくときには整理整頓されやすいです。
 */
public class Apple extends com.raku.apple_test.all_argument.Apple {
    private AppleEnumColor color;
    private int weight;
    private String taste;
    private int suger_content;
    private int ate;
    private String radius;

    public Apple(@NotNull AppleEnumColor color, int weight, @NotNull String taste, int ate) {
        super(color, weight, taste, ate);
        this.color = color;
        this.weight = weight;
        this.taste = taste;
        this.ate = ate;
    }

    public Apple(@NotNull AppleEnumColor color, int weight, @NotNull String taste, int ate, int suger_content, String radius) {
        this(color, weight, taste, ate); // 一番上のコンストラクタに同じ情報を入れる。
        this.suger_content = suger_content;
        this.radius = radius;
    }

    public int getSuger_content() {
        return suger_content;
    }

    public int  getWeight() {
        return weight;
    }

    public AppleEnumColor getColor() {
        return color;
    }

    public String getRadius() { return radius; }

    @Override
    public String getTaste() {
        return taste;
    }

    @Override
    public void setTaste(String taste) {
        this.taste = taste;
    }

    public int getAte() {
        return ate;
    }

    @Override
    public void setAte(int ate) {
        this.ate = ate;
        this.setWeight(weight-ate);
    }

    public void setColor(@Nullable AppleEnumColor color) {
        if (color != null) {this.color = color; return;}
        else throw new IllegalArgumentException("color is null");
    }

    public void setWeight(int weight) {
        if (weight > 0) this.weight = weight;
        else throw new IllegalArgumentException("weight is 0 or minus");
    }

    public void setSuger_content(int suger_content) {
        if (suger_content > 0) this.suger_content = suger_content;
        else throw new IllegalArgumentException("suger_content is 0 or minus");
    }
}
