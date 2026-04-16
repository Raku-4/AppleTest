package com.raku.apple_test.abstract_apple;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractApple {
    private final int weight; // 共通フィールド

    public AbstractApple(int weight) {
        if (weight <= 0)
            throw new IllegalArgumentException("重さは正の数でなくてはいけません");
        this.weight = weight;
    }

    public int getWeight() { //具体メソッド
        return weight;
    }

    public abstract void eat(); //抽象メソッド（サブクラスで必ず実装）

    public @NotNull String describe(){
        return "重さ:" + weight + "g";
    }
}
