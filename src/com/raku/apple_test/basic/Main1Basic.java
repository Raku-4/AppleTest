/**
 * Code Written By Raku.
 * Code's Description Written By Gemini, Raku.
 */
package com.raku.apple_test.basic;

/**
 * ここでは、君自身が、"君のやりたいことを伝える" 場所だよ。場所の名前はMain1Basic 君。
 * 君のやりたいことを、Main1Basic に書いてみよう。
 */
public class Main1Basic {
    public static void main(String[] args){

        /*
         これはインスタンス化。
         インスタンス化というのは、新しく(new) りんごをつくることだよ。
         でもりんごをつくるとき、「りんごをつくるなら色を教えてほしい」と、AppleBasic 君が言っていた。
         もし君が新しくりんごApple1 を作りたいなら、色を教える必要があるよ。
        */
        AppleBasic Apple1 = new AppleBasic("赤");
        /*
         この"赤"は、AppleBasic クラスのcolor1 に入れられるよ。
        */


        Apple1.eat();
        /*
         ここで君は、君が作ったりんごApple1 を食べるよ。
         すると、「赤色のあまいりんごを食べた。」と教えてくれるよ。
        */

        /*
         もしりんごの色について個別に知りたかったら、
         */
        System.out.println("\ncolor 君は、" + Apple1.color + "色です。");
        /* このように書くと、色が分かるようになるよ。 */
    }
}
