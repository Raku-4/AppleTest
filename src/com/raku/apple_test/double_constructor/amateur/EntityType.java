package com.raku.apple_test.double_constructor.amateur;

import com.raku.apple_test.double_constructor.pro.AllEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * amateur 版: FruitType/VegetableType 分類なし、EntityType を直接継承するだけの最小形。
 * pro の FruitType/VegetableType より前に理解すべき基本形です。
 */
public class EntityType<T extends AllEntity> extends com.raku.apple_test.double_constructor.pro.EntityType<T> {

    private EntityType(@NotNull String id, @NotNull EntityFactory<T> factory) {
        // EntityType に id と factory を渡すだけ。
        super(id, factory);
    }

    @Contract("_, _ -> new")
    public static <T extends AllEntity> @NotNull EntityType<T> of(@NotNull String id, @NotNull EntityFactory<T> factory) {
        // 入力: id=例 "apple", factory=例 Apple::new
        // 戻り値: EntityType<T>。例 EntityType<Apple>
        return new EntityType<>(id, factory);
    }
}

