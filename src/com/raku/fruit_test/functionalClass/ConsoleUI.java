package com.raku.fruit_test.functionalClass;

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import com.raku.banana_test.BananaEnumColor;
import com.raku.fruit_test.FruitInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record ConsoleUI(Scanner scanner) {
    /**
     * 型ごとにキャストして setColor を呼ぶ（FruitInterface<?> への書き込みを可能化）
     */
    @SuppressWarnings("unchecked")
    public static void setColorWithEnum(@NotNull FruitInterface<?> fruit, int fruitIndex, @NotNull String colorText) {
        try {
            if (fruitIndex == 0) { // りんご
                AppleEnumColor c = AppleEnumColor.valueOf(colorText.toUpperCase(Locale.ROOT)); // toUpperCase で大文字に変換。
                ((FruitInterface<AppleEnumColor>) fruit).setColor(c);
            } else if (fruitIndex == 1) { // ばなな
                BananaEnumColor c = BananaEnumColor.valueOf(colorText.toUpperCase(Locale.ROOT));
                ((FruitInterface<BananaEnumColor>) fruit).setColor(c);
            } else { // 新規果物（文字列）
                ((FruitInterface<String>) fruit).setColor(colorText);
            }
        } catch (IllegalArgumentException e) {
            // 無効な列挙名は文字列設定にフォールバック（最悪でも動作継続）
            ((FruitInterface<String>) fruit).setColor(colorText);
        }
    }

    /**
     履歴が空ならメッセージを返し、そうでなければ全件列挙。
     FruitRecord のフィールドは record により公開読み取り可能（r.color / r.weight とプロパティ名で参照可）。
     */
    public static void printHistory(@NotNull Map<String, List<FruitRecord>> history, String fruitName) {
        List<FruitRecord> list = history.getOrDefault(fruitName, Collections.emptyList());
        /* 特定の果物が持つ「色と重さ」のひとまとめのデータを取得するためのリスト。 */

        if (list.isEmpty()) {
            System.out.println("\nまだ履歴はありません。");
            return;
        }

        System.out.println("\nこれまでに生成された " + fruitName + " の情報リスト：");
        list.stream()
                .map(r -> "色：" + r.color() + "  重さ：" + r.weight() + "g")
                .forEach(System.out::println);
    }

    public static @Nullable Integer tryParseInt(@NotNull String s) {
        try {
            return Integer.parseInt(s); // 試しに入力を数値にしてみる。
        } catch (NumberFormatException e) {
            return null; // ダメだったら不正だと言う(戻り値をnull にする)。
        }
    }
}
