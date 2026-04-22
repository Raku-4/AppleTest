package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class BlackoutOverlay {

    private static final int TILE_SIZE = 16;
    private final long durationMillis;
    private final long holdMillis;
    private long startedAt;

    private float[] @Nullable [] speedMap;

    private int cols = -1;
    private int rows = -1;

    private final Random random = new Random();
    private boolean holdPhaseEntered;

    public BlackoutOverlay(long durationMillis, long holdMillis) {
        this.durationMillis = Math.max(1L, durationMillis);
        this.holdMillis = Math.max(0L, holdMillis);
        this.startedAt = 0L;
        this.holdPhaseEntered = false;
    }

    public void start() {
        startedAt = System.currentTimeMillis();
        speedMap = null;
        cols = -1;
        rows = -1;
        holdPhaseEntered = false;
    }

    /**
     * 黒保持フェーズに入った瞬間を 1 回だけ返します。
     */
    public boolean consumeEnteredHoldPhase() {
        if (holdPhaseEntered) {
            holdPhaseEntered = false;
            return true;
        }
        return false;
    }

    public void paint(@NotNull Graphics2D g2, int width, int height) {
        if (startedAt == 0L || width <= 0 || height <= 0) {
            return;
        }
        ensureSpeedMap(width, height);
        long elapsed = System.currentTimeMillis() - startedAt;
        long phase1End = durationMillis;
        long holdEnd = phase1End + holdMillis;
        long phase2End = holdEnd + durationMillis;
        if (elapsed >= phase2End) {
            return;
        }

        if (elapsed >= phase1End && elapsed < holdEnd) {
            holdPhaseEntered = true;
        }

        for (int ty = 0; ty < rows; ty++) {
            for (int tx = 0; tx < cols; tx++) {
                float speed = Objects.requireNonNull(speedMap[ty])[tx];
                float alpha01;

                if (elapsed < phase1End) {
                    alpha01 = clamp01((elapsed / (float) durationMillis) * speed);

                } else if (elapsed < holdEnd) {
                    alpha01 = 1.0f;
                } else {
                    float e2 = (elapsed - holdEnd);
                    float p = clamp01((e2 / (float) durationMillis) * speed);
                    alpha01 = 1.0f - p;
                }
                int alpha = (int) (alpha01 * 255.0f);

                int x = tx * TILE_SIZE;      // 左上 X 座標
                int y = ty * TILE_SIZE;      // 左上 Y 座標
                int w = Math.min(TILE_SIZE, width - x);
                int h = Math.min(TILE_SIZE, height - y);

                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRect(x, y, w, h);
            }
        }
    }

    public boolean isFinished() {
        if (startedAt == 0L) return true;
        long total = durationMillis + holdMillis + durationMillis;
        return (System.currentTimeMillis() - startedAt) >= total;
    }

    private void ensureSpeedMap(int width, int height) {
        int newCols = (width + TILE_SIZE - 1) / TILE_SIZE;
        int newRows = (height + TILE_SIZE - 1) / TILE_SIZE;
        if (speedMap != null && newCols == cols && newRows == rows) {
            return;
        }
        cols = newCols;
        rows = newRows;
        speedMap = new float[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Objects.requireNonNull(speedMap[y])[x] = 0.55f + random.nextFloat() * 1.20f;
            }
        }
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
