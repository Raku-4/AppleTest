package com.raku.apple_test.contain;

public class AppleContain {
    // staticメソッド（クラスに直接くっついている）
    public static void eat() {
        System.out.println("赤のリンゴを食べた！");
    }

    public static void main(String[] args) {
        AppleContain.eat(); // new しなくても呼べる！
    }
}