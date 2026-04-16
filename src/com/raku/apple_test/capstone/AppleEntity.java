package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

public class AppleEntity extends AbstractFood {
    private String color;
    private int sugarBrix;
    private final String name;

    public AppleEntity(EntityType<? extends AbstractFood> type, Zone zone, String name) {
        super(type, zone, name);
        this.color = "unknown";
        this.sugarBrix = 0;
        this.name = name;
    }

    public AppleEntity(Zone zone, String color, int sugarBrix, String name) {
        this(Catalog.APPLE, zone, name);
        this.color = color;
        this.sugarBrix = sugarBrix;
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

    @Override
    public double metric() {
        return sugarBrix;
    }

    @Override
    public @NotNull String describe() {
        return "AppleEntity{" +
                "type=" + getType().labeledId() +
                ", zone=" + getZone().name() +
                ", color='" + color + '\'' +
                ", sugarBrix=" + sugarBrix +
                '}';
    }

    @Override
    public String getName() {
        return name;
    }
}

