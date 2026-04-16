package com.raku.fruitGame.fruit.functionalClass.utility;

import com.raku.fruitGame.fruit.functionalClass.FruitRecord;

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
    private final Map<String, List<FruitRecord>> history = new LinkedHashMap<>();

    /**
     * 指定した果物名の「入れ物」を必ず用意します。
     * すでに存在する場合は何もしません。
     */
    public void ensureFruit(String fruitName) {
        history.computeIfAbsent(fruitName, key -> new ArrayList<>());
    }

    /**
     * 生成履歴を1件追加します。
     * 完全一致レコードは重複として無視します。
     */
    public void recordCreation(String fruitName, String color, long weight) {
        ensureFruit(fruitName);
        FruitRecord record = new FruitRecord(fruitName, color, weight);
        List<FruitRecord> list = history.get(fruitName);
        if (!list.contains(record)) {
            list.add(record);
        }
    }

    /**
     * 指定果物の履歴を読み取り専用で返します。
     * 呼び出し側からの add/remove を防ぐため unmodifiableList を返します。
     */
    public List<FruitRecord> getHistoryView(String fruitName) {
        List<FruitRecord> list = history.getOrDefault(fruitName, Collections.emptyList());
        return Collections.unmodifiableList(list);
    }

    /**
     * 全履歴を読み取り専用で返します。
     * Map 本体だけでなく、各 List も読み取り専用化して保護します。
     */
    public Map<String, List<FruitRecord>> viewAll() {
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
    public int size() {
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
    public void saveCsv(Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("timestamp,fruitName,color,weight");

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
                    + record.weight());
        }

        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * fruitNames 同期が不要な呼び出し用オーバーロード。
     */
    public void loadCsv(Path path) throws IOException {
        loadCsv(path, null);
    }

    /**
     * CSV を読み込み、履歴を再構築します。
     * fruitNames が渡された場合は、そこにも果物名を同期します。
     */
    public void loadCsv(Path path, List<String> fruitNames) throws IOException {
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
            try {
                weight = Long.parseLong(unCsv(cols.get(3)));
            } catch (NumberFormatException ex) {
                continue; // 数値変換不能な行はスキップ
            }

            recordCreation(fruitName, color, weight);
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
    private static String csv(String text) {
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
    private static String unCsv(String text) {
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
    private static List<String> splitCsv(String line) {
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
}
