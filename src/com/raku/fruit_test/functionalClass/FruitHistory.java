/**
 * Code Made by Gemini.
 * Code Arranged by Raku.
 */
package com.raku.fruit_test.functionalClass;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FruitHistory{
    public FruitHistory() {}

    /**
     生成履歴を保持する連想配列。LinkedHashMap は挿入順維持のため、表示順が安定します。
     各果物名ごとに "FruitRecord(fruitName, color, weight)" のリストを持ちます
     */
    private final Map<String, List<FruitRecord>> history = new LinkedHashMap<>();

    private static @NotNull String recordKey(@Nullable String fruitName, @Nullable String color, long weight) {
        return (fruitName == null ? "" : fruitName) + '\u0000'
                + (color == null ? "" : color) + '\u0000'
                + weight;
    }

    private static @NotNull String recordKey(@NotNull FruitRecord record) {
        return recordKey(record.fruitName(), record.color(), record.weight());
    }

    private static boolean containsRecord(@NotNull List<FruitRecord> list, @NotNull FruitRecord record) {
        String key = recordKey(record);
        for (FruitRecord existing : list) {
            if (recordKey(existing).equals(key)) {
                return true;
            }
        }
        return false;
    }


    /**
     2. computeIfAbsent（スマートな初期化）
     これは Map 操作を劇的にスッキリさせるメソッドです。
     昔の書き方 : キーがあるか確認する、なければ new ArrayList() を作る
     今の書き方: 「なければ作る、あればそれを使う」を1行で完結させます。
     */
    public void ensureFruit(String fruitName) {
        // fruitName という箱がなければ、新しく ArrayList を作ってセットしてね
        // _ (アンダースコア) は、ここでは「引数を使わないよ」という意味です
        history.computeIfAbsent(fruitName, ignored -> new ArrayList<>());
    }

    /** 生成イベントの記録（色と重さを履歴へ） */
    public void recordCreation(String fruitName, String color, long weight) {
        ensureFruit(fruitName); // キーの存在確認
        List<FruitRecord> list = history.get(fruitName);
        FruitRecord record = new FruitRecord(fruitName, color, weight);
        if (containsRecord(list, record)) {
            return;
        }
        list.add(record);
    }


    /**

     「りんごならりんごだけを見るとき」用

     正解の動き: 「名前通りのキーがなければ、その場限りの空リストを貸してあげる（Mapは汚さない）」
     */
    public @NotNull List<FruitRecord> getHistoryView(String fruitName) {
        List<FruitRecord> list = history.getOrDefault(fruitName, Collections.emptyList());
        return Collections.unmodifiableList(list);
    }

    /** 「全履歴をまとめて見るとき」用 */
    public @NotNull Map<String, List<FruitRecord>> viewAll() {
        Map<String, List<FruitRecord>> copy = new LinkedHashMap<>();
        // 元のリスト一つひとつを「読み取り専用」に変換して、コピー用のマップに詰める
        history.forEach((k, v) -> copy.put(k, Collections.unmodifiableList(v)));
        return Collections.unmodifiableMap(copy);
    }

    /*
     1. unmodifiableMap / unmodifiableList（読み取り専用の盾）
     viewAll() や getHistoryView() で使われているこの機能は、**「中身は見せてあげるけど、勝手に追加や削除はさせないよ」**という盾を作る魔法です。

     なぜ必要か？

     もし history そのものを Main クラスに渡すと、Main クラスで history.clear() （データの完全削除）が実行できてしまいます。
     これでは FruitHistory クラスのデータ管理が台無しです。

     unmodifiableMap/List メソッドを通すことで .add() や.clear() しようとすると、実行時にエラー（UnsupportedOperationException）が発生します。
     */


    /** 「統計を出すとき」用 */
    public int size() {
        return history.values().stream().mapToInt(List::size).sum();
    }

    /**
     ① 可変長リスト lines の作成
     理由: ファイルに書き込む前に、「メモリ上で一列の文章（データの塊）」を完成させるためです。
     Files.write メソッドは、このリストの "1 要素"を「ファイルの "1 行"」として書き込んでくれます。

     ② ヘッダの明記 (timestamp,fruitName...)
     理由: CSVを Excel やメモ帳で開いたとき、「この列が何を表しているか」 がわからないと困るからです。
     プログラミングでは、この 1 行目を「ヘッダ行」と呼びます。

     ③ 2重の for ループによる参照

     外側のループ: history.values()（りんごのリスト、ばななのリスト…）を一つずつ取り出す。

     内側のループ: そのリストの中にある FruitRecord（150gの赤いりんご、等）を一つずつ取り出す。

     結果: これにより、Mapの中に隠れている全ての記録を漏れなく 1 行ずつの文字列に変換できます。

     ④ ファイルへの書き出し (Files.write)
     ポイント: 最後に StandardCharsets.UTF_8 を指定しています。
     これがないと、WindowsやMacなど環境によって「文字化け」が起きる原因になりますが、
     UTF-8 を明示することで世界中どこでも正しく「りんご」と読めるようになります。
     */
    public void saveCsv(@NotNull Path path) throws IOException {
        // ヘッダ + 各行を文字列に
        List<String> lines = new ArrayList<>();
        lines.add("timestamp,fruitName,color,weight"); // ヘッダ (Header)。頭の方にあるもの (つまり先頭にあるもの)
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME); //現在時刻を記録
        Set<String> written = new LinkedHashSet<>();
        for (List<FruitRecord> list : history.values()) {
            for (FruitRecord r : list) {
                if (!written.add(recordKey(r))) {
                    continue;
                }
                String fruit = csv(r.fruitName()); // fruitRecord からゲットした fruitName を csv に通して名前を安全に保存にする。このことを「エスケープ処理」や「サニタイズ（無害化）」と呼ぶ。
                String color = csv(r.color()); // 同上。
                String weight = Long.toString(r.weight());
                lines.add(csv(now) + "," + fruit + "," + color + "," + weight);
            }
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /** CSV から履歴を読み込む（ファイルの続きから再開する用） */
    public void loadCsv(@NotNull Path path) throws IOException {
        loadCsv(path, null);
    }

    /** CSV から履歴を読み込み、必要なら果物名一覧も同期する。 */
    public void loadCsv(@NotNull Path path, @Nullable List<String> fruitNames) throws IOException {
        /* 1. ファイルが存在しない場合は何もしない（初回起動対策） */
        if (!Files.exists(path)) return;

        /* 2. ファイルを全行読み込む */
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        history.clear();

        if (fruitNames != null) {
            fruitNames.clear();
        }

        /* 3. 1行目（ヘッダ）は飛ばして、2行目から処理する */
        for (int i = 1; i < lines.size(); i++){
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            /*
             カンマで分割し、分割して得られた要素を配列に格納する。
            */
            List<String> columns = splitCsvLine(line);

            if (columns.size() >= 4) { // 要素の数（ヘッダー通り）
                // columns[0] はtimestamp (今回は読み飛ばしてもOK)
                String name = unCsv(columns.get(1));
                String color = unCsv(columns.get(2));
                long weight = Long.parseLong(unCsv(columns.get(3)));

                // 履歴に復元する
                this.recordCreation(name, color, weight);

                if (fruitNames != null && !fruitNames.contains(name)) {
                    fruitNames.add(name);
                }
            }
        }
    }

    /**
     この csv メソッドは、一言で言うと

     CSVファイルが壊れないように、データを安全な形にラッピング（梱包）する

     ための非常に重要な処理です。
     なぜこれが必要なのか、具体例を挙げて解説しますね。

     1. なぜそのまま書き込んではいけないのか？
     CSVは「カンマ（,）」で項目を区切るルールです。しかし、もしデータの中にカンマが含まれていたらどうなるでしょうか？

     データ: りんご

     色: 赤,ピンク（←ここにカンマがある！）

     期待する1行: りんご,"赤,青",150

     失敗例（そのまま書く）: りんご,赤,青,150

     失敗例だと、コンピュータは「赤」と「青」が別々の列だと勘違いしてしまい、データの列がズレてしまいます。
     これを防ぐために、**「データの前後をダブルクォート（"）で囲む」**というルールがあるのです。
     */
    private static @NotNull String csv(@Nullable String s) {
        // ① データが空っぽ（null）なら、空の文字として扱う
        if (s == null) return "";

        // ② 【重要】データの中にすでに " が入っていた場合の処理
        // CSVのルールでは、データ内の " は "" （2つ連続）に書き換える決まりがあります
        String escaped = s.replace("\"", "\"\"");
        // 例："赤"、青" -> ""赤""、青""、


        // ③ データの最初と最後を " でサンドイッチする
        // これにより、中にカンマが入っていても「ここからここまでが一つの項目ですよ」と証明できます。
        return "\"" + escaped + "\"";
        // 例：お祝い用"高級"りんご -> "お祝い用""高級""りんご"
        // そのままダブルクオートでデータを囲んでしまうと、 "お祝い用"鋼球"りんご"となってしまい、csv ファイルでは「高級」がデータの外側にあるかのように見えてしまう。
    }


    /** csvメソッドの逆：囲まれたダブルクオートを取り除く。
     * 🔍 ここがポイント！
     * replaceFirst("^\"", "") : 文字列の先頭にある " だけを消します。
     *<p>
     * replaceFirst("\"$", "") : 文字列の最後にある " だけを消します。
     * <p>
     * replace("\"\"", "") : 文字列の中にある "" を " に変換する。
     * */
    private @NotNull String unCsv(@Nullable String s) {
        if (s == null || s.isEmpty()) return "";

        // 前後の " を取り除き、"" を " に戻す。
        return s.replaceFirst("^\"", "")
                .replaceFirst("\"$", "")
                .replace("\"\"", "\"");
    }

    private static @NotNull List<String> splitCsvLine(@NotNull String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) {
                if (ch == '"') {
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
                columns.add(current.toString());
                current.setLength(0);
            } else if (ch == '"') {
                quoted = true;
            } else {
                current.append(ch);
            }
        }

        columns.add(current.toString());
        return columns;
    }
}