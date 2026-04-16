package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

public class BananaEntity extends AbstractFood {
    private String color;
    private int sugarBrix;
    private int kcal;

    public BananaEntity(EntityType<? extends AbstractFood> type, Zone zone, String name) {
        super(type, zone, name);
        this.color = "unknown";
        this.sugarBrix = 0;
        this.kcal = 0;
        this.name = name;
    }

    public BananaEntity(Zone zone, String color, int sugarBrix, int kcal, String name) {
        this(Catalog.BANANA, zone, name);
        this.color = color;
        this.sugarBrix = sugarBrix;
        this.kcal = kcal;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSugarBrix(int sugarBrix) {
        if (sugarBrix < 0) {
            throw new IllegalArgumentException("sugarBrix must be >= 0");
        }
        this.sugarBrix = sugarBrix;
    }

    public void setKcal(int kcal) {
        if (kcal < 0) {
            throw new IllegalArgumentException("kcal must be >= 0");
        }
        this.kcal = kcal;
    }

    @Override
    public double metric() {
        return sugarBrix;
    }

    @Override
    public @NotNull String describe() {
        return "BananaEntity{" +
                "type=" + getType().labeledId() +
                ", zone=" + getZone().name() +
                ", color='" + color + '\'' +
                ", sugarBrix=" + sugarBrix +
                ", kcal=" + kcal +
                '}';
    }

    private final String name;

    @Override
    public String getName() {
        return name;
    }
}

