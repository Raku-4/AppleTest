package com.raku.apple_test.swing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Random;

/**
 * 【超初心者向け解説】
 *
 * このクラスは「ゲーム画面全体をカッコよく暗くする演出」を実現します。
 *
 * 【演出の流れ】
 * 1. 画面を16×16ピクセルの「タイル」に分割
 * 2. 各タイルがバラバラな速度で白→黒へ変わっていく
 * 3. 画面全体が黒くなったら、少しの間その状態を保持
 * 4. 次に黒→白へ戻していく
 *
 * 結果として、見た目は「画面全体がノイズみたいにザザッと暗くなり、また戻る」
 * という効果になります。これは Minecraft や映画のような高度な演出です。
 *
 * 【プログラムの重要な考え方】
 * - 各タイルは「独立した速度」を持つ → Random で決める
 * - 時間経過を追跡する → startedAt + System.currentTimeMillis()
 * - 時間に応じてアルファ値（透明度）を変える → 0=透明, 255=不透明
 */
public class SwingTransitionOverlay{
    /**
     * 【タイル分割のサイズ】
     *
     * 画面を 16×16 ピクセルの正方形ブロックに分割します。
     * つまり、800×600 の画面なら 50 × 37 個のタイルができます。
     *
     * この値を大きくすると：ブロックサイズが大きくなる、処理が軽い
     * この値を小さくすると：ブロックサイズが小さくなる、処理が重い
     */
    private static final int TILE_SIZE = 16;

    /**
     * 【アニメーション速度】
     *
     * 「白 → 黒」（または「黒 → 白」）に変わるまでにかかる時間をミリ秒で指定します。
     * 例: 600 = 0.6秒で色が完全に変わる
     *
     * しかし、各タイルは独立した速度倍率を持つので、
     * 実際には「基準時間 × 速度倍率」で個別に計算されます。
     */
    private final long durationMillis;

    /**
     * 【黒い状態を保つ時間】
     *
     * 画面全体が黒くなった後、その状態を何ミリ秒間保つか？
     * 例: 120 = 0.12秒間、画面全体が真っ黒のままになります。
     *
     * コンストラクタ内で 120 に固定されていますが、
     * 本来は 「new BlackoutOverlay(600, 120)」のように指定できるのが理想です。
     */
    private final long holdMillis;

    /**
     * 【アニメーション開始時刻】
     *
     * System.currentTimeMillis() で取得した「開始時の絶対時刻」を記録します。
     * 例: 1700000000000 みたいな大きな数字
     *
     * paint() が呼ばれるたび、「現在時刻 - startedAt」を計算して
     * 「開始からどのくらい経ったか」を知ります。
     *
     * 0 の場合は「まだ開始されていない」という意味です。
     */
    private long startedAt;

    /**
     * 【各タイルの速度マップ】
     *
     * speedMap[y][x] には、座標 (x, y) のタイルが持つ「速度倍率」が入ります。
     *
     * 例:
     * - speedMap[0][0] = 0.8f  → このタイルは基準速度の 0.8倍（ゆっくり）
     * - speedMap[1][1] = 1.5f  → このタイルは基準速度の 1.5倍（速い）
     *
     * こうすることで、各タイルが「バラバラな速度」で色が変わります。
     *
     * null または古いサイズの場合は、ensureSpeedMap() で再計算されます。
     */
    private float[] @Nullable [] speedMap;

    /**
     * 【画面のタイル数（列と行）】
     *
     * cols = 画面幅 / 16 （横方向のタイル数）
     * rows = 画面高さ / 16 （縦方向のタイル数）
     *
     * 初期値 -1 は「未計算」を意味します。
     * paint() が最初に呼ばれた時に、ensureSpeedMap() で正しい値に更新されます。
     */
    private int cols = -1;
    private int rows = -1;

    /**
     * 【ランダム値生成器】
     *
     * 各タイルの速度を「ランダムに」決めるために使います。
     * 同じ処理を何度も実行しても、毎回違う速度になります。
     */
    private final Random random = new Random();

