package com.raku.apple_test.setter;

// この"AppleSetter" 君は、"りんご" について教えてくれるよ。
public class AppleSetter {

    // private というのは、他の人から見られたくないものや、変にいじられたりするのを防ぐために使うよ。
    // ここでは、color 君や weight 君は、このAppleSetter の中でしか見られたくないみたい。恥ずかしがり屋だね。
    private final String color;
    // ここに、AppleBasic 君と同じように、色についての箱を用意するよ。
    // final というのは、color 君に一度色を入れたら、もう入れ替えることは出来ないという事を教えてくれるよ。

    // ここに新しく、りんごの重さについて教えてくれる箱をおくよ。箱の名前は"weight"。weight 君は、日本語で"重さ" というよ。分かりやすいね。
    // weight 君には、"150" や "200" などの「りんごの重さ」が入れられるよ。
    private int weight;

    // これもコンストラクタ。
    public AppleSetter(String color1, int weight1){
        this.color = color1; // color 君はcolor1(赤) だよって教えてくれている
        setWeight(weight1);
        // weight 君はweight1(150) だよって教える前に、「weight1 君って、実はweight 君を
        // 変にいじろうとしていないかな」というのを確認するために、setWeight というメソッドに確認してもらうよ。
    }

    // 今回の部品は、weight1 って本当にりおんごの重さとして大丈夫かな
    // りんごが軽すぎたり、重すぎたりしたら「weight 君に入れられない」ということを教えてくれるよ。
    // もしweight1 君が普通だったら、weight 君に入れられるよ。
    // こうやって、このsetWeight 君は「君がつくろうとしているものは本当に大丈夫なのかな？」というのを確認してくれる。
    // この人のことを、みんなは「セッター」って呼ぶよ。
    public void setWeight(int weight1){

        if (weight1 <= 0){
            // もし軽すぎるりんごを君が作ろうとしていたら、ここでとめてくれるよ。
            throw new IllegalArgumentException("りんごが軽すぎるよ。");

        } else if (weight >= 500) {
            // もし重すぎるりんごを君が作ろうとしていたら、ここでとめてくれるよ。
            throw new IllegalArgumentException("りんごが重すぎるよ。");

            // やっぱり普通だったら、weight 君に入れてあげるよ。
        } else {
            this.weight = weight1;
        }
    }

    // 今回は、もし君が作ったりんごを食べたら「weight gのcolor 色のりんごを食べた。」ということを教えてくれるよ。
    // さらに出来ることが増えたね。
    public void eat() {
        System.out.println(weight + "g の" + color + "色のあまいりんごを食べた。");
    }

    // もし君が、恥ずかしがり屋なcolor 君とweight 君のことをどうしても見たいなら、「ゲッター」 さんにおねがいすると紹介してくれるよ。

    // ゲッター（Getter）：重さを知りたい人に見せるための部品
    // 「重さを教えて！」というお願いを聞いてくれる窓口
    public int getWeight() {
        return weight;
        // return っていうのは、君がゲッターさんに教えてと頼んだ時、
        // その重さ(weight)や色(color) についてゲッターか間接的に教えてくれるよ。
        // 直接は会えないけれど、君はゲッターの「return weight; 」によってweight 君について教えてくれるよ。
    }

    // ゲッター（Getter）：色を知りたい人に見せるための部品
    // 「色を教えて！」というお願いを聞いてくれる窓口
    public String getColor() {
        return color;
        // これもweight 君の時と同じく、color 君には直接会えないけれど、「return color;」によってゲッターさんがcolor 君について教えてくれるよ。
    }
}