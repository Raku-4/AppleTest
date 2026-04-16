package com.raku.apple_test.non_constructor;

import org.jetbrains.annotations.NotNull;

public class AppleNonConstructor {
    @NotNull String color = "赤";
    int weight = 150;

    public void eat() {
        System.out.println("この" + color + "のリンゴの重さは" + weight + "g です。");
    }
}