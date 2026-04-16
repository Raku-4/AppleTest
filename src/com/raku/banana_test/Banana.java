/**
 * Code Written By Raku.
 * Code's Description Written By Gemini, Raku.
 */
package com.raku.banana_test;

import com.raku.apple_test.all_argument.apple_interface.FruitInterface;
import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * 【レコードの解説】
 * record は Java 14から登場した「データ専用のクラス」だよ。
 * `(int weight, int ate)` と書くだけで、以下のものが自動で作られるんだ。
 * 1. private final int weight; (箱)
 * 2. private final int ate;    (箱)
 * 3. Banana(int weight, int ate) { ... } (コンストラクタ)
 * 4. weight() と ate() (ゲッター)
 */
public record Banana(int weight, int ate) implements FruitInterface {

    /**
     * 【色の定義】
     * インターフェースの「色を教えて」という約束を守るよ。
     * <p>@Contract(pure  = true) は「このメソッドは中身を壊さない安全なものだよ」という印。
     * <p>@NotNull は「絶対に空（null）を返さないよ」というお約束だよ。
     */
    @Contract(pure = true)
    @Override
    public @NotNull AppleEnumColor getColor() {
        return AppleEnumColor.YELLOW;
    }

    /**
     * 【味の定義】
     */
    @Contract(pure = true)
    @Override
    public @NotNull String getTaste() {
        return "ねっとり甘い";
    }

    /**
     * 【計算処理】
     * record の中にある weight と ate という「自動で作られた箱」を使って計算するよ。
     */
    @Contract(pure = true)
    @Override
    public int getRemainingWeight() {
        return weight - ate;
    }

    /**
     * 【判定処理】
     * 自分の重さ(weight)を見て、バナナ独自の基準で重たいか教えるよ。
     */
    @Contract(pure = true)
    @Override
    public boolean isHeavy() {
        return weight > 150;
    }

    /**
     * 【動作の定義】
     * 「食べる」という動作を画面に出力するよ。
     */
    @Override
    public void eat() {
        System.out.println(weight + "g のバナナをむいて" + ate +"g 食べた。味は" + getTaste() + "。");
    }
}