    /**
     * 【コンストラクタ】
     *
     * このクラスのインスタンスを作成する時に呼ばれます。
     * 「このアニメーションは何ミリ秒かけて色を変えるか」を指定します。
     *
     * @param durationMillis
     *        白→黒（と黒→白）にかかる時間。ミリ秒単位。
     *        ただし実際には各タイルが独立した速度を持つので、
     *        「基準時間」として機能します。
     *
     * @param holdMillis
     *        このパラメータは受け取りますが、実際には 120 に固定されます。
     *        将来の拡張のための名残かもしれません。
     */
    public SwingTransitionOverlay(long durationMillis, long holdMillis) {
        // 0 や負数が入ると divide by zero の原因になるので、最小 1 にします
        this.durationMillis = Math.max(1L, durationMillis);

        // パラメータは無視して、固定値 120 を使う
        this.holdMillis = holdMillis;

        // 未開始状態にする
        this.startedAt = 0L;
    }

    /**
     * 【アニメーション開始】
     *
     * このメソッドは、ゲーム画面で「演出を開始したい」という時に呼ばれます。
     * 例えば、シーン遷移時などです。
     *
     * 【何をするのか】
     * 1. 現在の時刻をスナップショットとして記録 → startedAt
     * 2. 古い速度マップをリセット（null に）
     * 3. タイル数をリセット（-1 に）
     *
     * こうすることで、「これから新しいアニメーション」という初期状態にします。
     */
    public void start() {
        // 「今この瞬間」の絶対時刻を記録します
        // 後で paint() が呼ばれるたび「現在時刻 - startedAt」を計算します
        startedAt = System.currentTimeMillis();

        // 古い速度マップを削除（画面サイズが変わったかもしれないので）
        speedMap = null;

        // タイル数のキャッシュもリセット
        cols = -1;
        rows = -1;
    }

