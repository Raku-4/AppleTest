package com.raku.apple_test.capstone;

/**
 * `EntityType` が実際に中身のオブジェクトを作るための受け口。
 *<p>
 * ここには `AppleEntity::new` や `OnionEntity::new` のような
 * 「コンストラクタ参照」が入る。
 *<p>
 * 重要なのは、参照した時点ではまだ生成されず、
 * `EntityType.create(...)` から呼ばれた瞬間にだけ実行されること。
 *<p>
 * 【Minecraft との共通パターン】
 * Minecraft のエンティティ登録でも、同じ仕組みを使っています。
 * 新しいエンティティ（例：CustomMob）は、自身のコンストラクタ参照を
 * `EntityFactory` へ登録してのみ生成可能にすることで、
 * 「どの型のエンティティが存在するか」をシステムが管理できます。
 * これにより、型安全性と制御可能性の両立が実現されます。
 */
@FunctionalInterface
public interface EntityFactory<T extends AbstractFood> {
    // `type` には定数自身 (`this`) が入り、`zone` と `name` と一緒に具体クラスへ渡される。
    T create(EntityType<? extends T> type, Zone zone, String name);
}