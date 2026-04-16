package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

/**
 * 1つの食べ物種別を表す「登録情報」。
 *<p>
 * - `id` : 文字列の識別子
 * - `category` : 果物/野菜の分類
 * - `factory` : その種別を作る具体的な方法
 *<p>
 * つまり `EntityType` は「何を、どう作るか」をひとまとめにした
 * レシピのようなクラス。
 */
public class EntityType<T extends AbstractFood> {
    private final String id;
    private final FoodCategory category;
    private final EntityFactory<T> factory;

    private EntityType(String id, FoodCategory category, EntityFactory<T> factory) {
        this.id = id;
        this.category = category;
        this.factory = factory;
    }

    public static <T extends AbstractFood> @NotNull EntityType<T> of(String id, FoodCategory category, EntityFactory<T> factory) {
        // `AppleEntity::new` のようなコンストラクタ参照を、ここで登録情報として保持する。
        return new EntityType<>(id, category, factory);
    }

    public String id() {
        return id;
    }

    public FoodCategory category() {
        return category;
    }

    public @NotNull String labeledId() {
        // カテゴリ記号 + id の形で、`[F]apple` のような表示名を作る。
        return category.mark() + id();
    }

    public T create(Zone zone, String name) {
        // ここで初めて factory が実行される。
        // `this` は「いま呼ばれている EntityType 定数そのもの」で、
        // `AppleEntity::new` / `OnionEntity::new` のような参照先へ type 情報として渡る。
        /*回答*/
        return factory.create(this, zone, name);
        //throw new UnsupportedOperationException("TODO: implement create(zone)");
    }
}