    /**
     * 【画面描画】
     *
     * このメソッドは「毎フレーム」（通常 60回/秒）呼ばれます。
     * ここで「今この瞬間、各タイルはどのくらい黒くなるべきか」を計算して描画します。
     *
     * 【何をするのか（大まかな流れ）】
     * 1. 安全性チェック（開始されているか？ サイズは有効か？）
     * 2. 速度マップの準備（なければ作る）
     * 3. 現在の経過時間を計算
     * 4. 3つのフェーズのどれに居るか判定
     *    - フェーズ1: 白 → 黒（進行中）
     *    - フェーズ2: 黒 → 黒（保持中）
     *    - フェーズ3: 黒 → 白（戻り中）
     * 5. 各タイルのアルファ値（透明度）を計算して描画
     *
     * @param g2
     *        Java Swing の Graphics2D オブジェクト。画面に描画するためのペンのようなもの。
     *
     * @param width
     *        画面の幅（ピクセル）例: 800
     *
     * @param height
     *        画面の高さ（ピクセル）例: 600
     */
    public void paint(@NotNull Graphics2D g2, int width, int height) {
        // 【安全性チェック】
        // 1. startedAt == 0L：アニメーションがまだ start() されていない
        // 2. width <= 0 または height <= 0：画面がまだ初期化されていない
        // これらの場合は何も描画しません（描画する意味がないため）
        if (startedAt == 0L || width <= 0 || height <= 0) {
            return;
        }

        // 【速度マップの準備】
        // 画面サイズに合わせた speedMap を用意します
        // 最初は null なので、ensureSpeedMap() が新規作成します
        // 次以降、サイズが同じなら再利用します（効率化のため）
        ensureSpeedMap(width, height);

        // 【経過時間を計算】
        // 「今この瞬間の絶対時刻」- 「開始時刻」 = 「開始からの経過ミリ秒数」
        //
        // 例）
        // - start() 時:        startedAt = 1700000000000
        // - paint() 1回目:     現在時刻 = 1700000000100  → elapsed = 100ms
        // - paint() 2回目:     現在時刻 = 1700000000166  → elapsed = 166ms
        //
        // つまり、paint() が呼ばれるたび elapsed は増えていきます。
        long elapsed = System.currentTimeMillis() - startedAt;

        // 【3つのフェーズの時間境界を計算】
        //
        // timeline:
        // ┌─────────────────┬──────────┬─────────────────┐
        // │ フェーズ1         │ フェーズ2 │ フェーズ3        │
        // │ 白→黒            │  黒→黒    │ 黒→白           │
        // │ duration        │ hold     │ duration        │
        // └─────────────────┴──────────┴─────────────────┘
        // 0              P1End     HoldEnd           P2End
        //
        long phase1End = durationMillis;          // 第1フェーズが終わる時刻
        long holdEnd = phase1End + holdMillis;    // 保持フェーズが終わる時刻
        long phase2End = holdEnd + durationMillis; // 第3フェーズが終わる時刻

        // 【完了判定】
        // すべてのフェーズが終わったなら、描画を止めます
        if (elapsed >= phase2End) {
            return;
        }


        // 【各タイルを描画】
        // 画面全体を 16×16 のブロックに分割し、
        // 各ブロックごとに「黒のアルファ値」を計算して描画します。
        for (int ty = 0; ty < rows; ty++) {
            for (int tx = 0; tx < cols; tx++) {
                // このタイルの「速度倍率」を取得
                // 例: speedMap[ty][tx] = 1.2 なら、このタイルは速度 1.2倍
                float speed = speedMap[ty][tx];

                // 【このタイルの現在のアルファ値（0.0～1.0）を計算】
                // 0.0 = 完全に透明（白が見える）
                // 1.0 = 完全に不透明（黒が見える）
                float alpha01;

                if (elapsed < phase1End) {
                    // 【フェーズ1: 白 → 黒】
                    //
                    // elapsed が 0 → durationMillis と増えるにつれ、
                    // alpha01 は 0.0 → 1.0 へ変わります。
                    //
                    // ただし各タイルは speed 倍率を持つので、
                    // 「基準時間」に speed を掛けることで
                    // 「速く黒くなるタイル」と「遅く黒くなるタイル」を作ります。
                    //
                    // 例) elapsed = 300ms, duration = 600ms, speed = 1.2
                    //     alpha01 = (300 / 600.0) * 1.2 = 0.6
                    //     → このタイルは 60% 黒くなっています。
                    alpha01 = clamp01((elapsed / (float) durationMillis) * speed);

                } else if (elapsed < holdEnd) {
                    // 【フェーズ2: 黒 → 黒（保持）】
                    //
                    // このフェーズでは、すべてのタイルが「完全に黒い」状態です。
                    // holdMillis の間、この状態が続きます。
                    alpha01 = 1.0f;
                } else {
                    // 【フェーズ3: 黒 → 白（戻り）】
                    //
                    // holdEnd からの経過時間を e2 として計算します。
                    // e2 が 0 → durationMillis と増えるにつれ、
                    // alpha01 は 1.0 → 0.0 へ変わります。
                    //
                    // これは「フェーズ1 の逆」です。
                    // alpha01 = 1.0f - p により、反転させます。
                    float e2 = (elapsed - holdEnd);
                    float p = clamp01((e2 / (float) durationMillis) * speed);
                    alpha01 = 1.0f - p;
                }

                // 【アルファ値を 0～255 の整数に変換】
                // Graphics2D は「アルファ値は 0～255」を期待します。
                // 0.0 → 0（透明）、1.0 → 255（不透明）
                int alpha = (int) (alpha01 * 255.0f);

                // 【タイルの矩形座標を計算】
                int x = tx * TILE_SIZE;      // 左上 X 座標
                int y = ty * TILE_SIZE;      // 左上 Y 座標
                // 「幅」と「高さ」は TILE_SIZE ですが、
                // 端のタイルは画面のはじから出ないようにクリッピング
                int w = Math.min(TILE_SIZE, width - x);
                int h = Math.min(TILE_SIZE, height - y);

                // 【黒い矩形を描画】
                // Color(0, 0, 0, alpha) は「黒にアルファ値 alpha を付けたもの」
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.fillRect(x, y, w, h);
            }
        }

    }

