package com.raku.fruitGame.fruit.functionalClass.utility;

import java.util.List;
import java.util.Scanner;

/**
 * 入力処理をメインロジックから分離するためのユーティリティ。
 *
 * <p>入力検証の while ループをここに集約することで、
 * MainInteractive 側の可読性を保ちます。</p>
 */
public final class Reader {
    private Reader() {
        // ユーティリティクラスのためインスタンス化しません。
    }

    /**
     * 0以上の整数を受け取るまで繰り返し入力を求めます。
     */
    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            Integer value = ConsoleUI.tryParseInt(scanner.nextLine().trim());
            if (value != null && value >= 0) {
                return value;
            }
            System.out.println("0以上の整数を入力してください。");
        }
    }

    /**
     * 既存果物名を入力させ、そのインデックスを返します。
     * 候補外入力は -1 を返して「新規果物作成」の合図にします。
     */
    public static int readFruitIndex(Scanner scanner, List<String> fruitNames) {
        while (true) {
            System.out.print("（りんご / ばなな / 新規名）>> ");
            String name = scanner.nextLine().trim();
            int idx = fruitNames.indexOf(name);
            if (idx >= 0) {
                return idx;
            }

            // ここで再ループさせず -1 を返す設計により、
            // 呼び出し元がそのまま新規果物作成フローへ遷移できます。
            System.out.println("候補にない名前です。新規名にする場合は後続の作成手順で登録されます。");
            return -1;
        }
    }

    /**
     * y/n の二択入力を読み取ります。
     */
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n) >> ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("yes")) {
                return false;
            }
            if (line.equalsIgnoreCase("n") || line.equalsIgnoreCase("no")) {
                return true;
            }
            System.out.println("y または n で入力してください。");
        }
    }
}
