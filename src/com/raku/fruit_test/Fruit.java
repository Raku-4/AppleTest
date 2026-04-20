package com.raku.fruit_test;

// ドメインモデル
public final class Fruit {
    private final String name;
    private String color;
    private int weight;
    private int ate;

    public Fruit(String name, String color, long l, int weight) {
        this.name = name;
        this.color = color;
        this.weight = weight;
        this.ate = 0;
    }
    public String name() { return name; }
    public String color() { return color; }
    public int weight() { return weight; }
    public int remaining() { return weight - ate; }
    public int ate() { return ate; }

    public void setColor(String color) { this.color = color; }
    public void setWeight(int weight) { this.weight = weight; }
    public void eat(int grams) { this.ate += grams; }

    public String getName() {
        return this.name;
    }

    public String getColor() { return this.color; }

    public int getWeight() { return this.weight; }
}
