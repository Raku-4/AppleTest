package com.raku.apple_test.constructor_reference;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main1 {
    public static void main(String[] args) {
        // 0引数コンストラクタ参照: () -> new FApple()
        Supplier<FApple> noArgFactory = FApple::new;

        // 1引数コンストラクタ参照: (color) -> new FApple(color)
        Function<String, FApple> oneArgFactory = FApple::new;

        // 2引数コンストラクタ参照: (color, brix) -> new FApple(color, brix)
        BiFunction<String, Integer, FApple> twoArgFactory = FApple::new;

        FApple a1 = noArgFactory.get();
        FApple a2 = oneArgFactory.apply("green");
        FApple a3 = twoArgFactory.apply("red", 14);

        System.out.println("a1 = " + a1);
        System.out.println("a2 = " + a2);
        System.out.println("a3 = " + a3);
    }
}

