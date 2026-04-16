package com.raku.apple_test.capstone;

import java.util.List;

/**
 * 生成レシピをまとめておくカタログ。
 *<p>
 * 各定数は単なる識別子ではなく、
 * 「id」「カテゴリ」「コンストラクタ参照」をひとまとめにした
 * `EntityType` の実体。
 *<p>
 * 【役割】
 * このクラスは「システムが安全に生成を許可する型の一覧」です。
 * 新しいエンティティを追加するときは、ここに登録する必要があります。
 * 登録されたもののみが `Catalog.APPLE.create(...)` で生成可能になり、
 * 登録されていない型は生成できません。
 *<p>
 * 【Minecraft との対応】
 * Minecraft では `EntityType` レジストリが同じ役割を果たします。
 * 登録されていないエンティティ ID で生成しようとするとエラーになるのと同じです。
 */
public final class Catalog {
    private Catalog() {
    }

    public static final EntityType<AppleEntity> APPLE =
            EntityType.of("apple", FoodCategory.FRUIT, AppleEntity::new);

    public static final EntityType<BananaEntity> BANANA =
            EntityType.of("banana", FoodCategory.FRUIT, BananaEntity::new);

    public static final EntityType<OnionEntity> ONION =
            EntityType.of("onion", FoodCategory.VEGETABLE, OnionEntity::new);

    public static final EntityType<BroccoliEntity> BROCCOLI =
            EntityType.of("broccoli", FoodCategory.VEGETABLE, BroccoliEntity::new);

    public static final List<EntityType<? extends AbstractFood>> ALL =
            List.of(APPLE, BANANA, ONION, BROCCOLI);
}

