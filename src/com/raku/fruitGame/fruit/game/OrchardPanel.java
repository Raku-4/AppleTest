package com.raku.fruitGame.fruit.game;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * 果樹園風の背景を描画するパネル。
 *
 * <p>固定画像ではなく、paintComponent で毎フレーム描画しています。</p>
 */
public class OrchardPanel extends JPanel {
    /**
     * 画面遷移のエフェクトを重ねるためのオーバーレイ。
     * null のときは通常描画のみ。
     */
    private TransitionOverlay overlay;

    public OrchardPanel() {
        // 空の色を背景色として設定
        setBackground(new Color(210, 245, 255));

        // 約60fps (1000 / 16) で repaint を呼び、アニメーション表示を滑らかにします。
        Timer timer = new Timer(16, e -> repaint());
        timer.start();
    }

    /**
     * 外部からオーバーレイ開始を要求するAPI。
     */
    public void startTransition(TransitionOverlay overlay) {
        this.overlay = overlay;
        this.overlay.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Graphics のコピーを使うことで、このメソッド内の設定変更を局所化します。
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 地面
        g2.setColor(new Color(145, 220, 145));
        g2.fillRect(0, h / 2, w, h / 2);

        // 太陽
        g2.setColor(new Color(255, 230, 90));
        g2.fillOval(w - 120, 20, 80, 80);

        // 木の幹
        g2.setColor(new Color(130, 90, 60));
        g2.fillRect(120, h / 2 - 60, 30, 120);

        // 葉
        g2.setColor(new Color(60, 160, 70));
        g2.fillOval(70, h / 2 - 130, 130, 100);

        // 実 (りんごを想定)
        g2.setColor(new Color(220, 40, 40));
        g2.fillOval(100, h / 2 - 95, 18, 18);
        g2.fillOval(145, h / 2 - 85, 18, 18);

        // オーバーレイが有効なら最後に重ね描き
        if (overlay != null && !overlay.isFinished()) {
            overlay.paint(g2, w, h);
        }

        g2.dispose();
    }
}
