package com.raku.apple_test.generics;

/**
 * ばななを受け取って表示する、上限制約付きラッパークラス。
 */
@SuppressWarnings("unused")
public class BlueBanana<M extends GenericsBanana<?>> {
    private final M fruit;

    public BlueBanana(M fruit) {
        this.fruit = fruit;
    }

    public M fruit() {
        return fruit;
    }

    public void eatFruit() {
        System.out.println("食べた果物： " + fruit.getName()
                + "\n 色： " + fruit.getColor()
                + "\n 重さ：" + fruit.getWeight() + " g");
    }
}
