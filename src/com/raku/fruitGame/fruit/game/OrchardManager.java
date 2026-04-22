package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.fruit.functionalClass.FruitRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrchardManager {
    private static final int[][] TREE_RANGES = {
            {6, 4, 13, 19}, {14, 4, 21, 19}, {22, 4, 29, 19},
            {6, 18, 13, 33}, {14, 18, 21, 33}, {22, 18, 29, 33},
            {5, 40, 14, 54}, {14, 40, 23, 54}, {23, 40, 32, 54},
            {5, 55, 14, 69}, {14, 55, 23, 69}, {23, 55, 32, 69},
            {5, 71, 14, 86}, {14, 71, 23, 86}, {23, 71, 32, 86},
            {50, 3, 59, 17}, {61, 3, 70, 17}, {71, 3, 80, 17}, {81, 3, 90, 17},
            {71, 25, 81, 39}, {82, 25, 92, 39},
            {36, 15, 46, 30}, {45, 25, 55, 40}, {56, 25, 66, 40}, {50, 41, 60, 56},
            {41, 56, 51, 71}, {58, 56, 68, 71},
            {71, 47, 81, 58}, {83, 47, 93, 58}, {71, 60, 81, 71}, {83, 60, 93, 71},
            {71, 73, 81, 84}, {83, 73, 93, 84},
            {38, 80, 47, 95}, {47, 80, 56, 95}, {56, 80, 65, 95}
    };

    public enum InteractionResult {
        NONE,
        PLANTED,
        GREW,
        HARVESTED,
        FAILED
    }

    public static class OrchardSlot {
        public @NotNull Rectangle bounds = new Rectangle();
        public FruitSpecies species;
        public @NotNull FruitStage stage = FruitStage.EMPTY;
        public long plantedAtMillis;
        public long fertilizedAtMillis;
        public int fruitCount;

        public OrchardSlot(FruitSpecies species) {
            this.species = species;
        }

        public long elapsedSeconds(long nowMillis) {
            if (stage == FruitStage.EMPTY || plantedAtMillis <= 0L) {
                return 0L;
            }
            return Math.max(0L, (nowMillis - plantedAtMillis) / 1000L);
        }

        public long fertilizerElapsedSeconds(long nowMillis) {
            if (fertilizedAtMillis <= 0L) {
                return 0L;
            }
            return Math.max(0L, (nowMillis - fertilizedAtMillis) / 1000L);
        }
    }

    private final List<OrchardSlot> slots = new ArrayList<>();
    private static final int MAX_TREES = 36;

    public OrchardManager() {
        for (int i = 0; i < MAX_TREES; i++) {
            slots.add(new OrchardSlot(speciesForTree(i)));
        }
    }

    public void updateLayout(int width, int height) {
        for (int i = 0; i < MAX_TREES; i++) {
            int[] range = TREE_RANGES[i];
            int x1 = width * range[0] / 100;
            int y1 = height * range[1] / 100;
            int x2 = width * range[2] / 100;
            int y2 = height * range[3] / 100;
            int fullW = Math.max(1, x2 - x1);
            int fullH = Math.max(1, y2 - y1);
            int clippedH = Math.max(1, (int) Math.round(fullH * 0.70));
            slots.get(i).bounds.setBounds(x1, y1, fullW, clippedH);
        }
    }

    public @NotNull List<Rectangle> getTreeBounds() {
        List<Rectangle> bounds = new ArrayList<>();
        for (OrchardSlot slot : slots) {
            bounds.add(slot.bounds);
        }
        return bounds;
    }

    public @NotNull List<OrchardSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public int findTouchedTree(@NotNull Rectangle playerBounds) {
        for (int i = 0; i < MAX_TREES; i++) {
            Rectangle touch = new Rectangle(slots.get(i).bounds);
            touch.grow(6, 6);
            if (touch.intersects(playerBounds)) {
                return i;
            }
        }
        return -1;
    }

    public void tryInteractWithTree(int index) {
        if (index < 0 || index >= MAX_TREES) return;
        OrchardSlot slot = slots.get(index);

        // 状態遷移のサンプル
        if (slot.stage == FruitStage.EMPTY) {
            slot.stage = FruitStage.UNRIPE; // 植える
        } else if (slot.stage == FruitStage.UNRIPE) {
            slot.stage = FruitStage.RIPE;   // 育つ
        } else if (slot.stage == FruitStage.RIPE) {
            slot.stage = FruitStage.EMPTY;  // 収��する
        }
    }

    public @NotNull InteractionResult tryPlant(int index, @Nullable FruitState heldFruit) {
        if (index < 0 || index >= MAX_TREES) {
            return InteractionResult.NONE;
        }
        OrchardSlot slot = slots.get(index);
        if (slot.stage != FruitStage.EMPTY) {
            return InteractionResult.FAILED;
        }

        if (heldFruit != null) {
            FruitSpecies heldSpecies = FruitSpecies.fromName(heldFruit.name());
            if (heldSpecies != slot.species) {
                return InteractionResult.FAILED;
            }
        }

        slot.stage = FruitStage.UNRIPE;
        slot.plantedAtMillis = System.currentTimeMillis();
        slot.fertilizedAtMillis = 0L;
        slot.fruitCount = 1;
        return InteractionResult.PLANTED;
    }

    public @NotNull InteractionResult tryGrowByHold(int index) {
        if (index < 0 || index >= MAX_TREES) {
            return InteractionResult.NONE;
        }
        OrchardSlot slot = slots.get(index);
        if (slot.stage != FruitStage.UNRIPE) {
            return InteractionResult.FAILED;
        }
        slot.stage = FruitStage.RIPE;
        slot.fruitCount = Math.max(1, slot.fruitCount);
        return InteractionResult.GREW;
    }

    public @Nullable FruitState tryHarvest(int index) {
        if (index < 0 || index >= MAX_TREES) {
            return null;
        }
        OrchardSlot slot = slots.get(index);
        if (slot.stage != FruitStage.RIPE && slot.stage != FruitStage.PERFECT) {
            return null;
        }

        String name = toFruitName(slot.species, slot.stage);
        String taste = slot.stage == FruitStage.PERFECT ? "最高においしい" : "おいしい";
        String maturity = slot.stage == FruitStage.PERFECT ? "完熟" : "熟した";
        FruitState result = new FruitState(
                name,
                slot.species.getRipeColor(),
                1L,
                1,
                taste + " / " + maturity,
                AssetImageLoader.load(slot.species.iconPathFor(slot.stage))
        );

        slot.fruitCount = Math.max(0, slot.fruitCount - 1);
        if (slot.fruitCount == 0) {
            slot.stage = FruitStage.EMPTY;
            slot.plantedAtMillis = 0L;
            slot.fertilizedAtMillis = 0L;
        }
        return result;
    }

    public boolean tryFertilize(int index) {
        if (index < 0 || index >= MAX_TREES) {
            return false;
        }
        OrchardSlot slot = slots.get(index);
        if (slot.stage != FruitStage.EMPTY) {
            return false;
        }
        slot.fertilizedAtMillis = System.currentTimeMillis();
        slot.fruitCount = 0;
        return true;
    }

    public boolean updateGrowth(long nowMillis) {
        boolean changed = false;
        for (OrchardSlot slot : slots) {
            if (slot.stage == FruitStage.EMPTY && slot.fertilizedAtMillis > 0L && slot.fertilizerElapsedSeconds(nowMillis) >= 120L) {
                slot.stage = FruitStage.UNRIPE;
                slot.plantedAtMillis = nowMillis;
                slot.fertilizedAtMillis = 0L;
                slot.fruitCount = 2;
                changed = true;
                continue;
            }
            if (slot.stage == FruitStage.UNRIPE && slot.elapsedSeconds(nowMillis) >= 180L) {
                slot.stage = FruitStage.RIPE;
                slot.fruitCount = Math.max(1, slot.fruitCount);
                changed = true;
            } else if (slot.stage == FruitStage.RIPE && slot.species.supportsPerfect() && slot.elapsedSeconds(nowMillis) >= 300L) {
                slot.stage = FruitStage.PERFECT;
                slot.fruitCount = Math.max(1, slot.fruitCount);
                changed = true;
            }
        }
        return changed;
    }

    public void restoreFromHistory(@NotNull Map<String, List<FruitRecord>> all) {
        clearAllPlants();
        long now = System.currentTimeMillis();
        for (List<FruitRecord> records : all.values()) {
            for (FruitRecord record : records) {
                if (record.treeId() < 0 || record.treeId() >= MAX_TREES) {
                    continue;
                }
                OrchardSlot slot = slots.get(record.treeId());
                slot.stage = maturityToStage(record.maturity().toString(), record.fruitName());
                slot.plantedAtMillis = now - Math.max(0L, record.elapsedSeconds()) * 1000L;
                slot.fruitCount = Math.max(1, (int) record.weight());
            }
        }
    }

    public void clearAllPlants() {
        for (OrchardSlot slot : slots) {
            slot.stage = FruitStage.EMPTY;
            slot.plantedAtMillis = 0L;
            slot.fertilizedAtMillis = 0L;
            slot.fruitCount = 0;
        }
    }

    public @NotNull Map<Integer, Long> snapshotFertilizerElapsed(long nowMillis) {
        Map<Integer, Long> out = new LinkedHashMap<>();
        for (int i = 0; i < slots.size(); i++) {
            OrchardSlot slot = slots.get(i);
            if (slot.stage == FruitStage.EMPTY && slot.fertilizedAtMillis > 0L) {
                out.put(i, slot.fertilizerElapsedSeconds(nowMillis));
            }
        }
        return out;
    }

    public void restoreFertilizerElapsed(@NotNull Map<Integer, Long> elapsedByTree, long nowMillis) {
        for (Map.Entry<Integer, Long> entry : elapsedByTree.entrySet()) {
            int index = entry.getKey();
            if (index < 0 || index >= slots.size()) {
                continue;
            }
            OrchardSlot slot = slots.get(index);
            if (slot.stage != FruitStage.EMPTY) {
                continue;
            }
            slot.fertilizedAtMillis = nowMillis - Math.max(0L, entry.getValue()) * 1000L;
        }
    }

    public static String toFruitName(@NotNull FruitSpecies species, @NotNull FruitStage stage) {
        return switch (stage) {
            case UNRIPE -> "unripe_" + species.getBaseName();
            case RIPE -> "ripe_" + species.getBaseName();
            case PERFECT -> species.supportsPerfect()
                    ? "perfect_ripe_" + species.getBaseName()
                    : "ripe_" + species.getBaseName();
            case EMPTY -> species.getBaseName();
        };
    }

    private static @NotNull FruitSpecies speciesForTree(int index) {
        int id = index + 1;
        if (id <= 6) {
            return FruitSpecies.BANANA;
        }
        if (id <= 12) {
            return FruitSpecies.APPLE;
        }
        if (id <= 15) {
            return FruitSpecies.ORANGE;
        }
        if (id <= 27) {
            return FruitSpecies.APPLE;
        }
        if (id <= 33) {
            return FruitSpecies.GRAPE;
        }
        return FruitSpecies.APPLE;
    }

    private static @NotNull FruitStage maturityToStage(@Nullable String maturity, @Nullable String fruitName) {
        if (maturity != null && maturity.contains("完熟")) {
            return FruitStage.PERFECT;
        }
        if (maturity != null && maturity.contains("熟")) {
            if (maturity.contains("未熟")) {
                return FruitStage.UNRIPE;
            }
            return FruitStage.RIPE;
        }

        String key = fruitName == null ? "" : fruitName.toLowerCase();
        if (key.startsWith("unripe_")) {
            return FruitStage.UNRIPE;
        }
        if (key.startsWith("perfect_ripe_")) {
            return FruitStage.PERFECT;
        }
        if (key.startsWith("ripe_")) {
            return FruitStage.RIPE;
        }
        return FruitStage.EMPTY;
    }

    public @Nullable OrchardSlot getSlot(int index) {
        if (index < 0 || index >= MAX_TREES) return null;
        return slots.get(index);
    }
}

