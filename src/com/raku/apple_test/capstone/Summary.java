package com.raku.apple_test.capstone;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public record Summary(Map<FoodCategory, Long> countByCategory,
                      Map<String, Double> averageMetricByType,
                      int csvDataLines) {

    static @NotNull Summary summarize(@NotNull List<AbstractFood> foods, Path csvPath) {
        Path dataPath = Objects.requireNonNull(csvPath, "csvPath");

        Map<FoodCategory, Long> countByCategory = new EnumMap<>(FoodCategory.class);
        for (FoodCategory category : FoodCategory.values()) {
            countByCategory.put(category, 0L);
        }

        Map<String, double[]> statsByType = new LinkedHashMap<>();
        for (AbstractFood food : foods) {
            countByCategory.merge(food.getCategory(), 1L, Long::sum);

            String labeledId = food.getType().labeledId();
            double[] stats = statsByType.computeIfAbsent(labeledId, ignored -> new double[2]);
            stats[0] += food.metric();
            stats[1] += 1.0;
        }

        Map<String, Double> averageMetricByType = new LinkedHashMap<>();
        for (Map.Entry<String, double[]> entry : statsByType.entrySet()) {
            double[] stats = entry.getValue();
            averageMetricByType.put(entry.getKey(), stats[1] == 0.0 ? 0.0 : stats[0] / stats[1]);
        }

        int csvLines = CsvSupport.countDataLines(dataPath);

        return new Summary(
                Collections.unmodifiableMap(countByCategory),
                Collections.unmodifiableMap(averageMetricByType),
                csvLines
        );
    }
}

