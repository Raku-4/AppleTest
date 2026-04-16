package com.raku.apple_test.functional;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AppleProcessor {
    // このメソッドは、リストと、「リストの要素に施す躯体的な処理」を引数として受け取ります。
    // リストの各要素に対して、渡された処理(appleNumber)を実行するメソッド

    // インスタンス化不可能であることを明示する
    private AppleProcessor(){}

    // 引数が何であっても機能的にfor 文で処理を行うだけなのでstatic メソッドを用いる
    // つまりprocessList というメソッドは、第一引数list の内容を、appleNumber の具体メソッドによって処理をおこなうという機能的なメソッド
    public static void processList(@NotNull List<Double> list, @NotNull AppleNumber appleNumber){
        for (int i = 0; i < list.size(); i++) {
            double original = list.get(i);
            int result = appleNumber.operate(original);
            // AppleNumber を継承した、"具体"メソッドであるoperate にoriginal を代入する
            // 今回はAppleNumber を継承した"具体"メソッドの内容が＠数を2 倍する」というだけなので、今回は
            // original に代入すればresult はoriginal が2倍された数になると思っていい。
            // もちろん、具体メソッドの内容を個々で書き換えれば結果も変わるが。、ややこしくなるので今回は行わない。

            System.out.println(original + " -> " + result);
            // original にはMain1Legacy クラスのnumbers リスト内の値が、
            // result には AppleNumbers を継承してnumbers リスト内の値に処理を行ったもの
        }
    }
}