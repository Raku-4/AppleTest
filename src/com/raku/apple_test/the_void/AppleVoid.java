package com.raku.apple_test.the_void;

public class AppleVoid {
    public String color;
    public int weight;

    public AppleVoid(String color, int weight) { // ← ここが引数
        this.color = color;
        this.weight = weight;
    }

    public void eat(int bite) { // ← bite が引数
        weight -= bite;
        System.out.println(color + "のりんごを" + bite + "g 食べた。残り " + weight + "g");
    }
}