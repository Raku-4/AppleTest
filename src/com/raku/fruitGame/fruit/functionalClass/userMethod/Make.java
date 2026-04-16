package com.raku.fruitGame.fruit.functionalClass.userMethod;

/**
 * 果物生成処理を抽象化する関数型インターフェース。
 *
 * @param <T> 生成される果物型
 */
@FunctionalInterface
public interface Make<T> {
    /**
     * 入力情報から新しい果物インスタンスを生成します。
     */
    T make(String name, String color, long weight);
}
