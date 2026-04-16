package com.raku.apple_test.double_constructor.pro;


import org.jetbrains.annotations.NotNull;

/**
 * このクラスが始まりのクラス。
 * 果物でも、野菜でも、お肉でも、もしゲームで新たに食べ物を追加するときは
 * この抽象クラスを絶対に継承し、ゲームロジックにあてはまる動作を行うようにする。
 */
public abstract class AllEntity {
    // 自分がどの登録型(EntityType)から生成されたかを保持する。
    private final @NotNull EntityType<? extends AllEntity> type;

    // 自分がどの世界(Level)に存在するかを保持する。
    private final @NotNull Level level;

    // すべての食べ物エンティティは、生成時に type と level を必ず受け取る。
    protected AllEntity(@NotNull EntityType<? extends AllEntity> type, @NotNull Level level) {
        this.type = type;
        this.level = level;
    }

    // 生成元の登録型を返す。
    public @NotNull EntityType<? extends AllEntity> getType() {
        // 戻り値の例: Onion インスタンスなら VegetableCatalog.ONION の型情報
        return type;
    }

    // 所属している世界を返す。
    public @NotNull Level getLevel() {
        // 戻り値の例: field
        return level;
    }

    // 各具体クラス(Apple/Bananaなど)が自己紹介文字列を実装する。
    public abstract String describe();
}