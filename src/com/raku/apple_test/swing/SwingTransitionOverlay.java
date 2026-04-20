/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/**
 * 画面を 16×16 のタイルに分けて、
 * 各タイルがばらばらな速度で黒くなってから、また白へ戻る演出です。
 *
 * <p>初心者向けに言うと、「画面に黒い砂がザザッと広がって、
 * その後に引いていく」ような見た目です。</p>
 *
 * <p>【内部の時間フェーズ】</p>
 * <ol>
 *   <li>白 → 黒（durationMillis）</li>
 *   <li>黒のまま保持（holdMillis）</li>
 *   <li>黒 → 白（durationMillis）</li>
 * </ol>
 */
public class SwingTransitionOverlay {
    // 16x16 ピクセル単位で演出を分割する。
    private static final int TILE_SIZE = 16;

    // 変化フェーズの長さ（白→黒、黒→白）
    private final long durationMillis;
    // 真っ黒保持フェーズの長さ
    private final long holdMillis;
    private final Random random = new Random();
    // タイルごとの速度倍率を保持するマップ
    private float[][] speedMap;
    private int cols = -1;
    private int rows = -1;
    // start() 呼び出し時刻（0 は未開始）
    private long startedAt;

    public SwingTransitionOverlay(long durationMillis, long holdMillis) {
        this.durationMillis = Math.max(1L, durationMillis);
        this.holdMillis = Math.max(0L, holdMillis);
        this.startedAt = 0L;
    }

    /**
     * アニメーション開始時刻を記録します。
     *
     * <p>ここで時刻を保存しておくと、{@link #paint(Graphics2D, int, int)} で
     * 「開始から何ミリ秒経ったか」を計算できます。</p>
     */
    public void start() {
        startedAt = System.currentTimeMillis();
        speedMap = null;
        cols = -1;
        rows = -1;
    }

    /**
     * 毎フレーム呼ばれて、今の進行度に合わせた黒タイルを描きます。
     */
    public void paint(Graphics2D g2, int width, int height) {
        if (startedAt == 0L || width <= 0 || height <= 0) {
            return;
        }

        // 画面サイズに対応した speedMap を準備
        ensureSpeedMap(width, height);

        // 開始からの経過時間を計算
        long elapsed = System.currentTimeMillis() - startedAt;
        long phase1End = durationMillis;
        long holdEnd = phase1End + holdMillis;
        long phase2End = holdEnd + durationMillis;

        if (elapsed >= phase2End) {
            return;
        }

        for (int ty = 0; ty < rows; ty++) {
            for (int tx = 0; tx < cols; tx++) {
                float speed = speedMap[ty][tx];
                float alpha01;

                if (elapsed < phase1End) {
                    // フェーズ1: 白→黒（0.0 から 1.0 へ）
                    alpha01 = clamp01((elapsed / (float) durationMillis) * speed);
                } else if (elapsed < holdEnd) {
                    // フェーズ2: 黒を維持
                    alpha01 = 1.0f;
                } else {
                    // フェーズ3: 黒→白（1.0 から 0.0 へ）
                    float e2 = elapsed - holdEnd;
                    float p = clamp01((e2 / (float) durationMillis) * speed);
                    alpha01 = 1.0f - p;
                }

                int alpha = (int) (alpha01 * 255.0f);
                int x = tx * TILE_SIZE;
                int y = ty * TILE_SIZE;
                int w = Math.min(TILE_SIZE, width - x);
                int h = Math.min(TILE_SIZE, height - y);

                // alpha を使って、半透明の黒タイルを重ねる
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRect(x, y, w, h);
            }
        }
    }

    /**
     * 演出が完全に終わったかどうかを返します。
     */
    public boolean isFinished() {
        if (startedAt == 0L) {
            return true;
        }
        long total = durationMillis + holdMillis + durationMillis;
        return (System.currentTimeMillis() - startedAt) >= total;
    }

    private void ensureSpeedMap(int width, int height) {
        // 切り上げ除算で必要タイル数を算出
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
                // 0.55x ～ 1.75x で速度をばらつかせる
                speedMap[y][x] = 0.55f + random.nextFloat() * 1.20f;
            }
        }
    }

    private static float clamp01(float v) {
        // 計算値を 0.0～1.0 に収める。アルファ値の破綻防止。
        return Math.max(0f, Math.min(1f, v));
    }
}

