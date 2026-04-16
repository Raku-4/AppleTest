package com.raku.apple_test.generics;

/**
 * ジェネリクス学習用のばななモデル。
 *
 * <p>りんご版と同じ仕組みを持ちますが、
 * こちらは「ばなな用の箱」を別クラスとして示すために分けています。</p>
 *
 * @param <TColor> 色を表す型
 */
public class GenericsBanana<TColor> {
    private final String name;
    private TColor color;
    private int weight;
    private int ate;

    public GenericsBanana(String name, TColor color, int weight) {
        this.name = name;
        this.color = color;
        this.weight = Math.max(0, weight);
        this.ate = 0;
    }

    public String name() {
        return name;
    }

    public TColor color() {
        return color;
    }

    public int weight() {
        return weight;
    }

    public int remaining() {
        return weight - ate;
    }

    public int ate() {
        return ate;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setWeight(int weight) {
        this.weight = Math.max(0, weight);
    }

    public void eat(int grams) {
        this.ate = Math.max(0, this.ate + grams);
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return String.valueOf(this.color);
    }

    public int getWeight() {
        return this.weight;
    }

    public String describe() {
        return "名前=" + name + ", 色=" + getColor() + ", 重さ=" + weight + "g, 食べた量=" + ate + "g, 残り=" + remaining() + "g";
    }
}
