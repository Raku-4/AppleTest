/**
 * Code Written By Raku.
 * Code's Description Written By Gemini, Raku.
 */
package com.raku.apple_test.all_argument.enum_color;

@SuppressWarnings("UnnecessaryModifier")
public enum AppleEnumColor {

    // 【Enum定数の定義】
    // ここに書かれているものが、この Enum で使える「色の種類（定数）」のすべてだよ。
    // リンゴの色は「赤」「青」「緑」の3種類しか選べない、というルールを決めているんだ。
    // ( ) の中にある "赤", "青", "緑" は、その定数に「付属させるデータ」だよ。
    RED("赤"),
    BLUE("青"),
    GREEN("緑"),
    YELLOW("黄"),
    NULL("null");

    // 【フィールドの定義】
    // private final: 定数に付属させるデータ（この場合は日本語のラベル）を保持する箱だよ。
    // final だから、一度決めたら変えられない、安全なデータだね。
    private final String label;

    // 【コンストラクタ】
    // Enumのコンストラクタは、public をつけずに書くよ。
    // これは、上にある RED("赤") のように、定数が作られる時にだけ呼ばれる特別なコンストラクタだよ。
    // ( ) の中の "赤" という文字列が、引数 label に渡されるんだ。
    private AppleEnumColor(String label1){
        this.label = label1; // 渡された "赤" を、この Enum 定数が持つ箱 (this.label) に保存するよ。
    }

    // 【ゲッター】
    // この定数に付属しているデータ（日本語ラベル）を知りたい時に使う、公開された窓口だよ。
    // Apple クラスの getColor() メソッドで使われていたね！
    public String getLabel(){
        return label;
    }
}