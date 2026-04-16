package com.raku.banana_test;

public enum BananaEnumColor {
    YELLOW("黄色"),
    GREEN("緑"),
    RAINBOW("虹");

    final String label;

    BananaEnumColor(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