    /**
     * 【アニメーション完了判定】
     *
     * このメソッドは「演出がすべて終わったかどうか」を判定します。
     * true が返ったら、「このオブジェクトはもう使わなくてもよい」という意味です。
     *
     * 【判定ロジック】
     * - startedAt == 0L: start() がまだ呼ばれていない → true（無視状態）
     * - elapsed >= 全フェーズの総時間: すべての演出が終わった → true
     *
     * @return
     *         true なら「完了」、false なら「まだ進行中」
     */
    public boolean isFinished() {
        // 開始されていないなら「完了」と見なす
        if (startedAt == 0L) return true;

        // 【総時間の計算】
        // フェーズ1（白→黒）+ フェーズ2（保持） + フェーズ3（黒→白）
        long total = durationMillis + holdMillis + durationMillis;

        // 経過時間が総時間以上なら「完了」
        return (System.currentTimeMillis() - startedAt) >= total;
    }

    /**
     * 【速度マップの初期化・維持】
     *
     * このメソッドは「各タイルの速度を管理する配列」を用意します。
     * 効率化のため：
     * - 最初の呼び出し: 新規作成
     * - 2回目以降（サイズ同じ）: 再利用
     * - 画面リサイズされた: 再計算
     *
     * 【参考】
     * タイル数 = ceil(画面サイズ / 16)
     * 例: 幅 800 → 800 / 16 = 50 個のタイル
     * 例: 幅 810 → (810 + 16 - 1) / 16 = 52 個のタイル
     *
     * @param width
     *        画面の幅
     *
     * @param height
     *        画面の高さ
     */
    private void ensureSpeedMap(int width, int height) {
        // 【新しいタイル数を計算】
        // (width + 15) / 16 は「幅を 16 で割った『切り上げ』」と同じ意味です。
        // 例: (800 + 15) / 16 = 815 / 16 = 50
        // 例: (801 + 15) / 16 = 816 / 16 = 51
        int newCols = (width + TILE_SIZE - 1) / TILE_SIZE;
        int newRows = (height + TILE_SIZE - 1) / TILE_SIZE;

        // 【キャッシュの有効性をチェック】
        // speedMap が既に存在し、かつサイズが同じなら、再利用します。
        if (speedMap != null && newCols == cols && newRows == rows) {
            return;
        }

        // 【新規作成】
        // speedMap を「新しいタイル数」に合わせて確保します。
        cols = newCols;
        rows = newRows;
        speedMap = new float[rows][cols];

        // 【各タイルにランダムな速度を割り当て】
        //
        // speed の範囲: 0.55f ~ 1.75f
        //
        // 計算式: 0.55f + random.nextFloat() * 1.20f
        // - random.nextFloat(): 0.0 ～ 1.0 のランダムな値
        // - * 1.20f: 0.0 ～ 1.2 にスケール
        // - + 0.55f: 0.55 ～ 1.75 に平行移動
        //
        // 例）
        // random = 0.0 → speed = 0.55 （最も遅い）
        // random = 0.5 → speed = 1.15 （中程度）
        // random = 1.0 → speed = 1.75 （最も速い）
        //
        // こうすることで、各タイルが「バラバラな速度」を持ちます。
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                speedMap[y][x] = 0.55f + random.nextFloat() * 1.20f;
            }
        }
    }

    /**
     * 【値をクランプ（範囲内に収める）】
     *
     * v が 0.0 ～ 1.0 の範囲外なら、範囲内に収めます。
     *
     * 【動機】
     * アルファ計算で、時々「1.05」や「-0.2」みたいな値が出ることがあります。
     * これはバグではなく、計算の性質上の丸め誤差です。
     * しかし Graphics2D は「0.0 ～ 1.0」しか期待しないので、
     * 超過分はクリッピングします。
     *
     * 【例】
     * clamp01(1.05) → 1.0
     * clamp01(-0.2) → 0.0
     * clamp01(0.5) → 0.5
     *
     * @param v
     *        0.0 ～ 1.0 の範囲に収めたい値
     *
     * @return
     *         0.0 ～ 1.0 の範囲内の値
     */
    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
