package com.raku.apple_test.double_constructor.pro;

import org.jetbrains.annotations.NotNull;

/**
 * 学習用 EntityType。
 * Minecraft の EntityType + EntityFactory の最小概念を再現している。
 * <p>
 * ここで、EntityType<T extends AllEntity> とする必要はあるのでしょうか。
 * ただ単にEntityType extends AllEntity とするのではダメなのでしょうか。
 * <p>
 * EnittyType<T extends AllEntity> は「型の種類を受け取るため」で、
 * EntityType extends AllEntity は「継承の宣言」になってしまうので、別物です。
 * <p>
 * 1. T extends AllEntity は「型の制約」
 * public class EntityTyoe<T extends AllEntity>
 * <p>
 * これは、
 * ・Tという型の変数を使います。
 * ・そのT はAllEntity を継承した型だけにします。
 * という意味です。
 * つまり、
 * ・Apple ・Banana ・Onion ・Broccoli
 * のような「具体的な種類」を、後から差し込めるようにしています。
 * 差し込むときは、「AllEntity を継承したT」 として差し込まれます。
 * <p>
 * 2. EntityType extends AllEntity は「クラスの継承」
 * public class EntityType extends AllEntity
 * <p>
 * これは、ジェネリクスではなく、ただの継承です。
 * つまり意味は、
 * ・EntityType というクラス自身が、AllEntity を継承する
 * という意味になります。
 * <p>
 * でも、EntityType は「果物や野菜そのもの」ではなく、「果物や野菜をつくるための型」です。
 * なので、EntityType 自体をAllEntity にするのは役割が違います。
 * <p>
 * つまり、前者は「AllEntity を継承した食べ物を、さらに果物用や野菜用に箱を作れるようにするもの」ですが、
 * 後者は「箱そのものを果物にする」ということです。
 * この違いです。
 * <p>
 * まとめると、このクラスは、AllEntity を継承した食べ物を、果物や野菜などの別々の型に安全に分類して登録し、
 * 必要な時に生成できるようにするための ”仕組み” そのものです。
 */
// VegetableType<Onion> で、実質的にEntityType<Onion>
// しかし、VegetableType<> に間接的に入れ物を作ることで、「このエンティティはVegetable であるEntityType ですよ」という意味を持たせることができる。
public class EntityType<T extends AllEntity> {

    /** このコードの意味は、
        戻り値がT 型（T はAllEntity を継承した型） のオブジェクト
        引数は EntityType<? extends T> 型（T を継承した型の EntityType）と Level 型 ということです。
        つまり、このファクトリー（方法）は、特定の EntityType と Level を受け取って、その EntityType に対応する T 型のインスタンスを生成するためのものです。
        例えば、Apple を生成するためのファクトリーは、EntityType<? extends AllEntity> と Level を受け取って、
        Apple などのインスタンスを返すことになります。

        EntityFactory を内部 interface にしている理由:
        1) EntityType 専用の契約だと明確にするため。
        2) EntityType.EntityFactory という名前で文脈を固定できるため。
        3) 外部に同名 interface を増やさず、学習時の混乱を減らすため。
        */
    @FunctionalInterface
    public interface EntityFactory<T extends AllEntity> {

        // 「T 型の EntityType」と「生成先 level」を受け取り、T を1つ生成して返す契約。
        // 例: Onion::new は (EntityType<? extends Onion>, Level) -> Onion に適合するため渡せる。
        // ちなみにT はOnion やBroccoli などのクラス型です。
        // 入力: type=例 VegetableType<Onion>, level=例 field
        // 戻り値: T=例 Onion インスタンス
        T create(EntityType<? extends T> type, Level level);
        // ? extends T とすることで、型の取り違えをコンパイル時に防ぐ
    }

    private final @NotNull String id;
    private final @NotNull EntityFactory<T> factory;

    /**
     * id を用いて オブジェクトごとに仕分けを行い、
     * factory を用いて オブジェクトの種類を決める。
     */
    protected EntityType(@NotNull String id, @NotNull EntityFactory<T> factory) {
        this.id = id; // "onion"
        this.factory = factory; // Onion::new
    }

    public @NotNull String getId() {
        return id;
    }

    // Subclasses can override this to show a category marker like [F] or [V].
    public @NotNull String getCategoryMark() {
        return "[?]";
    }

    public @NotNull String getLabeledId() {
        return getCategoryMark() + id;
    }

    public T create(Level level) {
        /* Entity を作る流れ
        1 EntityType#create(level) を呼ぶ。
        2 その中で factory.create(this, level) を呼ぶ。（this は今の EntityType インスタンス）
        3 factory は登録済みの生成処理（例: Apple::new）を実行する。
        4 その生成処理は new Apple(type, level) のようにコンストラクタを呼ぶ。
        5 コンストラクタは super(type, level) を呼ぶ。
        6 AllEntity が type と level をフィールドに保存する。
        7 生成された T インスタンス（例: Apple/Onion）が factory.create の戻り値になる。
        8 その値が EntityType#create の戻り値として返る。
        9 呼び出し側は返ってきたインスタンスを使う。
        */
        // this に入る値: 例 VegetableCatalog.ONION (VegetableType<Onion>)
        // 戻り値: factory が生成した T。例 ONION.create(field) の戻り値は Onion
        return factory.create(this, level);
        // 3) factory.create(this, level) を呼ぶと
        // 実際には new Onion(this, level) が実行される
    }
}
/*
 * // 1) インターフェースは「契約」だけ（実装なし）
 * public interface EntityFactory<T> {
 *     T create(EntityType<? extends T> type, Level level);  // 中身なし
 * }
 *<p>
 * // 2) Onion::new が「実装」になる
 * // Onion::new は実質この lambda と同じ：
 * EntityFactory<Onion> factory = (type, level) -> new Onion(type, level);
 *<p>
 * // 3) factory.create(this, level) を呼ぶと
 * // 実際には new Onion(this, level) が実行される
 * Onion result = factory.create(this, level);
 * // = new Onion(this, level);
 */