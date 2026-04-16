package com.raku.apple_test.functional;

@FunctionalInterface
public interface AppleNumber {
    // 処理の契約を定義するインターフェース
    // Double を引数にとり、int を返す処理
    int operate(Double a);
}