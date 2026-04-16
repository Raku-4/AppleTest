package com.raku.apple_test.abstract_class;

import com.raku.apple_test.abstract_apple.AbstractApple;

public class Main1Abstract {
    public static void main(String[] args){
        AbstractApple a;

        a = new AppleAbstract(150, "甘い");
        a.eat();
        System.out.println(a.describe());
    }
}