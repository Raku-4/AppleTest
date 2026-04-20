/**
 * December fourteenth, 2025
 * Class, Code Made by Raku.
 * Code Arranged better by Copilot , (Almost) Gemini.
 * Code Described by Raku , Copilot, Gemini.
 * <p>
 * ジェネリクス、どんな風にいじられても崩れない堅牢な設計、
 * リスト、配列、マップ、レコードクラス、ファイルの作成、書き込み、読み出し、
 * 実行者に与える自由なオブジェクトの生成、匿名クラス、
 * インターフェース、ストリームAPI、
 * ユーティリティクラスの作成による責務の分離、
 * そして未来の自分と未来の誰かに向けた超詳細な解説を添えたクラス
 * (これは技術以前の、「エンジニアとしての品格」です。コードは書く時間より、
 * 読まれる時間の方が圧倒的に長いため、言語化能力が高いことはチーム開発で最強の武器になります。)。
 */
package com.raku.fruitGame.interactive;
/*
package : このファイルが属する論理的な名前空間を指定します。ビルド時・実行時のクラス検索やアクセス修飾子のスコープに影響します。通常、src/main/java のディレクトリ構成と一致させます。
 */

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import com.raku.banana_test.BananaEnumColor;
import com.raku.banana_test.BananaInteractive;
import com.raku.fruit_test.FruitInterface;
import com.raku.fruit_test.functionalClass.ConsoleUI;
import com.raku.fruit_test.functionalClass.FruitHistory;
import com.raku.fruit_test.functionalClass.Reader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 *1. 今回のクラス作成においける課題点。
 *  Map と List が混同して読みにくくなっている。
 *<p>
 * 今の状態: 「Map の中から String で取り出して、出てきた List を forEach で回して、その中の Record の weight やcolor を取り出して、呼び出し元（Main）に返す....。」
 *<p>
 * Mainの負担: データの「しまい方」まで全部把握してなきゃいけない。
 *<p>
 * 理想の状態: 「fruitHistory 君、『りんご』の履歴をちょうだい」
 *<p>
 * → Mainの負担: 欲しいものを言うだけ。中身が Map なのか List なのかは知らなくていい。
 */
public class MainInteractive {


    public static final List<String> fruitNames = new ArrayList<>();
        /*
        果物名のリスト。インデックス順が後の basket と一致する前提です。
        final は "再代入"は不可だが、要素の追加は可能（参照は不変でも、内部状態は変えられる）。
         */

    private static void ensureFruitName(String fruitName) {
        if (!fruitNames.contains(fruitName)) {
            fruitNames.add(fruitName);
        }
    }

    private static void ensureDefaultFruitNames() {
        ensureFruitName("りんご");
        ensureFruitName("ばなな");
    }

