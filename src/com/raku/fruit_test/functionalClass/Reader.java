package com.raku.fruit_test.functionalClass;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Scanner;

public class Reader {
    public static int readPositiveInt(@NotNull Scanner scanner) {
        while (true) {
            System.out.print("どのくらいの重さで作りたいですか（g）>> ");
            String s = scanner.nextLine().trim();
            Integer v = ConsoleUI.tryParseInt(s);
            if (v != null && v >= 0) return v;
            System.out.println("正しい整数値（0以上）を入力してください。");
        }
    }

    public static int readFruitIndex(@NotNull Scanner scanner, @NotNull List<String> fruitNames) {
        while (true) {
            System.out.print("（りんご / ばなな）>> ");
            String name = scanner.nextLine().trim(); // 空白文字(Unicode の 0020 番以下の文字)を除くために、.trim() を追記しました。

            int index = fruitNames.indexOf(name); /* name に代入された、"りんご"または"ばなな"という名前の存在。戻り値は、
            ①もしi 番目のインデックスで fruitNames と位置するname があったら、そのインデックス番号を返す。
            ②存在しなければ、-1 を返す。*/

            if (index >= 0) return index; /* fruitIndex に戻り値index(fruitName に格納されたインデックスの番号) を返す。return なので、
            ここでこのメソッドはループを強制的に切って終了する。*/
            System.out.println("正しい入力をしてください。"); /* 一度もreturn されなかったら、このメッセージをだしてもう一度ループをする。*/
        }
    }

    public static @NotNull String readColor(@NotNull Scanner scanner, int fruitIndex) {
        String prompt = (fruitIndex == 0)
                ? "何色で作りたいですか（AppleEnumColor：例 RED）>> " // fruitNames のインデックス番号 0 ならりんご、
                : "何色で作りたいですか（BananaEnumColor：例 YELLOW）>> "; // インデックス番号 1 ならばなな。
        return readLine(scanner, prompt);
    }

    public static boolean readYesNo(@NotNull Scanner scanner) {
        while (true) {
            System.out.print("まだ食べたいですか？（y/n）" + " ");
            String s = scanner.nextLine().trim();
            if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes")) return true;
            if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no")) return false;
            System.out.println("y / n で入力してください。");
        }
    }

    @NotNull
    public static String readLine(@NotNull Scanner scanner, String prompt) {
        System.out.print(prompt); // readColor で作った質問（文字）を表示する。
        return scanner.nextLine().trim(); // 色の名前を戻り値にする。。
    }
}
