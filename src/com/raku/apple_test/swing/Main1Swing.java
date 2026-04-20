/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import javax.swing.SwingUtilities;

/**
 * Swing 学習用の入口クラスです。
 *
 * <p>このクラスの役割はたった1つで、{@link SwingLessonFrame} を起動することです。</p>
 *
 * <p>初心者のうちは、まず「main メソッドは入口」「実際の画面作りは別クラス」と
 * 分けて考えると、とても読みやすくなります。</p>
 *
 * <p>【このクラスで覚えたいこと】</p>
 * <ul>
 *   <li>main は「最初に呼ばれる場所」</li>
 *   <li>画面作成は EDT 上で行うのが Swing の基本</li>
 *   <li>本体ロジックを別クラスへ分けると、役割が明確になる</li>
 * </ul>
 */
public class Main1Swing {
    /**
     * 学習用画面を起動します。
     *
     * <p>処理の流れ:</p>
     * <ol>
     *   <li>EDT に「画面作成の仕事」を依頼する</li>
     *   <li>{@link SwingLessonFrame} を new する</li>
     *   <li>{@code showFrame()} で画面を表示する</li>
     * </ol>
     */
    public static void main(String[] args) {
        // EDT (Event Dispatch Thread) は、Swing の画面更新やボタン操作などを
        // まとめて担当する「専用の作業スレッド」です。
        // Swing はこの EDT 上で部品を作成・更新するのが基本です。
        // そうしないと、画面が固まったり、表示が乱れたりする原因になりやすいからです。

        SwingUtilities.invokeLater(() -> {
            SwingLessonFrame frame = new SwingLessonFrame();
            frame.showFrame();
        });
    }
}

/*
 覚えておくと便利な単語
 - スレッド (Thread): プログラム内で並行して進む作業の流れ。
 - EDT (Event Dispatch Thread): Swing の画面更新やイベント処理を担当する専用スレッド。
 - キャッシュ (Cache): 一度読んだデータを保存し、同じ読み込みを省略する仕組み。
*/
