package com.raku.apple_test.capstone;

/**
 * 果物・野菜に共通する「親クラス」。
 *<p>
 * 個々の具象クラスはここから
 * - どの `EntityType` で作られたか
 * - どの `Zone` に属するか
 * - 名前をどう持つか
 * を受け継ぐ。
 */
public abstract class AbstractFood {
    private final EntityType<? extends AbstractFood> type;
    private final Zone zone;
    private final String foodName;

    protected AbstractFood(EntityType<? extends AbstractFood> type, Zone zone, String foodName) {
        // `EntityType` から渡された種別情報を、共通フィールドとして保存する。
        this.type = type;
        this.zone = zone;
        this.foodName = foodName;
    }

    public EntityType<? extends AbstractFood> getType() {
        return type;
    }

    public Zone getZone() {
        return zone;
    }

    public  String getFoodName() {return foodName;}

    public FoodCategory getCategory() {
        // 具体クラスではなく、登録された type からカテゴリを逆引きできる。
        return type.category();
    }

    // 集計用の共通指標。果物は糖度、野菜は重さ。
    public abstract double metric();

    public abstract String describe();

    public abstract String getName();
}

