package com.raku.fruitGame.fruit.functionalClass.userMethod;

/**
 * 「食べる」という1操作を関数として扱うためのインターフェース。
 *
 * <p>将来的にラムダ式やメソッド参照で注入しやすくする意図があります。</p>
 */
@FunctionalInterface
public interface Eat {
    /**
     * 指定グラム分を食べる操作を実行します。
     */
    void eat(long grams);
}
