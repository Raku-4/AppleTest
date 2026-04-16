package com.raku.apple_test.record;

import org.jetbrains.annotations.NotNull;

/**
 * recordは「データを運ぶための不変オブジェクト」を短く書く構文です。
 *<p>
 * これ1行で、次を自動生成します:
 * - private final フィールド (name, sweetness, origin)
 * - アクセサ(name(), sweetness(), origin())
 * - equals / hashCode / toString
 * - getter と setter
 */
public record AppleRecord(String name, int sweetness, String origin) {

    /**
     * compact constructor:
     * ヘッダの全引数(name, sweetness, origin)を受け取り、
     * 検証や補正を行う場所です。
     */
    public AppleRecord {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (sweetness < 0 || sweetness > 100) {
            throw new IllegalArgumentException("sweetness must be 0..100");
        }
        if (origin == null || origin.isBlank()) {
            origin = "unknown";
        }
    }

    /** 通常のメソッドも追加できます。 */
    public @NotNull String tasteLabel() {
        if (sweetness >= 80) return "very sweet";
        if (sweetness >= 50) return "balanced";
        return "tart";
    }
}