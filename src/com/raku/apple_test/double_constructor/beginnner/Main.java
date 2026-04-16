package com.raku.apple_test.double_constructor.beginnner;

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;

public class Main {
    public static void main(String[] args) {
        Apple apple_red = new Apple(AppleEnumColor.RED,100, "甘い", 50);
        Apple apple_blue = new Apple(AppleEnumColor.BLUE,150,"甘酸っぱい",30, 80, "大きい");

        System.out.println(apple_red.getSuger_content());
        System.out.println(apple_blue.getSuger_content());

        System.out.println(apple_red.getRadius());
        System.out.println(apple_blue.getRadius());
        // apple_red からインスタンス元のコンストラクター以外の情報をgetter で抜き取ろうとすると、Integer 型ではでは0、String 型ではnull を返されます。

        System.out.println(apple_blue.getTaste());
        System.out.println(apple_blue.getColor());
    }
}
