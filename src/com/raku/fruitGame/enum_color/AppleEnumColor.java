package com.raku.fruitGame.enum_color;

import java.util.HashSet;
import java.util.Set;

/**
 * りんごで許可する色定数。
 *
 * <p>自由文字列ではなく enum を使うことで、
 * スペルミスをコンパイル時に検出しやすくなります。</p>
 */
public enum AppleEnumColor {
    RED("赤"),
    BLUE("青"),
    GREEN("緑"),
    YELLOW("黄");

    /** 画面表示用ラベル */
    private final String label;

    AppleEnumColor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getAllLabel() {
        for (AppleEnumColor color : AppleEnumColor.values()) {
            return color.getLabel();
        }
        return "";
    }
}
