package com.raku.apple_test.double_constructor.pro.food.vegetable.vegetables;

import com.raku.apple_test.double_constructor.pro.AllEntity;
import com.raku.apple_test.double_constructor.pro.EntityType;
import com.raku.apple_test.double_constructor.pro.Level;
import com.raku.apple_test.double_constructor.pro.food.vegetable.VegetableCatalog;
import org.jetbrains.annotations.NotNull;

public class Broccoli extends AllEntity {
    private String vividness;
    private String color;
    private String size;
    private int weight;

    public Broccoli(@NotNull EntityType<? extends AllEntity> type, @NotNull Level level) {
        super(type, level);
        this.vividness = "unknown";
        this.color = "unknown";
        this.size = "unknown";
        this.weight = 0;
    }

    public  Broccoli(@NotNull Level level, String vividness, String color, String size, int weight) {
        this(VegetableCatalog.BROCCOLI, level);
        this.vividness = vividness;
        this.color = color;
        this.size = size;
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public String getVividness() {
        return vividness;
    }

    public String getSize() {
        return size;
    }

    public int getWeight() {
        return weight;
    }

    public void setVividness(@NotNull String vividness) {
        if (vividness.isBlank()) throw new IllegalArgumentException("vividness is blank");
        this.vividness = vividness;
    }

    public void setColor(@NotNull String color) {
        if (color.isBlank()) throw new IllegalArgumentException("color is blank");
        this.color = color;
    }

    public void setSize(@NotNull String size) {
        if (size.isBlank()) throw new IllegalArgumentException("size is blank");
        this.size = size;
    }

    public void setWeight(int weight) {
        if (weight < 0) throw new IllegalArgumentException("weight must be >= 0");
        this.weight = weight;
    }

    @Override
    public @NotNull String describe() {
        return "Broccoli{" +
                "type=" + getType().getLabeledId() +
                ", level=" + getLevel().name() +
                ", color='" + color + '\'' +
                ", weight=" + weight +
                ", vividness='" + vividness + '\'' +
                '}';
    }
}
