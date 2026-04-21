package com.raku.fruitGame.fruit.functionalClass.utility;

import com.raku.fruitGame.fruit.functionalClass.FruitRecord;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 果物生成履歴を管理するクラス。
 *
 * <p>内部表現は Map&lt;果物名, その果物の生成記録リスト&gt; です。</p>
 * <p>LinkedHashMap / LinkedHashSet を使っている理由は、挿入順を維持して表示順を安定させるためです。</p>
 */
public class FruitHistory {
    /**
     * 履歴本体。
     * キー: 果物名 (例: "りんご")
     * 値: その果物の履歴 (FruitRecord のリスト)
     */
    private static final Map<String, List<FruitRecord>> history = new LinkedHashMap<>();

    /**
     * 指定した果物名の「入れ物」を必ず用意します。
     * すでに存在する場合は何もしません。
     */
    public static void ensureFruit(String fruitName) {
        history.computeIfAbsent(fruitName, key -> new ArrayList<>());
    }

    /**
     * 生成履歴を1件追加します。
     * 完全一致レコードは重複として無視します。
     */
    public static void recordCreation(String fruitName, String color, long weight) {
        recordCreation(fruitName, color, weight, "", "通常", 0L, -1);
    }

    public static void recordCreation(
            String fruitName,
            String color,
            long weight,
            String taste,
            String maturity,
            long elapsedSeconds,
            int treeId
    ) {
        ensureFruit(fruitName);
        FruitRecord record = new FruitRecord(fruitName, color, weight, taste, maturity, elapsedSeconds, treeId);
        List<FruitRecord> list = history.get(fruitName);
        if (!list.contains(record)) {
            list.add(record);
        }
    }

    public static void clearAll() {
        history.clear();
    }

    public static void removeTreeRecord(int treeId) {
        for (List<FruitRecord> list : history.values()) {
            list.removeIf(record -> record.treeId() == treeId);
        }
        cleanupEmptyFruits();
    }

    public static FruitRecord findLatestBagRecord(String fruitName) {
        List<FruitRecord> list = history.get(fruitName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            FruitRecord record = list.get(i);
            if (record.treeId() < 0) {
                return record;
            }
        }
        return null;
    }

