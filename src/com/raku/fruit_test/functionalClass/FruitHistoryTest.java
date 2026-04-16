
package com.raku.fruit_test.functionalClass;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// JUnit の Assertions クラスを静的インポートすることで、
// Assertions.assertEquals ではなく、assertEquals と書けるようになります。
public class FruitHistoryTest {

    // テスト用のダミーファイル名。他のテストや本番ファイルと衝突しないように
    private static final Path TEST_PATH = Path.of("test_fruit_history");

    // =========================================================================
    // テストメソッド
    // =========================================================================

    /**
     * 【テストの目的】
     * 1. データを保存 (saveCsv) する。
     * 2. 別インスタンスで読み込み (loadCsv) する。
     * 3. 読み込んだデータの件数と内容が、保存前と完全に一致するか検証する。
     */


    @Test
    void testSaveAndLoadIntegrity() throws IOException {
        // --- STEP 1: 保存用の履歴データ (history1) を準備 ---
        FruitHistory history1 = new FruitHistory();
        history1.recordCreation("りんご", "RED", 150L);
        history1.recordCreation("ばなな", "YELLOW", 110L);
        history1.recordCreation("オリジナル", "虹", 5000L); // 特殊文字を含むデータもテスト
        history1.recordCreation("りんご", "RED", 150L); // 完全一致の重複は破棄される

        // データの件数を確認
        assertEquals(3, history1.size(), "保存前の件数が3件であることを確認。");

        // --- STEP 2: ファイルに保存する ---
        history1.saveCsv(TEST_PATH);

        // ファイルが実際に作成されたことを確認 (堅牢性のテスト)
        assertTrue(Files.exists(TEST_PATH), "CSVファイルが作成されていることを確認。");

        // --- STEP 3: 新しいインスタンス (history2) で読み込む ---
        FruitHistory history2 = new FruitHistory();
        history2.loadCsv(TEST_PATH);

        // --- STEP 4: 検証 (Assert) ---

        // 読み込んだデータの件数が、保存前の件数と一致することを確認
        assertEquals(history1.size(), history2.size(), "読み込んだ件数が保存前の件数と一致すること。");

        List<FruitRecord> bananaRecords = history2.getHistoryView("ばなな");
        assertEquals(1, bananaRecords.size(), "ばななの記録が一件であることを確認。");
        assertEquals(110L, bananaRecords.get(0).weight(), "ばななの重さが110Lであることを確認。");

        // エスケープ処理（虹）が正しく復元されているかを確認
        List<FruitRecord> customRecords = history2.getHistoryView("オリジナル");
        assertEquals("虹", customRecords.get(0).color(), "オリジナル");

        // --- STEP 5: 後処理 (Clean Up) ---
        // テストが終わったら、作成したファイルは必ず消去します
        Files.deleteIfExists(TEST_PATH);
    }

    // 【おまけ】空のファイルを読み込んだ時のテスト
    @Test
    void testLoadEmptyFIle() throws IOException {
        // 事前にファイルを作成し、中身を空にする
        Files.deleteIfExists(TEST_PATH);
        Files.createFile(TEST_PATH); // 空ファイルを作成

        FruitHistory history = new FruitHistory();
        history.loadCsv(TEST_PATH);

        // 読み込んだ後も件数が0であることを確認
        assertEquals(0, history.size(), "からのファイルを読み込んでも件数は0件であること。");

        // 後処理
        Files.deleteIfExists(TEST_PATH);
    }
}
