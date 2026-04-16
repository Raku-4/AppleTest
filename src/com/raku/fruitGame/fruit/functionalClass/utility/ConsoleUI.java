package com.raku.fruitGame.fruit.functionalClass.utility;

import com.raku.fruitGame.enum_color.AppleEnumColor;
import com.raku.fruitGame.enum_color.BananaEnumColor;
import com.raku.fruitGame.fruit.functionalClass.FruitRecord;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * コンソール表示・入力補助に関するユーティリティ。
 *
 * <p>このクラスは状態を持たないため、すべて static メソッドで提供します。</p>
 */
public final class ConsoleUI {
    private ConsoleUI() {
        // ユーティリティクラスなのでインスタンス化を禁止
    }

    /**
     * FruitInterface<?> の setColor 呼び出しを安全に仲介します。
     *
     * <p>ワイルドカード型 (<?>) はそのままでは書き込み禁止なので、
     * 実際の果物種別に応じて型キャストしてから setColor を実行します。</p>
     */
    @SuppressWarnings("unchecked")
    public static void setColorWithEnum(FruitInterface<?> fruit, int fruitIndex, String colorText) {
        try {
            if (fruitIndex == 0) {
                // りんご: AppleEnumColor を期待
                AppleEnumColor color = AppleEnumColor.valueOf(colorText.toUpperCase(Locale.ROOT));
                ((FruitInterface<AppleEnumColor>) fruit).setColor(color);
            } else if (fruitIndex == 1) {
                // ばなな: BananaEnumColor を期待
                BananaEnumColor color = BananaEnumColor.valueOf(colorText.toUpperCase(Locale.ROOT));
                ((FruitInterface<BananaEnumColor>) fruit).setColor(color);
            } else {
                // 新規果物: 文字列色をそのまま受理
                ((FruitInterface<String>) fruit).setColor(colorText);
            }
        } catch (IllegalArgumentException ex) {
            // enum名が不正でもゲーム継続を優先し、文字列として扱います。
            ((FruitInterface<String>) fruit).setColor(colorText);
        }
    }

    /**
     * 指定果物の履歴を見やすい形式で表示します。
     */
    public static void printHistory(Map<String, List<FruitRecord>> history, String fruitName) {
        List<FruitRecord> list = history.getOrDefault(fruitName, Collections.emptyList());
        if (list.isEmpty()) {
            System.out.println("- " + fruitName + " の履歴はまだありません。");
            return;
        }

        System.out.println("- " + fruitName + " の履歴");
        for (FruitRecord record : list) {
            System.out.println("  色: " + record.color() + " / 重さ: " + record.weight() + "g");
        }
    }

    /**
     * 数値パースを例外非依存の戻り値で提供します。
     *
     * <p>成功: Integer、失敗: null</p>
     */
    public static Integer tryParseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
