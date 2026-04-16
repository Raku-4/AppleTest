package com.raku.fruitGame.fruit.functionalClass;

import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FruitHistory の保存・復元整合性を確認するテスト。
 */
class FruitHistoryTest {
    /** 本番データと衝突しない一時ファイル名 */
    private static final Path TEST_PATH = Path.of("test_fruit_game_history.csv");

    @Test
    void saveAndLoadKeepsData() throws IOException {
        // 1) 保存元履歴を作る
        FruitHistory h1 = new FruitHistory();
        h1.recordCreation("りんご", "RED", 150L);
        h1.recordCreation("ばなな", "YELLOW", 120L);
        h1.recordCreation("りんご", "RED", 150L); // 重複行は無視される想定

        assertEquals(2, h1.size());

        // 2) CSV 保存できることを確認
        h1.saveCsv(TEST_PATH);
        assertTrue(Files.exists(TEST_PATH));

        // 3) 新インスタンスで読み込み、内容一致を検証
        FruitHistory h2 = new FruitHistory();
        h2.loadCsv(TEST_PATH);

        assertEquals(2, h2.size());
        assertEquals(1, h2.getHistoryView("りんご").size());
        assertEquals(150L, h2.getHistoryView("りんご").get(0).weight());

        // 4) 後始末
        Files.deleteIfExists(TEST_PATH);
    }
}
