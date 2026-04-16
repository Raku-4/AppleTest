package com.raku.apple_test.generics;

/**
 * りんごを受け取って表示する、上限制約付きラッパークラス。
 */
@SuppressWarnings("unused")
public record RedApple<M extends GenericsApple<?>>(M fruit) {
    public void eatFruit() {
        System.out.println("食べた果物： " + fruit.getName()
                + "\n 色： " + fruit.getColor()
                + "\n 重さ：" + fruit.getWeight() + " g");
    }
}

