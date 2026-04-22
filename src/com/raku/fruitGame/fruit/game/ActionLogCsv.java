package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * アイテム使用・植樹・成長・収穫のイベントを都度別名CSVへ保存します。
 */
public final class ActionLogCsv {
    private static final DateTimeFormatter NAME_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ISO_DATE_TIME;

    private ActionLogCsv() {
    }

    public static void logEvent(String event, String itemName, String detail) {
        LocalDateTime now = LocalDateTime.now();
        String suffix = now.format(NAME_FMT);
        Path dir = Path.of("action_logs");
        Path file = dir.resolve("action_" + suffix + ".csv");

        try {
            Files.createDirectories(dir);
            List<String> lines = List.of(
                    "timestamp,event,itemName,detail",
                    csv(now.format(TS_FMT)) + "," + csv(event) + "," + csv(itemName) + "," + csv(detail)
            );
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // ログ保存失敗でもゲーム進行は継続する
        }
    }

    private static @NotNull String csv(@Nullable String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}

