package com.raku.fruitGame.fruit.functionalClass.userMethod;

import com.raku.fruitGame.fruit.functionalClass.utility.FruitInterface;

/**
 * ユーザー定義の新規果物を表すクラス。
 *
 * <p>enum で型制約された既存果物と違い、色は String で自由入力を許可します。</p>
 */
public class CustomFruit implements FruitInterface<String>, Eat {
    /** 果物名 (作成後は変更しない) */
    private final String name;

    /** 色 (自由文字列) */
    private String color;

    /** 現在ロットの総重量 */
    private long weight;

    /** 累積摂取量 */
    private long ate;

    public CustomFruit(String name, String color, long weight) {
        this.name = name;
        this.color = color;
        this.weight = Math.max(0, weight);
        this.ate = 0;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setColor(String color) {
        // null は空文字に置換して呼び出し側の null チェック負担を下げる
        this.color = color == null ? "" : color;
    }

    @Override
    public void setAte(long ate) {
        this.ate = Math.max(0, ate);
    }

    @Override
    public void setWeight(long weight) {
        this.weight = Math.max(0, weight);
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
    public String getColor() {
        return color;
    }

    @Override
    public void eat(long grams) {
        // eat 操作は setAte を再利用してバリデーションを共通化
        setAte(this.ate + Math.max(0, grams));
    }
}
