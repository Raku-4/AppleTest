package com.raku.apple_test.double_constructor.amateur;

import com.raku.apple_test.double_constructor.pro.food.fruit.fruits.Apple;
import com.raku.apple_test.double_constructor.pro.food.fruit.fruits.Banana;

/**
 * amateur 版レジストリ。
 * pro の FruitCatalog/VegetableCatalog のように分類せず、
 * シンプルに EntityType を使ってリンゴとバナナを登録するだけ。
 */
public final class Catalog {
    private Catalog() {
    }

    // 入力: id="apple", factory=SimpleApple::new
    // 戻り値: SimpleEntityType<SimpleApple>
    // APPLE.create(level) の戻り値: SimpleApple インスタンス
    public static final EntityType<Apple> APPLE =
            EntityType.of("apple", Apple::new);

    public static final EntityType<Banana> BANANA =
            EntityType.of("banana", Banana::new);



    // banana も同様に登録可能（ここでは省略し、Main で説明）
}

