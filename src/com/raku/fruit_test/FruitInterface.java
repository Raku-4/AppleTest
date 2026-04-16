package com.raku.fruit_test;

public interface FruitInterface<E> {
    void setColor(E color);
    void setAte(long ate);
    void setWeight(long weight);
    long getRemainingWeight();
    long getAte();
    String getColor();
}