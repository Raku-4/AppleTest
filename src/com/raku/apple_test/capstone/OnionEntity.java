package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

public class OnionEntity extends AbstractFood {
    private String kind;
    private String color;
    private int weight;

    // これは factory から呼ばれる本命のコンストラクタ。
    // `EntityType` と `Zone` がここに届き、親クラス `AbstractFood` に保存される。
    public OnionEntity(EntityType<? extends AbstractFood> type, Zone zone, String name) {
        super(type, zone, name);
        this.name = name;
        this.kind = "unknown";
        this.color = "unknown";
        this.weight = 0;
    }

    // こちらは `new OnionEntity(field, ...)` のように直接作りたいときの便利コンストラクタ。
    // 内部では `Catalog.ONION` を使って、上の本命コンストラクタへ委譲している。
    public OnionEntity(Zone zone, String kind, String color, int weight, String name) {
        this(Catalog.ONION, zone, name);
        this.kind = kind;
        this.color = color;
        this.weight = weight;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setWeight(int weight) {
        // 負の重さは許さない、というバリデーション。
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be >= 0");
        }
        this.weight = weight;
    }

    @Override
    public double metric() {
        return weight;
    }

    @Override
    public @NotNull String describe() {
        // `getType().labeledId()` で「どの登録情報から作られたか」を表示する。
        return "OnionEntity{" +
                "type=" + getType().labeledId() +
                ", zone=" + getZone().name() +
                ", kind='" + kind + '\'' +
                ", color='" + color + '\'' +
                ", weight=" + weight +
                '}';
    }

    private final String name;

    @Override
    public String getName() {
        return name;
    }
}