    private static void saveHistory(@NotNull FruitHistory fruitHistory, Path path) {
        try {
            fruitHistory.saveCsv(path);
            System.out.println("\nデータを保存しました。");
        } catch (IOException e) {
            System.err.println("\n保存に失敗しました：" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Path path = Path.of("fruit_history.csv");

        ConsoleUI ui = new ConsoleUI(new Scanner(System.in));
        /*
        プログラムの入口であるscanner 君。コンソール入出力のため Scanner を標準入力（System.in）にバインド。ユーザーの入力文字列を読み取ります。
         */

        FruitHistory fruitHistory = new FruitHistory();

        final List<FruitInterface<?>> basket = new ArrayList<>();
        /*
        異種ジェネリクス（色の型が異なる）を同じコレクションで扱うため、FruitInterface<?> としてワイルドカード受け。
        ?(アンバウンド・ワイルドカード) は「色型が不明」を表し、読み取りはできるが書き込み（setColor(T)）はそのままだと不可。後述の安全キャストで解決します。
         */

        try {
            fruitHistory.loadCsv(path, fruitNames);
            System.out.println("前回の履歴を" + fruitHistory.size() + "件読み込みました。");
        } catch (IOException e) {
            System.out.println("\n履歴の読み込みに失敗しました。\n初回起動であれば気にしなくて大丈夫です。");
        }

        ensureDefaultFruitNames();

        basket.add(new AppleInteractive(AppleEnumColor.RED, 150, 0));    /* FruitInterface<AppleEnumColor> 型 */
        basket.add(new BananaInteractive(BananaEnumColor.YELLOW, 110, 0)); /* FruitInterface<BananaEnumColor> 型 */
        /*
        既存果物の初期インスタンスをバスケットに格納。
        りんごは AppleEnumColor.RED、ばななは BananaEnumColor.YELLOW。
        重さ（150g or 110g）と食べた量（0g）を初期値に設定。
         */

        /* 履歴（果物名 -> 色・重さ）
        final Map<String, List<FruitRecord>> history = new LinkedHashMap<>();
        history.put("りんご", new ArrayList<>());
        history.put("ばなな", new ArrayList<>());
        (元のコード)
         */
        fruitHistory.recordCreation("りんご", AppleEnumColor.RED.name(), basket.get(0).getRemainingWeight());
        fruitHistory.recordCreation("ばなな", BananaEnumColor.YELLOW.name(), basket.get(1).getRemainingWeight());


        /*
        生成履歴を保持する連想配列。LinkedHashMap は挿入順維持のため、表示順が安定します。

        各果物名ごとに "FruitRecord(color, weight)" のリストを持ちます
         */

        System.out.println("最初に食べたい果物を入力してください（りんご / ばなな）");
        int fruitIndex = Reader.readFruitIndex(ui.scanner(), fruitNames);
        /*
        ユーザーに最初の果物選択を促し、検証付きでインデックスを取得。
        存在しない名前は再入力させる設計です。以下のコードを読み取るためには、readFruitIndex のwhile ループを抜ける
        (つまり実行者が正しい入力を行う) 必要があります。
         */

        FruitInterface<?> current = basket.get(fruitIndex);
        String currentName = fruitNames.get(fruitIndex);
        /*
        選択されたインデックスから、現在の果物（実体）と表示名を決定。
        以降、current / currentName を参照します。
        */

        /*
        (元コード)
        history.get(currentName).add(new FruitRecord(current.getColor(), current.getRemainingWeight()));
        この時点の色と重さを履歴に1件追加。記録は「生成イベント」単位です。
         */


        System.out.println("新しく " + current.getColor() + " 色の "
                + current.getRemainingWeight() + "g の " + currentName + " を作りました。");
        System.out.println("""
                \n食べたい量を入力してください。\
                今現在の果物に関する生成
                リスト表示は "list"、全ての果物に関する生成リスト表示は"listAll"、
                やめる場合は "cancel" を入力してください。""");
        /*
        ユーザー向け出力。現在の果物の初期状態を通知し、
        その後の操作（list / cancel / 数値）を案内します
         */


        // 食べるループ
        while (true) {
            System.out.print("\n食べたい量 >> ");
            String cmd = ui.scanner().nextLine().trim();
            /*
            無限ループでユーザー入力を受け続けます。
            trim() は前後スペースを除去し、入力ブレを減らします。
             */

            if (cmd.equalsIgnoreCase("cancel")) {
                System.out.println("\n" + currentName + "は放置されて腐ってしまいました。残りは "
                        + current.getRemainingWeight() + "g です。");

                System.out.println("\n今日は合計" + fruitHistory.size() + "個の果物達を作りました!");

                saveHistory(fruitHistory, path);

                break;
                /*
                cancel：現セッションを中止。状態を保持したままループを抜けます。
                ここでは「腐った」というロールメッセージを出しています。
                */
            }

            if (cmd.trim().equalsIgnoreCase("list")) {
                ConsoleUI.printHistory(fruitHistory.viewAll(), currentName);
                continue;
                /*
                list：履歴を表示。処理は続行（continue）し、次の入力を待ちます。
                 */
            }

            if (cmd.trim().equalsIgnoreCase("listAll")) {
                for (String name : fruitNames) {
                    ConsoleUI.printHistory(fruitHistory.viewAll(), name);
                }
                continue;
            }

            Integer ate = ConsoleUI.tryParseInt(cmd);
            if (ate == null || ate < 0) {
                System.out.println("\n正しい整数値（0以上）を入力してください。");
                continue;
            }
            /*
            数値入力の検証。tryParseInt は例外を返り値で吸収するユーティリティ（nullを返されたら、"不正"という意味）。
            負数は禁止し、再入力を促します。
             */


            current.setAte(current.getAte() + ate);
            /*
            累積摂取モデル：既に食べた量getAte()に今回の量ate を加算。
            setAte は「総摂取量の更新」です。
             */


            long remaining = current.getRemainingWeight(); // 残っている量を計算。
            /*
            残量判定：

            > 0：残っている。
            == 0：食べ切った。
            < 0：希望量が超過（不足量を正数表示するため -remaining）。

            この分岐が「次の生成」へ進む導線になります。
             */
            if (remaining > 0) {
                System.out.println("\n" + currentName + "は残り " + remaining + "g です。");
            } else if (remaining == 0) {
                System.out.println("\nあなたは" + currentName + "を食べきりました。");
                saveHistory(fruitHistory, path);

                break;
            } else {
                System.out.println("\nあなたが食べたかった量が " + (-remaining) + "g 足りなかったようです。");

                // 続けるか
                if (!Reader.readYesNo(ui.scanner())) { // ! (つまりfalse)  ×  false  =  true
                    System.out.println("\nおしまい。");
                    return;
                }
                /*
                続行確認：y/n のブール入力。false（No）ならプログラム終了。
                ここは早期 returnで明確に終端します。
                 */

                // 次の選択（既存 or 新規）
                System.out.println("\n今度は何を食べたいですか？（りんご / ばなな / 新規名）");
                System.out.print("\n果物名 >> ");
                String nextName = ui.scanner().nextLine().trim();

                int nextIndex = fruitNames.indexOf(nextName);
                if (nextIndex >= 0) { /* fruitNames にある名前の果物だったら */
                    /* 既存果物を新しく生成 */


                    FruitInterface<?> nextFruit = basket.get(nextIndex);
                    String colorText = Reader.readColor(ui.scanner(), nextIndex);
                    int weight = Reader.readPositiveInt(ui.scanner());
                    /*
                    既存果物を再生成するため、色と重さを入力します。
                    readColor は果物別の列挙名ヒント付きプロンプトを表示。
                    readPositiveInt は0以上の整数のみ受理。
                     */


                    /* ★ ジェネリクス：型に合わせてキャストしてから setColor */
                    ConsoleUI.setColorWithEnum(nextFruit, nextIndex, colorText);
                    /*
                    重要：FruitInterface<?> はそのままでは setColor(T) を呼べません（アンバウンド・ワイルドカード書き込み禁止）。

                    実体の型に安全キャストしてから setColor します（AppleEnumColor / BananaEnumColor / String）。
                    列挙名が不正なら IllegalArgumentException を捕捉し、文字列設定にフォールバック。
                     */


                    nextFruit.setWeight(weight);
                    nextFruit.setAte(0);
                    /*
                    weight はユーザーの希望の値にし、
                    新しいロットは**未摂取（0）**から開始。重さはユーザー指定値。
                     */

                    String name = fruitNames.get(nextIndex);
                    fruitHistory.recordCreation(name, nextFruit.getColor(), nextFruit.getRemainingWeight());
                    /*
                    生成イベントを履歴へ記録。
                    色は getColor() の文字列表現。
                    重さは getRemainingWeight()（生成直後＝総重量）。
                     */

                    System.out.println("\n新たに " + nextFruit.getColor() + " 色の "
                            + nextFruit.getRemainingWeight() + "g の " + name + " を作りました。");
                    /* ユーザーへ生成完了を告知。 */

                    current = nextFruit;
                    currentName = name;


                } else { /* fruitNames に無い新規果物を、匿名クラスで追加。 */
                    /* 新規果物（色は自由文字列） -> FruitInterface<String> として匿名クラスを実装 */
                    String colorText = Reader.readLine(ui.scanner(), "何色で作りたいですか。 >> ");
                    long weight = Reader.readPositiveInt(ui.scanner());
                    /*
                    未知の名前は新規果物。色は自由文字列、重さは 0 以上の整数。
                    */

                    FruitInterface<String> custom = new FruitInterface<>() {
                        String color = colorText;
                        long weightVal = weight;
                        long ateVal = 0;
                        /*
                        匿名クラスで FruitInterface<String> を実装。
                        色型を String に固定することで、柔軟な色入力を許容。
                        */

                        @Override
                        public void setColor(String c) {
                            this.color = c;
                        }

                        @Override
                        public void setAte(long a) {
                            this.ateVal = Math.max(0, a); // 引数が0 より大きいかをMath のstatic メソッドmax で使用する。
                        }

                        @Override
                        public void setWeight(long w) {
                            this.weightVal = Math.max(0, w);
                        }

                        @Override
                        public long getRemainingWeight() {
                            return this.weightVal - this.ateVal;
                        }

                        @Override
                        public long getAte() {
                            return this.ateVal;
                        }

                        @Override
                        public String getColor() {
                            return this.color;
                        }
                    };

                    /*
                    FruitInterface のジェネリクスの要求には、String型を返すようにしています。
                    これにより、柔軟な色の追加を行えます（但し色の名前を実行者が誤ってもそのまま新規の色として追加される。
                    これは、全ての果物の色を網羅し列挙した果物FruitColorEnum クラスの設計を考える必要がある。）

                    残量は重さ−食べた量。getAte は累計摂取量を返します（つまり重さを減らすのではなく食べる量を累積して増やす）。
                    */

                    fruitNames.add(nextName); /* 新たな果物の名前を追加。*/
                    basket.add(custom); /* 新たな果物オブジェクトをbasket に追加。*/

                    fruitHistory.ensureFruit(nextName); /* Map キーに新たな果物の名前と、そのキーの要素を格納する空間をペアで追加。 */
                    fruitHistory.recordCreation(nextName, custom.getColor(), custom.getRemainingWeight());
                    /* 新たな果物オブジェクトcustom の色と重さのデータを格納するレコードクラスをインスタンス化して追加。 */

                    /*
                    以上で、新規果物を公式リストへ登録。
                    以後は既存果物と同じ扱いで履歴・選択に参加できます
                    */


                    System.out.println("あ\nなたの考えた新たな果物「" + nextName + "」を作りました。");
                    System.out.println("\n色：" + custom.getColor() + " / 重さ：" + custom.getRemainingWeight() + "g");
                    /* ユーザーへ新規作成の完了を告知。 */

                    current = custom;
                    currentName = nextName;
                }
            }
        }
    }
}