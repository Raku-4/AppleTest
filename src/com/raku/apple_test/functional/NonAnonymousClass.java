package com.raku.apple_test.functional;

// これがいわば匿名クラスの真の姿。
// インターフェース AppleNumber を実装します。
public class NonAnonymousClass implements AppleNumber{

    @Override
    public int operate(Double a){
        // 抽象メソッドの具体化：引数を2倍にして戻り値をint に変換して返します。
        return (int) (a * 2);
    }
}