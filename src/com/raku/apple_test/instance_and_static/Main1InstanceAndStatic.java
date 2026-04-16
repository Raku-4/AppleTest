package com.raku.apple_test.instance_and_static;

public class Main1InstanceAndStatic {
    public static void main(String[] args) {
        // インスタンスを2つ作る
        AppleInstanceAndStatic apple1 = new AppleInstanceAndStatic();
        AppleInstanceAndStatic apple2 = new AppleInstanceAndStatic();

        // インスタンスフィールドはオブジェクトごとに独立
        apple1.instanceNum = 5;
        apple2.instanceNum = 10;

        System.out.println("apple1.instanceNum = " + apple1.instanceNum); // 5
        System.out.println("apple2.instanceNum = " + apple2.instanceNum); // 10

        // static フィールドはクラス全体で共有
        apple1.staticNum = 100;
        System.out.println("apple1.StaticNum = " + apple1.staticNum); // 100
        System.out.println("apple2.StaticNum = " + apple2.staticNum); // 100

        // インスタンス経由でも同じ static フィールドにアクセスできる（推奨はクラス名経由）
        apple2.staticNum = 200;
        System.out.println("apple1.StaticNum = " + apple1.staticNum);
        System.out.println("apple2.StaticNum = " + apple2.staticNum);

        // 元のクラスでみんなで共有しているものなので、クラス名から直接数値を変えることもできます。
        AppleInstanceAndStatic.staticNum = 300;
        System.out.println("apple1.StaticNum = " + apple1.staticNum);
        System.out.println("apple2.StaticNum = " + apple2.staticNum);
    }
}