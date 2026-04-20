package com.raku.fruitGame.fruit.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * 横方向にスライドする暗幕オーバーレイ。
 *
 * <p>進行率 progress を 0.0 -> 1.0 で計算し、矩形の X 座標に変換します。</p>
 */
public class SlideTransitionOverlay implements TransitionOverlay {
    private static final String OVERLAY_TEXTURE = "assets/fruitGame/textures/misc/img.png";
    /** アニメーション継続時間 (ミリ秒) */
    private final long durationMillis;

    /** true: 左 -> 右 / false: 右 -> 左 */
    private final boolean leftToRight;

    /** start() が呼ばれた時刻。0 は未開始を意味します。 */
    private long startedAt;

    private final Image overlayTexture;

    public SlideTransitionOverlay(long durationMillis, boolean leftToRight) {
        // 0 や負値が入ってもゼロ除算を避けるため最低1msに補正
        this.durationMillis = Math.max(1L, durationMillis);
        this.leftToRight = leftToRight;
        this.startedAt = 0L;
        this.overlayTexture = AssetImageLoader.load(OVERLAY_TEXTURE);
    }

    @Override
    public void start() {
        // 【重要な設計パターン】
        // System.currentTimeMillis() は、1970年1月1日からの経過ミリ秒数（絶対値）を返します。
        // しかし、ここで記録する目的は「絶対時刻を知りたい」のではなく、
        // 「このメソッドが呼ばれた時点から、paint() が何度も呼ばれるたび
        //   『どれだけ時間が経ったか』を計測する」ためのスナップショットです。
        //
        // つまり、後で paint() で「現在時刻 - start時刻」という差分を計ることで、
        // アニメーション開始からの経過時間が分かります。
        startedAt = System.currentTimeMillis();
    }

    @Override
    public void paint(Graphics2D g2, int width, int height) {
        // 未開始または完了済みなら描画しません。
        if (startedAt == 0L || isFinished()) {
            return;
        }

        // 【経過時間の計算】
        // 「現在の絶対時刻」- 「アニメーション開始時の絶対時刻」 = 「開始からの経過ミリ秒数」
        //
        // 例)
        //   start() 実行時: startedAt = 1234567890000 (ある時刻の絶対値)
        //   paint() 実行時: System.currentTimeMillis() = 1234567891200 (100ms後)
        //   → elapsed = 1234567891200 - 1234567890000 = 1200ms
        //
        // 絶対値そのものは無意味ですが、差分を取ることで「経過時間」が得られます。
        double elapsed = (double) (System.currentTimeMillis() - startedAt);

        // 経過時間を 0.0 - 1.0 の進行率に正規化
        // 例) duration=600ms, elapsed=300ms → progress = 0.5 (50%進行)
        double progress = Math.min(1.0, elapsed / durationMillis);

        // 進行率を矩形の x 座標に変換
        int x;
        if (leftToRight) {
            x = (int) ((progress - 1.0) * width);
        } else {
            x = (int) ((1.0 - progress) * width);
        }

        if (overlayTexture != null) {
            g2.drawImage(overlayTexture, x, 0, width, height, null);
            return;
        }

        // テクスチャが無い場合のみ半透明の黒で代替。
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(x, 0, width, height);
    }

    @Override
    public boolean isFinished() {
        return startedAt != 0L && (System.currentTimeMillis() - startedAt) >= durationMillis;
    }
}
