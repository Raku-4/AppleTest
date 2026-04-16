package com.raku.apple_test.double_constructor.pro.food.fruit;

import com.raku.apple_test.double_constructor.pro.food.fruit.fruits.Apple;
import com.raku.apple_test.double_constructor.pro.food.fruit.fruits.Banana;

/**
 * 学習用レジストリ。Minecraft の RegistryObject 相当の位置付け。
 * このクラス自身は、FruitType に従った果物たちを名前とともに登録するだけのもの。
 */
public final class FruitCatalog {
    private FruitCatalog() {
    }

    /**
     *ここでAPPLE は「りんごを作るための型付きレシピ」を持っています。
     * Apple::new (コンストラクタ参照) を保存しているので、create(level) したら内部でそのコンストラクタが呼び出されます。
     */
    public static final FruitType<Apple> APPLE =
            FruitType.of("apple", Apple::new);

    public static final FruitType<Banana> BANANA =
            FruitType.of("banana", Banana::new);
}