    public static void replaceLatestBagRecord(String fruitName, FruitRecord replacement) {
        List<FruitRecord> list = history.get(fruitName);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            FruitRecord record = list.get(i);
            if (record.treeId() < 0) {
                list.set(i, replacement);
                return;
            }
        }
    }

    public static void removeLatestBagRecord(String fruitName) {
        List<FruitRecord> list = history.get(fruitName);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            FruitRecord record = list.get(i);
            if (record.treeId() < 0) {
                list.remove(i);
                cleanupEmptyFruits();
                return;
            }
        }
        cleanupEmptyFruits();
    }

    public static void upsertTreeState(
            int treeId,
            String fruitName,
            String color,
            long weight,
            String taste,
            String maturity,
            long elapsedSeconds
    ) {
        removeTreeRecord(treeId);
        recordCreation(fruitName, color, weight, taste, maturity, elapsedSeconds, treeId);
    }

    /**
     * 指定果物の履歴を読み取り専用で返します。
     * 呼び出し側からの add/remove を防ぐため unmodifiableList を返します。
     */
    public static @NotNull @UnmodifiableView List<FruitRecord> getHistoryView(String fruitName) {
        List<FruitRecord> list = history.getOrDefault(fruitName, Collections.emptyList());
        return Collections.unmodifiableList(list);
    }

    /**
     * 全履歴を読み取り専用で返します。
     * Map 本体だけでなく、各 List も読み取り専用化して保護します。
     */
    public static @NotNull @UnmodifiableView Map<String, List<FruitRecord>> viewAll() {
        Map<String, List<FruitRecord>> readonly = new LinkedHashMap<>();
        for (Map.Entry<String, List<FruitRecord>> e : history.entrySet()) {
            readonly.put(e.getKey(), Collections.unmodifiableList(e.getValue()));
        }
        return Collections.unmodifiableMap(readonly);
    }

    /**
     * 履歴総件数を返します。
     * 各果物ごとのリストサイズを合算しています。
     */
    public static int size() {
        int count = 0;
        for (List<FruitRecord> list : history.values()) {
            count += list.size();
        }
        return count;
    }

    /**
     * 現在の履歴を CSV として保存します。
     * フォーマット: timestamp,fruitName,color,weight
     */
    public static void saveCsv(Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("timestamp,fruitName,color,weight,taste,maturity,elapsedSeconds,treeId");

        // すべての行に同じ保存時刻を付与する設計
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        // Mapを横断してユニークレコード集合にまとめ、重複を書き出さないようにします。
        Set<FruitRecord> unique = new LinkedHashSet<>();
        for (List<FruitRecord> list : history.values()) {
            unique.addAll(list);
        }

        for (FruitRecord record : unique) {
            lines.add(csv(timestamp) + ","
                    + csv(record.fruitName()) + ","
                    + csv(record.color()) + ","
                    + record.weight() + ","
                    + csv(record.taste()) + ","
                    + csv(record.maturity()) + ","
                    + record.elapsedSeconds() + ","
                    + record.treeId());
        }

        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * fruitNames 同期が不要な呼び出し用オーバーロード。
     */
    public static void loadCsv(Path path) throws IOException {
        loadCsv(path, null);
    }

    /**
     * CSV を読み込み、履歴を再構築します。
     * fruitNames が渡された場合は、そこにも果物名を同期します。
     */
    public static void loadCsv(Path path, List<String> fruitNames) throws IOException {
        // 初回起動などでファイルがない場合は何もしません。
        if (!Files.exists(path)) {
            return;
        }

        history.clear();
        if (fruitNames != null) {
            fruitNames.clear();
        }

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        // 0行目はヘッダなので 1行目から処理
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            List<String> cols = splitCsv(line);
            if (cols.size() < 4) {
                continue; // 列不足行は破損データとしてスキップ
            }

            String fruitName = unCsv(cols.get(1));
            String color = unCsv(cols.get(2));
            long weight;
            String taste = "";
            String maturity = "通常";
            long elapsedSeconds = 0L;
            int treeId = -1;
            try {
                weight = Long.parseLong(unCsv(cols.get(3)));
            } catch (NumberFormatException ex) {
                continue; // 数値変換不能な行はスキップ
            }

            if (cols.size() >= 8) {
                taste = unCsv(cols.get(4));
                maturity = unCsv(cols.get(5));
                try {
                    elapsedSeconds = Long.parseLong(unCsv(cols.get(6)));
                } catch (NumberFormatException ignored) {
                }
                try {
                    treeId = Integer.parseInt(unCsv(cols.get(7)));
                } catch (NumberFormatException ignored) {
                }
            }

            recordCreation(fruitName, color, weight, taste, maturity, elapsedSeconds, treeId);
            if (fruitNames != null && !fruitNames.contains(fruitName)) {
                fruitNames.add(fruitName);
            }
        }
    }

    /**
     * CSV安全化。
     *
     * <p>CSVのルール上、文字列内の " は "" にエスケープし、
     * 項目全体を "..." で囲みます。</p>
     */
    @Contract(pure = true)
    private static @NotNull String csv(String text) {
        if (text == null) {
            return "\"\"";
        }
        String escaped = text.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    /**
     * csv() の逆変換。
     * 前後の引用符を剥がし、"" を " に戻します。
     */
    private static @NotNull String unCsv(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String t = text;
        if (t.startsWith("\"") && t.endsWith("\"") && t.length() >= 2) {
            t = t.substring(1, t.length() - 1);
        }
        return t.replace("\"\"", "\"");
    }

    /**
     * 1行のCSVを手動で分解します。
     *
     * <p>単純 split(",") を使わない理由:
     * 引用符内のカンマは区切りではなくデータ本体だからです。</p>
     */
    private static @NotNull List<String> splitCsv(@NotNull String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == '"') {
                    // 連続する "" はエスケープされた引用符として扱う
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    current.append(ch);
                }
            } else if (ch == ',') {
                cols.add(current.toString());
                current.setLength(0);
            } else if (ch == '"') {
                quoted = true;
            } else {
                current.append(ch);
            }
        }

        cols.add(current.toString());
        return cols;
    }

    private static void cleanupEmptyFruits() {
        history.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());
    }
}
