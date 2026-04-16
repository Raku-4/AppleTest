package com.raku.apple_test.setter;

// ここでも、君自身が、"君のやりたいことを伝える" 場所だよ。場所の名前はMain1Basic 君。
// 君のやりたいことを、Main1Setter に書いてみよう。
public class Main1Setter {
    public static void main(String[] args){

        // ここでも君は、新しく(new) りんごを作るよ。インスタンス化と言う事を前に習ったね。
        // 二つ目のりんごは、AppleSetter 君を使ってリンゴを作るよ。
        // 色に加えて、重さもりんごを作るために教える必要があるよ。
        // AppleSetter 君に教えてあげよう。
        AppleSetter Apple2 = new AppleSetter("赤",150);

        // そしてここで、君は新しく作ったりんごApple2 を食べる。
        Apple2.eat();

        // そして、君は恥ずかしがり屋のcolor 君とweight 君のことを個別に知りたかったら、「ゲッター」さんに聞こう。
        // 聞き方は、新しく作ったりんごの名前の後に、getColor() または getWeight と書くよ。

        System.out.println("\nweight 君は、" + Apple2.getWeight() + "です。");
        System.out.println("color 君は、" + Apple2.getColor() + "です。");
        System.out.println("\nどちらもゲッター(getter) さんが教えてくれたよ。");

    }
}