/**
 * Code Written By Raku.
 * Code's Description Written By Gemini, Raku.
 */
package com.raku.apple_test.all_argument.apple_interface;

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;

// これは「果物（Fruit）という名前のルールブック」だよ。
// インターフェース(interface) は、具体的な中身（どうやってやるか）は書かずに、
// 「果物なら、最低限これだけのことはできなきゃダメだよ！」という「お約束」だけを並べるんだ。
public interface FruitInterface {

    // 【お約束のリスト】
    // ここに書かれているメソッドには { ... }（中身）がないよね。
    // 「中身は、このルールに従うクラス（AppleSpecificalMethodsなど）を、自分で準備してね」という意味だよ。

    int getRemainingWeight(); // 残っている果物の量を教えてくれること。
    int ate();             // 君が食べた果物の量を教えてくれること。
    AppleEnumColor getColor();        // 果物の色を教えてくれること。
    String getTaste();        // 果物の味を教えてくれること。
    boolean isHeavy();        // その果物が重たいか、普通か判定してくれること。
    void eat();               // 「食べる」という動作ができること。
}

/*

💡 なぜインターフェースを使うの？
「最初からクラスに書けばいいじゃない」と思うかもしれませんが、インターフェースを使うと、プログラムがとても柔軟になります。

「共通の呼び出し方」を保証する: リンゴ（Apple）でもバナナ（Banana）でも、この FruitInterface を implements（実装）していれば、どちらも必ず eat() メソッドを持っていることが保証されます。

型をまとめられる: 「リンゴ」も「バナナ」も、プログラムの中では同じ「果物（FruitInterface型）」として扱うことができるようになります。

🔗 Apple とのつながり
あなたが先に作った Apple クラスの冒頭にこう書きましたね。

public class Apple implements FruitInterface

これは、Apple 君が 「僕は FruitInterface さんの決めたルールを全部守ります（実装します）！」 と宣言している状態です。
もし、一つでもメソッド（例えば eat()）を書き忘れると、Javaが「約束が違うよ！」とエラーを出して教えてくれるんです。

 */