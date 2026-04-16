/**
 * Code Written By Raku.
 * Code's Description Written By Gemini, Raku.
 */
package com.raku.apple_test.all_argument;

import com.raku.apple_test.all_argument.apple_interface.FruitInterface;
import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import org.jetbrains.annotations.NotNull;

/**
 * この"Apple" 君は、"りんご" について教えてくれるよ。
 * だけど、Apple 君も、「りんごはくだものだけど、果物だったら何をすべきか」を知りたい。
 * それは、FruitInterface くんが、「果物だったらみんな守るべきルール」を教えてくれる。
 * でも教えてもらうためには、Apple 君の名前の横に implements って書いて、
 * 果物について教えてくれた人の「名前」、いまだったら「FruitInterface」 と書いてあげようね。
 */
public class Apple implements FruitInterface {

    /**
     * private String color; -> AppleColor クラスのenum を参照するのに変更
     */
    private AppleEnumColor color;
    private int weight;
    private int ate;
    private String taste;

    /**
     * コンストラクタ
     */
    public Apple(@NotNull AppleEnumColor color1, int weight1, @NotNull String taste1, int ate1) {
        setColor(color1);    /* 色を教えてほしいとお願いする。 */
        setWeight(weight1);  /* 重さを教えてほしいとお願いする。 */
        setTaste(taste1);    /* 味を教えてほしいとお願いする。 */
        setAte(ate1);        /* 食べたい量を教えてほしいとお願いする。 */
    }

    /**
     * 君が変な色のリンゴを作ろうとしてないか見てくれるセッターさん(setter)。
     * if (...) {..} っていうのは、「もし、(...)だったら、{...}しよう」っていうもの。
     * color == null で、「色 が ない のか」を確認してくれる。もし正しければ（色がなしだったら）、{色は無しにできません}と教えるよ。
     * もし間違っていたら(色がなしzは無くて、赤"や"青"って君が作るときに教えてくれたら)、{} の中のことをしないよ。
     */
    public void setColor(@NotNull AppleEnumColor color1){
        if (color1 == null){
            /* null は、色がないこと。 */
            throw new IllegalArgumentException("色は無しにはできません。");
        }

        /* もし間違っていたら(君が色をしっかりつけていたら)、color 君に 色を入れる。 */
        this.color = color1;
    }

    /**
     * 君がりんごを食べすぎていないか見てくれるセッターさん。
     * もし、(きみが150g のリンゴよりもおおくたべようとしていたら)、、{「りんごを食べ過ぎですと」教える}
     */
    public void setAte(int ate1){

        /*
         ate > 150 は、きみが食べるリンゴの量 が150g よりも多いという意味。
         これが正しかったら、{りんごを食べ過ぎです}と教える、
        */
        if (ate1 > 150){
            throw new IllegalArgumentException("りんごを食べ過ぎです。");
        }

        /* これが間違っていたら(君が食べたいリンゴの量が正しかったら)、、ate 君に君が食べたい量を入れるよ。 */
        ate = ate1;

    }

    /**
     * 作りたいリンゴが軽すぎないか見てくれるセッターさん。
     */
    public void setWeight(int weight1) {
        /*
         weight < 0 は、きみが食べるリンゴの量 が0g よりも小さいという意味。
         これが正しかったら、{「りんごが軽すぎるよ。」と教える。}
        */
        if (weight1 < 0) {
            throw new IllegalArgumentException("君の作りたいりんごが軽すぎるよ。");
        }

        /*
         これが間違っていたら(りんごが軽くなかったら)、今度は重過ぎるか見るよ。
         weight > 500 というのは、(もし君の作りたいりんごの重さが500g をこえている)という意味。
         これが正しかったら、{「君の作りたいりんごが重すぎるよ。」と教える。}
        */
        else if (weight1 > 500) {
            throw new IllegalArgumentException("君の作りたいりんごが重すぎるよ。");
        }

        /* 全部間違っていたら(君が正しい重さのりんごを作っていたら)、君が作りたいりんごの量をweight 君に入れるよ。 */
        weight = weight1;

    }

    /**
     * 君が変な味を作ろうとしていないか見てくれるセッターさん。
     */
    public void setTaste(@NotNull String taste1) {
        if (taste1 == null){
            /*
             taste1 == null というのは。「君が味のなしにしようとしている」という意味。
             もし正しかったら、{「味のないりんごはつくれないよ。」と教える。}
            */
            throw new IllegalArgumentException("味のないりんごは作れないよ。");
        }

        /* もし間違っていたら（君がなにか味を付けていたら）、taste 君に入れて上げるよ。 */
        taste = taste1;
    }

    /**
     * もし君がリンゴを食べた後に、残ったりんごの重さを知りたかったら、
     * ゲッターに聞こう。
     */
    public int getRemainingWeight() {
        return weight - ate; // 君が作ったりんごの重さ から 君が食べた量 を引き算して、残ったりんごの量をゲッターさんが返してくれるよ。
    }

    /**
     * 恥ずかしがりやなate 君の"中に入れたもの"について知りたいなら、ゲッターさんが教えてくれるよ。
     */
    public int ate(){
        return ate; /* "食べた量"(ate)をゲッターが返してくれる。 */
    }

    /**
     * 恥ずかしがりやなtaste 君の"中に入れたもの"について知りたいなら、ゲッターさんが教えてくれるよ。
     */
    public String getTaste(){
        return taste; /* "味"(taste)をゲッターが返してくれる。 */
    }

    /**
     * String: 戻り値が文字列
     */
    public AppleEnumColor getColor() {
        return color;
    }

    /**
     * boolean: 真偽値を返す
     */
    public boolean isHeavy() {
        return weight > 200; /* 200g より重いかどうか */
    }

    /* 君がリンゴを食べたとき、どんな感じになるか教えてくれる、メソッドeat 君。 */
    public void eat() {
        System.out.println(weight + "g の " + color.getLabel() + " のリンゴを" + ate + "g 食べた。");
    }
}