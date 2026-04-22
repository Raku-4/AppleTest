/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * 背景画像とオーバーレイを描くパネルです。
 *
 * <p>ここで学べること:</p>
 * <ul>
 *   <li>{@link JPanel} は「中身を描く場所」だということ</li>
 *   <li>{@code paintComponent} が Swing によって呼ばれること</li>
 *   <li>画像を描く順番が大事なこと</li>
 *   <li>オーバーレイを重ねると演出が作れること</li>
 * </ul>
 */
public class SwingBackgroundPanel extends JPanel {
    // 学習用に使う背景画像のパスです。
    private static final String MAIN_MENU_BG = "assets/fruitGame/textures/main_menu/main_menu.png";

    // 読み込んだ背景画像。null のときは単色背景へフォールバックします。
    private final Image backgroundImage;
    // オーバーレイ描画中だけ動かすタイマー。
    private final @NotNull SwingDemoTimer demoTimer;
    // 黒幕演出の描画オブジェクト。演出していないときは null。
    private @Nullable SwingTransitionOverlay overlay;

    public SwingBackgroundPanel() {
        // 背景をこのパネル側で全面描画するため不透明にします。
        setOpaque(true);
        // 画像が読めないときの代替色です。
        setBackground(new Color(210, 245, 255));
        this.backgroundImage = SwingAssetLoader.load(MAIN_MENU_BG);
        this.demoTimer = new SwingDemoTimer(this);
    }

    /**
     * ブラックアウト演出を開始します。
     */
    public void startTransition() {
        // 既存オーバーレイがあっても、新規演出を優先して上書きします。
        this.overlay = new SwingTransitionOverlay(600L, 2000L);
        this.overlay.start();
        // アニメーション中だけ定期再描画を有効にする。
        this.demoTimer.start();
        // 開始直後の1フレームをすぐ表示。
        repaint();
    }

    /**
     * タイマーを止めるときに使います。
     */
    public void stopAnimation() {
        this.demoTimer.stop();
    }

    /**
     * パネルの見た目を描くためのメソッドです。
     *
     * <p>【とても重要】</p>
     * <ul>
     *   <li>このメソッドは「自分で直接呼ぶ」ものではありません</li>
     *   <li>Swing が必要なタイミングで自動的に呼びます</li>
     *   <li>描き直したいときは {@code repaint()} を呼んで依頼します</li>
     * </ul>
     *
     * <p>【呼ばれる主なタイミング】</p>
     * <ul>
     *   <li>ウィンドウを初めて表示したとき</li>
     *   <li>ウィンドウサイズを変えたとき</li>
     *   <li>別ウィンドウで隠れていた部分が再表示されたとき</li>
     *   <li>{@code repaint()} を呼んだとき</li>
     * </ul>
     *
     * <p>【このメソッド内の基本ルール】</p>
     * <ol>
     *   <li>{@code super.paintComponent(g)} を最初に呼ぶ（前フレームの残像を消す）</li>
     *   <li>背景を描く</li>
     *   <li>前景（今回はオーバーレイ）を重ねる</li>
     *   <li>最後に作った Graphics を破棄する</li>
     * </ol>
     */
    @Override
    protected void paintComponent(@NotNull Graphics g) {
        // 親クラスの標準描画処理。ここを省くと残像やちらつきの原因になりやすい。
        super.paintComponent(g);

        // Graphics のコピーを使うと、このメソッド内の設定変更を局所化できます。
        // （外側の Graphics 状態を汚さない）
        Graphics2D g2 = (Graphics2D) g.create();
        // 線や文字のギザギザを減らして、見た目をなめらかにします（アンチエイリアス）。
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (backgroundImage != null) {
            // 1) まず背景を描く
            g2.drawImage(backgroundImage, 0, 0, width, height, this);
        } else {
            // 背景画像がない場合は単色で塗る
            g2.setColor(getBackground());
            g2.fillRect(0, 0, width, height);
        }

        // 2) 次にオーバーレイを重ねる（描画順が重要）
        if (overlay != null) {
            if (!overlay.isFinished()) {
                overlay.paint(g2, width, height);
            } else {
                // 演出が完了したら後処理を行う
                overlay = null;
                demoTimer.stop();
            }
        }

        // create() で作った Graphics を解放する。
        g2.dispose();
    }
}

