package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 肥料の待機状態(treeIdごとの経過秒)を保存します。
 */
public final class FertilizerCsv {
    private FertilizerCsv() {
    }

    public static void save(@NotNull Path path, @NotNull Map<Integer, Long> elapsedByTree) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("treeId,elapsedSeconds");
        for (Map.Entry<Integer, Long> entry : elapsedByTree.entrySet()) {
            lines.add(entry.getKey() + "," + entry.getValue());
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static @NotNull Map<Integer, Long> load(@NotNull Path path) throws IOException {
        Map<Integer, Long> out = new LinkedHashMap<>();
        if (!Files.exists(path)) {
            return out;
        }
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] cols = line.split(",", -1);
            if (cols.length < 2) {
                continue;
            }
            try {
                int treeId = Integer.parseInt(cols[0].trim());
                long elapsed = Long.parseLong(cols[1].trim());
                out.put(treeId, Math.max(0L, elapsed));
            } catch (NumberFormatException ignored) {
                // skip broken line
            }
        }
        return out;
    }
}

