/**
 * Code Written By Raku.
 * Code's Description Written By Gemini, Raku.
 */
package com.raku.apple_test.basic;

// この"AppleBasic" 君は、"りんご" について教えてくれるよ。
public class AppleBasic {

    // ここに、色について教えてくれる箱をおくよ。箱の名前は"color"。color 君は、日本語で"色" というよ。分かりやすいね。
    // color 君には、"赤" や "青" などの「色の名前」が入れられるよ。
    public String color;
    // public っていうのは、みんなに見てほしい、いじってほしいということを教えてくれるよ。color 君はかまってちゃんなんだね。


    // これはコンストラクタ。
    // コンストラクタというのは、「りんごを」
    // ここでは、「りんごならみんな"色 (color)" をもっている」ということを教えてくれるよ。
    public AppleBasic(String color1) {
        // 君がりんごをつくるとき、AppleBasic 君は「色を教えてほしい」とお願いするよ。
        // きみが教えた色は、 color1 という名前の「色の名前」の箱に入れられるよ。
        color = color1; // 君が色を教えたら、こうやって、「color 君は color1(赤) だよ」って教えてくれるんだ。
    }


    // これはメソッド。
    // メソッドというのは、「何かをする"部品"(もの)」のことだよ。部品の名前はeat 。日本語で"食べる"という意味だよ。
    // 今回の部品は、もし君がりんごを食べたら、どんな色の、どんな味のりんごを食べたかを、教えてくれるよ。
    public void eat(){
        System.out.println(color + "色のあまいりんごを食べた。");
        // さっき、color 君は コンストラクタでcolor1(赤)、だよって教えてくれたから、
        // ここでは「赤色のあまいリンゴを食べた。」って教えてくれるようになるよ。
    }
}