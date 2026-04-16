/**
 * Code Written By Raku.
 * Code's Description Written By Gemini, Raku.
 */
package com.raku.apple_test.all_argument;


import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;

// ここは、君が「作ったりんごに何をさせるか」を指示する場所だよ。
public class Main1SpecificalMethods {
    public static void main(String[] args){

        // 【インスタンス化と初期設定】
        // 新しいリンゴ (redApple) を作るよ。
        // 作る時には、Apple 君がコンストラクタで必要な情報（引数）を教えてとお願いしてくるね。
        // 教える順番は、(色:RED, 重さ:150, 味:"甘い", 食べた量:50) の順番だよ。
        Apple redApple = new Apple(AppleEnumColor.RED, 150, "甘い",50);

        // ------------------------------------------------------------------------------------------------------

        // 【メソッドの実行（振る舞いの指示）】
        // Apple の void メソッド eat を呼び出すよ。
        // eat 君は、何も値を返さないけど、画面にメッセージを出力してくれるね。
        redApple.eat();

        // ------------------------------------------------------------------------------------------------------

        // 【ゲッターの呼び出し（データ取得と計算）】

        // 1. 計算ゲッターの呼び出し: getRemainingWeight()
        // weight から ate を引いた「残り」を計算して、整数(int) で返してくれるゲッターさんを呼ぶよ。
        int w = redApple.getRemainingWeight();

        // ゲッター getAte() で食べた量を取得し、計算結果 w と合わせて出力するよ。
        System.out.println(redApple.ate() + "g 食べたので、残りの重さは " + w + "g です。");

        // ゲッター getTaste() でリンゴの味を取得して出力するよ。
        System.out.println("このリンゴは" + redApple.getTaste() + "味です。");

        // ------------------------------------------------------------------------------------------------------

        // 【boolean 戻り値の活用】
        // ゲッター isHeavy() を呼び出すよ。これは「200gより重いかどうか」を true(正しい、つまり重い) か false(間違い、つまり普通) で返してくれるね。
        if (redApple.isHeavy()) {
            // もし isHeavy() が true (正しい)だったら、このメッセージを出すよ。
            System.out.println("このリンゴは重いです。");
        } else if (!redApple.isHeavy()) {
            // もし isHeavy() が false （間違い）だったら、このメッセージを出すよ。
            System.out.println("このリンゴは普通の重さです。");
        }

        // ------------------------------------------------------------------------------------------------------

        // 【セッターの呼び出し（データの安全な変更）】

        // セッター setColor() を呼び出して、リンゴの色を「安全に」変更するよ。
        // 引数には、Enum の AppleEnumColor.GREEN (緑) を渡すよ。
        // もしここで null などを渡したら、セッターのバリデーションが働いて、エラーになってくれるね！
        redApple.setColor(AppleEnumColor.GREEN);

        // 変更された色をゲッター getColor() で確認するよ。
        // ※このゲッターは、Enum のラベルを String に変換して返してくれるね。
        System.out.println("腐った後の色: " + redApple.getColor());
    }
}