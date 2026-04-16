package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

public class BroccoliEntity extends AbstractFood {
    private String vividness;
    private String color;
    private String size;
    private int weight;

    public BroccoliEntity(EntityType<? extends AbstractFood> type, Zone zone, String name) {
        super(type, zone, name);
        this.name = name;
        this.vividness = "normal";
        this.color = "green";
        this.size = "M";
        this.weight = 0;
    }

    public BroccoliEntity(Zone zone, String vividness, String color, String size, int weight, String name) {
        this(Catalog.BROCCOLI, zone, name);
        this.vividness = vividness;
        this.color = color;
        this.size = size;
        this.weight = weight;
    }

    public void setVividness(String vividness) {
        this.vividness = vividness;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setWeight(int weight) {
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
        return "BroccoliEntity{" +
                "type=" + getType().labeledId() +
                ", zone=" + getZone().name() +
                ", vividness='" + vividness + '\'' +
                ", color='" + color + '\'' +
                ", size='" + size + '\'' +
                ", weight=" + weight +
                '}';
    }

    private String name;

    @Override
    public String getName() {
        return name;
    }
}

