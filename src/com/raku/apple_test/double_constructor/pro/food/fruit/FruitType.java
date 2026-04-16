package com.raku.apple_test.double_constructor.pro.food.fruit;

import com.raku.apple_test.double_constructor.pro.AllEntity;
import com.raku.apple_test.double_constructor.pro.EntityType;
import org.jetbrains.annotations.NotNull;

/**
 * FruitEntity 専用の EntityType。Minecraft の登録型を学習用に簡略化したクラス。
 * <p></>
 * このクラスは、果物をりんごやバナナのような、別型として安全に扱えるようにするための仕組み（型付きの土台）です。
 */
// FruitType は EntityType の「果物カテゴリ版」。
// これにより、EntityFactory インターフェースを利用できるようになります。
public final class FruitType<T extends AllEntity> extends EntityType<T> {

    // FruitType のstatic メソッド of(...) 経由でのみ 果物を生成させる。
    private FruitType(@NotNull String id, @NotNull EntityFactory<T> factory) {
        // 親の EntityType に、箱ラベル(id)と生成方法(factory)を渡して初期化。
        super(id, factory);
    }

    // 静的ファクトリ: FruitType を作る唯一の公開入口。
    public static <T extends AllEntity> @NotNull FruitType<T> of(@NotNull String id, @NotNull EntityFactory<T> factory) {
        // 例: FruitType.of("banana", Banana::new)
        return new FruitType<>(id, factory); // 果物を仕分けられるように、id を指定する。factory は果物の生成方法を決めること。
    }

    @Override
    public @NotNull String getCategoryMark() {
        return "[F]";
    }
}