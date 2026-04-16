package com.raku.fruitGame.fruit.game;

import java.awt.Graphics2D;

/**
 * 画面遷移エフェクトの最小契約を定義するインターフェース。
 */
public interface TransitionOverlay {
    /**
     * 遷移開始時に一度呼ばれる想定の初期化メソッド。
     */
    void start();

    /**
     * オーバーレイを描画します。
     *
     * @param g2 描画先
     * @param width 描画領域の幅
     * @param height 描画領域の高さ
     */
    void paint(Graphics2D g2, int width, int height);

    /**
     * 遷移が完了したかどうかを返します。
     */
    boolean isFinished();
}
