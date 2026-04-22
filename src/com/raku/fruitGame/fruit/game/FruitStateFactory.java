package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.fruit.functionalClass.FruitRecord;
import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import org.jetbrains.annotations.NotNull;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * FruitHistory から GUI 用 FruitState 一覧を作る工場です。
 */
public final class FruitStateFactory {
    private FruitStateFactory() {
    }

    public static @NotNull List<FruitState> createInventoryStates() {
        List<FruitState> states = new ArrayList<>();
        Map<String, List<FruitRecord>> all = FruitHistory.viewAll();
        for (Map.Entry<String, List<FruitRecord>> entry : all.entrySet()) {
            String fruitName = entry.getKey();
            List<FruitRecord> records = entry.getValue();
            if (records.isEmpty()) {
                continue;
            }
            List<FruitRecord> bagRecords = new ArrayList<>();
            for (FruitRecord record : records) {
                if (record.treeId() < 0) {
                    bagRecords.add(record);
                }
            }
            if (bagRecords.isEmpty()) {
                continue;
            }
            FruitRecord latest = bagRecords.get(bagRecords.size() - 1);
            int quantity = bagRecords.size();
            Image icon = AssetImageLoader.load(resolveFruitIconPath(fruitName));
            String description = latest.color() + " / " + latest.taste() + " / " + latest.maturity();
            states.add(new FruitState(fruitName, latest.color(), latest.weight(), quantity, description, icon));
        }
        return states;
    }

    public static Image resolveIconForName(@NotNull String fruitName) {
        return AssetImageLoader.load(resolveFruitIconPath(fruitName));
    }

    private static @NotNull String resolveFruitIconPath(@NotNull String fruitName) {
        String key = fruitName.toLowerCase(Locale.ROOT);
        if (key.contains("肥料") || key.contains("fertilizer")) {
            return "assets/fruitGame/textures/misc/fertilizer.png";
        }
        if (key.startsWith("perfect_ripe_")) {
            if (key.contains("banana") || key.contains("ばなな")) {
                return "assets/fruitGame/textures/fruits/perfect_ripe_banana.png";
            }
            if (key.contains("orange") || key.contains("おれんじ") || key.contains("オレンジ")) {
                return "assets/fruitGame/textures/fruits/perfect_ripe_orange.png";
            }
        }
        if (key.startsWith("unripe_")) {
            if (key.contains("banana") || key.contains("ばなな")) {
                return "assets/fruitGame/textures/fruits/unripe_banana.png";
            }
            if (key.contains("orange") || key.contains("おれんじ") || key.contains("オレンジ")) {
                return "assets/fruitGame/textures/fruits/unripe_orange.png";
            }
            if (key.contains("grape") || key.contains("ぶどう") || key.contains("ブドウ")) {
                return "assets/fruitGame/textures/fruits/unripe_grape.png";
            }
            return "assets/fruitGame/textures/fruits/unripe_apple.png";
        }
        if (key.contains("banana") || key.contains("ばなな")) {
            return "assets/fruitGame/textures/fruits/ripe_banana.png";
        }
        if (key.contains("orange") || key.contains("おれんじ") || key.contains("オレンジ")) {
            return "assets/fruitGame/textures/fruits/ripe_orange.png";
        }
        if (key.contains("grape") || key.contains("ぶどう") || key.contains("ブドウ")) {
            return "assets/fruitGame/textures/fruits/ripe_grape.png";
        }
        return "assets/fruitGame/textures/fruits/ripe_apple.png";
    }
}

