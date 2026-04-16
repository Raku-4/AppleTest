package com.raku.apple_test.double_constructor.pro.food.vegetable;

import com.raku.apple_test.double_constructor.pro.food.vegetable.vegetables.Broccoli;
import com.raku.apple_test.double_constructor.pro.food.vegetable.vegetables.Onion;

public final class VegetableCatalog {
    private VegetableCatalog() {}

    // ONION は「Onion を生成するための型情報(EntityType)」を保持する定数。
    // Onion::new はコンストラクタ参照であり、EntityFactory<Onion> として渡される。
    // ここで EntityFactory 型になるのは Onion クラス本体ではなく Onion::new 側。
    // Onion クラスのコンストラクタの引数と、EntityFactory のメソッドcreate の引数が一致しているので、
    // Onion::new をFactory 型としてEntityType に渡すことができる。
    // id に入る値: "onion"
    // factory に入る値: Onion::new (type, level) -> new Onion(type, level)
    // ONION.create(level) の戻り値: Onion
    public static final VegetableType<Onion> ONION =
            VegetableType.of("onion", Onion::new);
                              // id, EntityFactory

    public static final VegetableType<Broccoli> BROCCOLI =
            VegetableType.of("broccoli", Broccoli::new);
}