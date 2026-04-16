package com.raku.fruitGame.interactive;

import com.raku.fruitGame.enum_color.AppleEnumColor;
import com.raku.fruitGame.enum_color.BananaEnumColor;
import com.raku.fruitGame.fruit.functionalClass.userMethod.CustomFruit;
import com.raku.fruitGame.fruit.functionalClass.utility.ConsoleUI;
import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import com.raku.fruitGame.fruit.functionalClass.utility.FruitInterface;
import com.raku.fruitGame.fruit.functionalClass.utility.Reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * FruitGame のコンソール版メインクラス。
 *
 * <p>このクラスの主な責務:</p>
 * <ul>
 *   <li>ユーザー入力を受け取り、果物オブジェクトへ反映する。</li>
 *   <li>履歴クラス (FruitHistory) と連携して生成ログを保存・読込する。</li>
 *   <li>既存果物 (りんご/ばなな) と新規果物 (CustomFruit) を同一フローで扱う。</li>
 * </ul>
 */
public class MainInteractive {

    /**
     * 履歴保存を1か所にまとめるヘルパーメソッド。
     * 例外処理を main から分離し、分岐を見やすくします。
     */
    private static void saveHistory(FruitHistory history, Path path) {
        try {
            history.saveCsv(path);
            System.out.println("履歴を保存しました。");
        } catch (IOException e) {
            // 保存失敗時もゲーム自体は継続可能なので、ここでは通知のみに留めます。
            System.out.println("履歴の保存に失敗しました: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 標準入力を読む Scanner。ゲーム終了まで使い続ける想定です。
        Scanner scanner = new Scanner(System.in);

        // CSV の保存先。実行ディレクトリ直下に fruit_history.csv が生成されます。
        Path path = Path.of("fruit_history.csv");

        // 履歴を担当するクラス。Map + List の構造はこのクラス側に隠蔽します。
        FruitHistory history = new FruitHistory();

        // fruitNames と basket は「同じインデックスが同じ果物」を表すペア構造です。
        List<String> fruitNames = new ArrayList<>();
        List<FruitInterface<?>> basket = new ArrayList<>();

        // 初期データ: デフォルトで扱える果物を2種類登録。
        fruitNames.add("りんご");
        fruitNames.add("ばなな");
        basket.add(new AppleInteractive(AppleEnumColor.RED, 150, 0));
        basket.add(new BananaInteractive(BananaEnumColor.YELLOW, 110, 0));

        // 前回履歴がある場合は読み込み、fruitNames 側も同期します。
        try {
            history.loadCsv(path, fruitNames);
            System.out.println("履歴を " + history.size() + " 件読み込みました。");
        } catch (IOException e) {
            System.out.println("履歴読み込みに失敗しました（初回なら問題ありません）。");
        }

        // デフォルト果物の「現在の状態」も履歴に反映して、一覧時の空表示を防ぎます。
        history.recordCreation("りんご", basket.get(0).getColor(), basket.get(0).getRemainingWeight());
        history.recordCreation("ばなな", basket.get(1).getColor(), basket.get(1).getRemainingWeight());

        System.out.println("食べたい果物を選んでください。入力例: りんご / ばなな");
        int index = Reader.readFruitIndex(scanner, fruitNames);

        // current/currentName は「今まさに食べている対象」を保持します。
        FruitInterface<?> current;
        String currentName;

        if (index >= 0) {
            // 既存果物を選んだケース
            current = basket.get(index);
            currentName = fruitNames.get(index);
        } else {
            // 候補外入力は新規果物として受け付けるケース
            System.out.print("新規果物名 >> ");
            currentName = scanner.nextLine().trim();

            System.out.print("色 >> ");
            String color = scanner.nextLine();

            int weight = Reader.readPositiveInt(scanner, "重さ(g) >> ");

            CustomFruit custom = new CustomFruit(currentName, color, weight);
            fruitNames.add(currentName);
            basket.add(custom);

            current = custom;
            history.recordCreation(currentName, current.getColor(), current.getRemainingWeight());
        }

        System.out.println("現在: " + currentName + " / 色: " + current.getColor() + " / 重さ: " + current.getRemainingWeight() + "g");
        System.out.println("整数で食べる量を入力。list で履歴表示、switch で果物変更、cancel で終了。");

        // メインループ: cancel が入力されるまで継続します。
        while (true) {
            System.out.print("食べる量 >> ");
            String cmd = scanner.nextLine().trim();

            if (cmd.equalsIgnoreCase("cancel")) {
                saveHistory(history, path);
                System.out.println("終了します。");
                return;
            }

            if (cmd.equalsIgnoreCase("list")) {
                // 現在の果物だけ表示する簡易一覧
                ConsoleUI.printHistory(history.viewAll(), currentName);
                continue;
            }

            if (cmd.equalsIgnoreCase("switch")) {
                // 食べる対象を切り替える分岐
                System.out.print("次の果物名 >> ");
                String nextName = scanner.nextLine().trim();
                int nextIndex = fruitNames.indexOf(nextName);

                if (nextIndex >= 0) {
                    // 既存果物へ切替
                    FruitInterface<?> next = basket.get(nextIndex);

                    System.out.print("色(enum名または文字列) >> ");
                    String colorText = scanner.nextLine();
                    ConsoleUI.setColorWithEnum(next, nextIndex, colorText);

                    int weight = Reader.readPositiveInt(scanner, "重さ(g) >> ");
                    next.setWeight(weight);
                    next.setAte(0); // 新ロットとして食べた量を初期化

                    current = next;
                    currentName = fruitNames.get(nextIndex);
                    history.recordCreation(currentName, current.getColor(), current.getRemainingWeight());
                } else {
                    // 未登録名は CustomFruit として追加
                    System.out.print("色 >> ");
                    String color = scanner.nextLine();
                    int weight = Reader.readPositiveInt(scanner, "重さ(g) >> ");

                    CustomFruit custom = new CustomFruit(nextName, color, weight);
                    fruitNames.add(nextName);
                    basket.add(custom);

                    current = custom;
                    currentName = nextName;
                    history.recordCreation(currentName, current.getColor(), current.getRemainingWeight());
                }

                System.out.println("切り替え: " + currentName + " / " + current.getColor() + " / " + current.getRemainingWeight() + "g");
                continue;
            }

            // 数値入力を試す。失敗時は null なので再入力へ。
            Integer ate = ConsoleUI.tryParseInt(cmd);
            if (ate == null || ate < 0) {
                System.out.println("0以上の整数を入力してください。");
                continue;
            }

            // 累積摂取量モデル: 既存の ate に今回の値を足していく方式
            current.setAte(current.getAte() + ate);
            long remaining = current.getRemainingWeight();

            if (remaining > 0) {
                System.out.println(currentName + " の残りは " + remaining + "g");
            } else if (remaining == 0) {
                System.out.println(currentName + " を食べきりました。");
                if (Reader.readYesNo(scanner, "続けますか")) {
                    saveHistory(history, path);
                    return;
                }
            } else {
                // remaining < 0: 入力量が現在の残量を超過したケース
                System.out.println((-remaining) + "g 足りませんでした。");
                if (Reader.readYesNo(scanner, "別の果物を作りますか")) {
                    saveHistory(history, path);
                    return;
                }
            }
        }
    }
}
