package com.raku.apple_test.record;

public class MainRecord {
    public static void main(String[] args) {
        // 1) record生成: compact constructorの検証と補正が働く
        AppleRecord fuji = new AppleRecord("Fuji", 82, "Aomori");
        AppleRecord fujiSame = new AppleRecord("Fuji", 82, "Aomori");
        AppleRecord blankOrigin = new AppleRecord("Shinano", 45, "");

        // 2) 通常class生成: 同じ値を保持
        AppleDataClass classApple = new AppleDataClass("Fuji", 82, "Aomori");
        AppleDataClass classAppleSame = new AppleDataClass("Fuji", 82, "Aomori");

        // 3) recordの自動生成メソッド確認
        System.out.println("=== record basics ===");
        System.out.println("name accessor   : " + fuji.name());
        System.out.println("tasteLabel      : " + fuji.tasteLabel());
        System.out.println("toString auto   : " + fuji);
        System.out.println("equals by value : " + fuji.equals(fujiSame));
        System.out.println("hash same?      : " + (fuji.hashCode() == fujiSame.hashCode()));
        System.out.println("origin fallback : " + blankOrigin.origin());

        // 4) 通常classとの比較
        System.out.println();
        System.out.println("=== class c ===");
        System.out.println("toString manual : " + classApple);
        System.out.println("equals manual   : " + classApple.equals(classAppleSame));

        // 5) 例外確認（バリデーション）
        System.out.println();
        System.out.println("=== validation demo ===");
        try {
            new AppleRecord("", 10, "Nagano");
        } catch (IllegalArgumentException e) {
            System.out.println("invalid name    : " + e.getMessage());
        }

        try {
            new AppleRecord("Akane", 120, "Nagano");
        } catch (IllegalArgumentException e) {
            System.out.println("invalid sweetness: " + e.getMessage());
        }
    }
}
