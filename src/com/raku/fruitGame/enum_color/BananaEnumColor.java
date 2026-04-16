package com.raku.fruitGame.enum_color;

/**
 * ばななで許可する色定数。
 */
public enum BananaEnumColor {
    YELLOW("黄色"),
    GREEN("緑"),
    BROWN("茶色");

    /** 画面出力用の日本語ラベル */
    private final String label;

    BananaEnumColor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
