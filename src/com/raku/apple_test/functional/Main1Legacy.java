package com.raku.apple_test.functional;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("Convert2Lambda")
public class Main1Legacy {

    private Main1Legacy(){}

    public static void main(String[] args) {

        List<Double> numbers = Arrays.asList(1.2, 2.3, 3.4);



        System.out.println("\n--- 処理A: 数a を2倍にして、AppleNumber クラスによってint型に変換する（古典的な方法）---");
        AppleNumber multiplyByTwo1 = new NonAnonymousClass();

        AppleProcessor.processList(numbers, multiplyByTwo1);



        System.out.println("\n---処理B: 数a を2倍にして、AppleNumber クラスによってint型に変換する（匿名クラスを用いた方法）---");
        // 処理A の実装（匿名クラスのインスタンスを生成）
        AppleNumber multiplyByTwo2 = new AppleNumber() {
            @Override // AppleNumber クラスはインタ―フェースなので@Overritde を明記して、メソッドを実装する
            public int operate(Double a) {
                return (int) (a * 2);
            }
        };
        //new AppleNumber() : AppleNumber を実装するオブジェクトをこれから作る
        //{...}             : オブジェクトの実体であるクラスの"定義の「中身」"（本体）
        //@Override ...     : AppleNumber の operate メソッドの具体的な処理内容

        // 処理を続行
        AppleProcessor.processList(numbers, multiplyByTwo2);



        System.out.println("\n--- 処理C: 数a を2倍にして、AppleNumber クラスによってint型に変換する（ラムダ式）---");
        // 比較：Java 8以降のラムダ式
        // 冗長な記述が全て消え、「a -> (int) (a * 2)」 という処理ブロックだけが残る
        AppleNumber multiplyByTwo3 = (a) -> (int) (a * 2);

        // さらに、上記のラムダ式はメソッド参照という機能で更に簡単に書ける

        AppleProcessor.processList(numbers, multiplyByTwo3);

        System.out.println("\n\nいずれも結果は変わらず、処理の中身の意味も変わらないが、コードが見やすくなったという点では\nA -> B -> C の順にJava が進化している。");
    }
}
