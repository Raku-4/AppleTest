/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import javax.swing.Timer;

/**
 * Swing の再描画を定期実行するための小さなヘルパーです。
 *
 * <p>初心者向けには、{@code Timer} は「一定間隔ごとに同じ処理を呼ぶ仕組み」
 * だと考えると分かりやすいです。</p>
 *
 * <p>このクラスの責務は「一定間隔で {@code repaint()} を呼ぶ」ことだけです。</p>
 */
public class SwingDemoTimer {
    private final Timer timer;

    public SwingDemoTimer(SwingBackgroundPanel panel) {
        // 16ms ごとに repaint() すると、だいたい 60fps くらいの見た目になります。
        // 1000ms / 16ms ≒ 62.5 回/秒
        this.timer = new Timer(16, _ -> panel.repaint());
        this.timer.setRepeats(true);
    }

    /**
     * タイマーを開始します。
     *
     * <p>既に動いているときは何もしません（多重 start 防止）。</p>
     */
    public void start() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    /**
     * タイマーを停止します。
     *
     * <p>既に止まっているときは何もしません。</p>
     */
    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
}

