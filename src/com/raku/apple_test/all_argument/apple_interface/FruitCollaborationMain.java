/**
 * Code , Description Written By Gemini.
 */
package com.raku.apple_test.all_argument.apple_interface;

import com.raku.apple_test.all_argument.Apple;
import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import com.raku.banana_test.Banana;

import java.util.ArrayList;
import java.util.List;

public class FruitCollaborationMain {
    public static void main(String[] args) {
        // インターフェース型のリスト。これが「魔法のカゴ」です。
        List<FruitInterface> basket = new ArrayList<>();

        // 1. 通常クラス（こだわり型）のリンゴを追加
        // セッターで厳しいチェックを経て作られる、信頼のリンゴ。
        basket.add(new Apple(AppleEnumColor.RED, 400, "甘い", 50));

        // 2. レコード（シンプル型）のバナナを追加
        // データを安全に運ぶことに特化した、スマートなバナナ。
        basket.add(new Banana(150, 40));

        System.out.println("=== 魔法のカゴ（インターフェース）の中身を順番に食べるよ ===\n");

        for (FruitInterface fruit : basket) {
            // ここがポリモーフィズムの真髄！
            // 中身が何であるかを問わず、
            // 「果物なら食べられる」という共通の約束(インターフェース)だけで実行できる。
            fruit.eat();

            System.out.println("残りの重さ: " + fruit.getRemainingWeight() + "g");
            System.out.println("重いかな？: " + (fruit.isHeavy() ? "重い！" : "普通だね") + "\n");
        }
    }
}