/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.Image;

/**
 * 画像つきボタンを作るための工場クラスです。
 *
 * <p>同じようなボタンを何度も書かなくて済むように、見た目の共通処理をまとめています。</p>
 *
 * <p>【学習ポイント】</p>
 * <ul>
 *   <li>部品生成を共通化すると、修正箇所を1か所に集約できる</li>
 *   <li>画像が読めないときは文字ボタンへフォールバックできる</li>
 *   <li>UI の見た目調整（枠なし・背景なし）をまとめて適用できる</li>
 * </ul>
 */
public final class SwingImageButtonFactory {
    private SwingImageButtonFactory() {
    }

    /**
     * 画像つきボタンを生成します。
     *
     * <p>通常画像が読めれば画像ボタン化し、読めなければ文字ボタンのまま使えます。</p>
     */
    public static @NotNull JButton create(String fallbackText, String normalPath, String pressedPath, int width, int height) {
        // まずは必ず動くように、文字ボタンとして作る（安全な初期値）
        JButton button = new JButton(fallbackText);
        button.setPreferredSize(new Dimension(width, height));

        Image normal = SwingAssetLoader.load(normalPath);
        Image pressed = SwingAssetLoader.load(pressedPath);

        if (normal != null) {
            // 通常画像があればアイコン化し、見た目を画像ボタン寄りに整える
            button.setIcon(new ImageIcon(normal.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            button.setText("");
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
        }

        if (pressed != null) {
            // 押下時画像は任意。あれば押した見た目を差し替える
            button.setPressedIcon(new ImageIcon(pressed.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        }

        return button;
    }
}

