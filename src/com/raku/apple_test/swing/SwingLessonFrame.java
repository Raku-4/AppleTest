/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
 * Swing の「画面全体の箱」を担当するクラスです。
 *
 * <p>ここでは、{@link JFrame} の作成、中央パネルの設置、上部ボタンパネルの配置、
 * 背景の更新、オーバーレイの表示など、画面の外枠に関することをまとめています。</p>
 *
 * <p>初心者向けに言うと、{@code JFrame} は「アプリの窓」、このクラスはその窓の
 * 組み立て担当です。</p>
 *
 * <p>【このクラスで覚えたいこと】</p>
 * <ul>
 *   <li>{@link JFrame} は「部品を載せる土台」</li>
 *   <li>{@link BorderLayout} は「上・中央など配置先を分ける」</li>
 *   <li>「初期化」と「表示」を分けると読みやすい</li>
 * </ul>
 */
public class SwingLessonFrame {
    private final JFrame frame;
    private final SwingBackgroundPanel backgroundPanel;
    private final SwingControlPanel controlPanel;

    public SwingLessonFrame() {
        // 1) 画面の土台を作る
        this.frame = new JFrame("AppleTest Swing Lesson");
        // 2) 背景描画担当パネルを作る
        this.backgroundPanel = new SwingBackgroundPanel();
        // 3) ボタン操作担当パネルを作る
        this.controlPanel = new SwingControlPanel(backgroundPanel, frame);
        // 4) サイズ・レイアウト・配置などをまとめて設定する
        initializeFrame();
    }

    /**
     * JFrame の基本設定をまとめて行います。
     *
     * <p>学習ポイント:</p>
     * <ul>
     *   <li>{@code setDefaultCloseOperation} で終了動作を決める</li>
     *   <li>{@code setLayout} で部品の並べ方を決める</li>
     *   <li>{@code add(..., BorderLayout.XXX)} で位置を指定して載せる</li>
     * </ul>
     */
    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 560);
        frame.setLayout(new BorderLayout());

        // 北側（上）にボタン群、中央に背景描画パネルを配置します。
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(backgroundPanel, BorderLayout.CENTER);
        // null を渡すと画面中央に配置されます。
        frame.setLocationRelativeTo(null);
    }

    /**
     * 実際にウィンドウを表示します。
     */
    public void showFrame() {
        frame.setVisible(true);
    }
}

