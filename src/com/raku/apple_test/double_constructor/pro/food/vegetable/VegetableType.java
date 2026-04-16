package com.raku.apple_test.double_constructor.pro.food.vegetable;

import com.raku.apple_test.double_constructor.pro.AllEntity;
import com.raku.apple_test.double_constructor.pro.EntityType;
import org.jetbrains.annotations.NotNull;

public class VegetableType<T extends AllEntity> extends EntityType<T> {

    private VegetableType(@NotNull String id, @NotNull EntityFactory<T> factory) {
        // VegetableType は EntityType を継承し、生成ロジック(factory)は親に委譲する。
        super(id, factory);
    }

    public static <T extends AllEntity> @NotNull VegetableType<T> of(@NotNull String id, @NotNull EntityFactory<T> factory) {
        // ここで受け取る factory は「T を作る関数」。
        // 例: Onion::new は EntityFactory<Onion> として型推論される。
        // 入力: id=例 "onion", factory=例 Onion::new
        // 戻り値: VegetableType<T>。例 VegetableType<Onion>
        return new VegetableType<>(id, factory);
    }

    @Override
    public @NotNull String getCategoryMark() {
        return "[V]";
    }
}
