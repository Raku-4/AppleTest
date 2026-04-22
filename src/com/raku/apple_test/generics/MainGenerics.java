package com.raku.apple_test.generics;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ジェネリクスとは、クラスやメソッドが「どんな型でも扱える」ようにするための機能です。
 * <p>例えば、
 * <p></p>「一方のリンゴの第一番目のコンストラクタは色に関するものを持たせたいけど、
 *  もう一方のリンゴには品種に関する情報を持たせたい。」
 *  という時に使うことができます。
 *  <p>
 *  ここでは例としてApple クラスを考えます。
 *  <p>
 *  使い方は、クラスの名前の横に<any>や<T>のように、型のために「引数」という箱を作ってあげるだけ。
 *  <p>そのあと、コンストラクタやメソッド、フィールドの引数に
 *  <p></p> public <Any> information
 *  とやってあげるだけ。<p></p>
 *  そのあと、呼び出し側のメインクラスなどで、<Any> の部分に<String>や<Integer> などと指定してあげると、その型を使ってクラスやメソッドが動いてくれます。
 *  <p> 例 (オブジェクトクラスとメインクラスの二つ)
 *  <p> public Apple<Any>{
 *  <p>     public Apple(String name, Any color, int weight) {
 *          this.name = name;
 *          this.color = color;
 *          this.weight = weight;
 *          }
 *  <p> }
 *  <p> public class MainGenerics {
 *  <p>     public static void main(String[] args) {
 *  <p>         Apple<String> redApple = new Apple<>("赤いりんご", "RED", 150);
 *  <p>         Apple<Integer> greenApple = new Apple<>("緑のりんご", 7, 130);
 *  <p>     }
 *  <p> }
 *  <p> これだけで、同じクラスを使って、色を文字列で持つりんごも、色を数値で持つりんごも作ることができます。
 *  <p> さらに、同じクラスを使って、色を文字列で持つばななも、色を数値で持つばななも作ることができます。
 *  <p> これがジェネリクスの基本的な使い方です。
 *  <p> さらに、ジェネリクスを使うと、同じクラスで色々な型を扱えるだけでなく、型安全なコードを書くことができます。
 *  <p> 例えば、Apple<String> と Apple<Integer> は別の型として扱われるので、間違って Apple<String> のオブジェクトを Apple<Integer> の変数に代入することができません。
 *  <p> これにより、コンパイル時に型の不一致を検出することができ、バグを減らすことができます。
 *  <p> ジェネリクスは、Javaのコレクションフレームワークなどで広く使われており、コードの再利用性と安全性を高めるための重要な機能です。
 */
public class MainGenerics {
    public static void main(String[] args) {
        // 文字列を色として持つ、素直なりんご
        // 色を String でもつこともできますし、数値で持つこともできます。
        GenericsApple<String> Apple1 = new GenericsApple<>("りんご", "RED", 150);
        GenericsApple<Integer> Apple2 = new GenericsApple<>("実験りんご", 7, 120);

        GenericsBanana<String> yellowBanana = new GenericsBanana<>("ばなな", "YELLOW", 110);
        GreenBanana greenBanana = new GreenBanana("青くなりかけのばなな", 105);
        YellowBanana ripeBanana = new YellowBanana("熟したばなな", 115);

        // ラッパークラスは「中身を受け取って表示する」ことに責務を絞っています。
        RedApple<GenericsApple<String>> redPrinter = new RedApple<>(Apple1);
        BlueBanana<GenericsBanana<String>> bananaPrinter = new BlueBanana<>(yellowBanana);

        printLine("=== 1. 個別オブジェクトの表示 ===");
        redPrinter.eatFruit();
        bananaPrinter.eatFruit();

        printLine("=== 2. 型が違っても同じクラスで持てる例 ===");
        printValue("文字列色のりんご", Apple1.describe());
        printValue("数値色のりんご", Apple2.describe());
        printValue("緑のばなな", greenBanana.describe());
        printValue("黄色のばなな", ripeBanana.describe());

        printLine("=== 3. List とワイルドカードの例 ===");
        printAppleShelf(List.of(Apple1, Apple2));
        printBananaShelf(List.of(yellowBanana, greenBanana, ripeBanana));
    }

    /**
     * どんな型でも受け取れる、最も基本的な汎用メソッド。
     *
     * <p>型パラメータ T は、呼び出し時に自動推論されます。</p>
     */
    private static <T> void printValue(String label, T value) {
        System.out.println(label + " -> " + value);
    }

    /**
     * 画面の見出しを整えるだけの補助メソッド。
     */
    private static void printLine(String text) {
        System.out.println();
        System.out.println(text);
    }

    /**
     * りんごの一覧を表示します。
     *
     * <p><? extends GenericsApple<?>> を使うことで、
     * "GenericsApple の子孫なら何でも受け取る" ことができます。</p>
     */
    private static void printAppleShelf(@NotNull List<? extends GenericsApple<?>> apples) {
        System.out.println("-- Apple Shelf --");
        for (GenericsApple<?> apple : apples) {
            System.out.println(apple.describe());
        }
    }

    /**
     * ばななの一覧を表示します。
     */
    private static void printBananaShelf(@NotNull List<? extends GenericsBanana<?>> bananas) {
        System.out.println("-- Banana Shelf --");
        for (GenericsBanana<?> banana : bananas) {
            System.out.println(banana.describe());
        }
    }
}
