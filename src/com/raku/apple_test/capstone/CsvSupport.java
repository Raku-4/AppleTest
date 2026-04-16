package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 食べ物の生成履歴を保持し、CSVへ保存・復元するための補助クラス。
 *<p>
 * `CsvSupport` は「生の CSV 文字列をいじる道具」ではなく、
 * `AbstractFood` の履歴を安全に扱うための保存箱として動く。
 */
public final class CsvSupport {
    CsvSupport() {}

    private static final Map<String, List<AbstractFood>> history = new LinkedHashMap<>();

    private static @NotNull String foodKey(@Nullable String foodName, @Nullable String labeledId, @Nullable Zone zone) {
        return (foodName == null ? "" : foodName) + '\u0000'
                + (labeledId == null ? "" : labeledId) + '\u0000'
                + (zone == null ? "" : zone.name());
    }

    private static @NotNull String foodKey(@NotNull AbstractFood food) {
        return foodKey(food.getFoodName(), food.getType().labeledId(), food.getZone());
    }

    private static boolean containsFood(@NotNull String foodName, @NotNull String labeledId, @NotNull Zone zone) {
        List<AbstractFood> list = history.get(foodName);
        if (list == null) {
            return false;
        }

        String key = foodKey(foodName, labeledId, zone);
        for (AbstractFood existing : list) {
            if (foodKey(existing).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static void ensureFood(String foodName) {
        history.computeIfAbsent(foodName, ignored -> new ArrayList<>());
    }

    public static void recordFood(@NotNull AbstractFood food) {
        ensureFood(food.getFoodName());
        String foodName = food.getFoodName();
        String labeledId = food.getType().labeledId();
        Zone zone = food.getZone();
        if (containsFood(foodName, labeledId, zone)) {
            return;
        }

        history.get(foodName).add(new AbstractFood(food.getType(), food.getZone(), food.getFoodName()) {
            @Override
            public double metric() {
                return food.metric();
            }

            @Override
            public String describe() {
                return food.describe();
            }

            @Override
            public String getName() {
                return food.getName();
            }
        });
    }

    public static @NotNull @UnmodifiableView List<AbstractFood> getFoods(String foodName) {
        List<AbstractFood> list = history.getOrDefault(foodName, Collections.emptyList());
        return Collections.unmodifiableList(list);
    }

    public static @NotNull @UnmodifiableView Map<String, List<AbstractFood>> getFoods() {
        Map<String, List<AbstractFood>> copy = new LinkedHashMap<>();
        history.forEach((foodName, list) -> copy.put(foodName, Collections.unmodifiableList(list)));
        return Collections.unmodifiableMap(copy);
    }

    public static int size() {return history.values().stream().mapToInt(List::size).sum();}

    public static void saveCSV(@NotNull Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("timestamp,foodName,labeledId,zone");
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        Set<String> written = new LinkedHashSet<>();
        for (List<AbstractFood> list : history.values()) {
            for (AbstractFood abstractFood : list) {
                String foodName = csv(abstractFood.getFoodName());
                String labeledId = csv(abstractFood.getType().labeledId());
                String zone = csv(abstractFood.getZone().name());
                String key = foodKey(abstractFood);
                if (!written.add(key)) {
                    continue;
                }
                lines.add(csv(now) + "," + foodName + "," + labeledId + "," + zone);
            }
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static void SaveCSV(@NotNull Path path) throws IOException {
        saveCSV(path);
    }

    public static void loadCSV(@NotNull Path path) throws IOException {
        if (!Files.exists(path)) return;

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        history.clear();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            List<String> columns = splitCsvLine(line);
            if (columns.size() < 4) {
                continue;
            }

            String foodName = unCsv(columns.get(1));
            String labeledId = unCsv(columns.get(2));
            Zone zone = resolveZone(unCsv(columns.get(3)));

            AbstractFood food = switch (foodName) {
                case "apple" -> Catalog.APPLE.create(zone, foodName);
                case "banana" -> Catalog.BANANA.create(zone, foodName);
                case "broccoli" -> Catalog.BROCCOLI.create(zone, foodName);
                case "onion" -> Catalog.ONION.create(zone, foodName);
                default -> throw new IllegalStateException("Unexpected value: " + foodName);
            };

            if (!labeledId.isEmpty() && !food.getType().labeledId().equals(labeledId)) {
                throw new IllegalStateException("CSV labeledId mismatch: " + labeledId);
            }

            recordFood(food);
        }
    }

    public static int countDataLines(@Nullable Path path) {
        if (path == null || !Files.exists(path)) {
            return 0;
        }

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return 0;
            }

            int count = 0;
            for (int i = 1; i < lines.size(); i++) {
                if (!lines.get(i).trim().isEmpty()) {
                    count++;
                }
            }
            return count;
        } catch (IOException e) {
            return 0;
        }
    }

    private static @NotNull String csv(@Nullable String string) {
        if (string == null) return  "";
        String escaped = string.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private static Zone resolveZone(@Nullable String zoneName) {
        if (zoneName == null || zoneName.isEmpty()) {
            return MainCapstone.field;
        }
        return new Zone(zoneName);
    }

    private static @NotNull List<String> splitCsvLine(@NotNull String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (quoted) { // "onion",""pizza"""
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"'); // 名前として追加する。
                        i++;
                    } else {
                        quoted = false; // 名前の終点を検知する。
                    }
                } else {
                    current.append(ch);
                }
            } else if (ch == ',') {
                columns.add(current.toString());
                current.setLength(0);
            } else if (ch == '"') {
                quoted = true; //名前の始点を検知する。
            } else {
                current.append(ch);
            }
        }

        columns.add(current.toString());
        return columns;
    }

    private static @NotNull String unCsv(@Nullable String string) {
        if (string == null || string.isEmpty()) return "";

        return string.replaceFirst("^\"", "")
                     .replaceFirst("\"$", "")
                     .replace("\"\"","\"");
    }

    public static void want2save(@NotNull Scanner scanner, Path path) {
        System.out.println("Do you want to save the history to CSV? (y/n)");
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                try {
                    SaveCSV(path);
                    System.out.println("Saved the history to " + path + " successfully.");
                    return;
                } catch (IOException e) {
                    System.out.println("Error saving to CSV: " + e.getMessage());
                    return;
                }

            } else if (input.equals("n") || input.equals("no")) {
                return;
            } else {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }
    }
}