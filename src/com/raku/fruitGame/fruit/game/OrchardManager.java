package com.raku.fruitGame.fruit.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrchardManager {
    public static class OrchardSlot {
        public Rectangle bounds = new Rectangle();
        public FruitSpecies species;
        public FruitStage stage = FruitStage.EMPTY;

        public OrchardSlot(FruitSpecies species) {
            this.species = species;
        }
    }

    private final List<OrchardSlot> slots = new ArrayList<>();
    private static final int MAX_TREES = 36;

    public OrchardManager() {
        // 木を36本初期化（種類は適当に割り振り）
        FruitSpecies[] allSpecies = FruitSpecies.values();
        for (int i = 0; i < MAX_TREES; i++) {
            slots.add(new OrchardSlot(allSpecies[i % allSpecies.length]));
        }
    }

    public void updateLayout(int width, int height) {
        // 画面サイズに合わせて36本の木の矩形を計算
        int cols = 6;
        int rows = 6;
        int cellW = width / cols;
        int cellH = height / rows;

        for (int i = 0; i < MAX_TREES; i++) {
            int col = i % cols;
            int row = i / cols;
            // セルの中央付近に木を配置
            slots.get(i).bounds.setBounds(col * cellW + cellW / 4, row * cellH + cellH / 4, cellW / 2, cellH / 2);
        }
    }

    public List<Rectangle> getTreeBounds() {
        List<Rectangle> bounds = new ArrayList<>();
        for (OrchardSlot slot : slots) {
            bounds.add(slot.bounds);
        }
        return bounds;
    }

    public int findTouchedTree(Rectangle playerBounds) {
        for (int i = 0; i < MAX_TREES; i++) {
            if (slots.get(i).bounds.intersects(playerBounds)) {
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

    public OrchardSlot getSlot(int index) {
        if (index < 0 || index >= MAX_TREES) return null;
        return slots.get(index);
    }
}

