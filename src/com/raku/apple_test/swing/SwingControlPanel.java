/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import com.raku.fruitGame.interactive.MainInteractive;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;

/**
 * 画面上部に並ぶボタン群を担当するクラスです。
 *
 * <p>ここでは、{@link FlowLayout} の使い方、ボタンのクリックイベント、
 * 画像付きボタンの再利用を学べます。</p>
 *
 * <p>【このクラスで覚えたいこと】</p>
 * <ul>
 *   <li>UI 部品を「作る場所」と「描く場所」を分ける</li>
 *   <li>ボタンクリックで処理を呼ぶ（イベント駆動）</li>
 *   <li>長い処理は UI スレッド外で実行する</li>
 * </ul>
 */
public class SwingControlPanel extends JPanel {
    private static final String START_BUTTON = "assets/fruitGame/textures/main_menu/start_button.png";
    private static final String START_BUTTON_PRESSED = "assets/fruitGame/textures/main_menu/sunk_start_button.png";
    private static final String CONTINUE_BUTTON = "assets/fruitGame/textures/main_menu/continue_button.png";
    private static final String CONTINUE_BUTTON_PRESSED = "assets/fruitGame/textures/main_menu/sunk_continue_button.png";
    private static final String EXIT_BUTTON = "assets/fruitGame/textures/main_menu/exit_button.png";
    private static final String EXIT_BUTTON_PRESSED = "assets/fruitGame/textures/main_menu/sinked_exit_button.png";

    public SwingControlPanel(SwingBackgroundPanel backgroundPanel, JFrame frame) {
        // FlowLayout.LEFT: 左から順にボタンを並べるレイアウト
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setBackground(new Color(0, 0, 0, 0));

        JButton consoleButton = SwingImageButtonFactory.create(
                "Console Mode",
                START_BUTTON,
                START_BUTTON_PRESSED,
                170,
                56
        );
        consoleButton.addActionListener(event -> {
            // 異常なタイムスタンプのイベントは無視
            if (event.getWhen() < 0L) {
                return;
            }
            // コンソール操作はブロッキングになりやすいため、UI スレッドを止めないよう別スレッドで起動
            Thread consoleThread = new Thread(() -> MainInteractive.main(new String[0]), "MainInteractive-ConsoleMode");
            consoleThread.start();
        });

        JButton transitionButton = SwingImageButtonFactory.create(
                "Transition",
                CONTINUE_BUTTON,
                CONTINUE_BUTTON_PRESSED,
                170,
                56
        );
        transitionButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            // 背景パネル側へ「演出を開始して」と依頼
            backgroundPanel.startTransition();
        });

        JButton exitButton = SwingImageButtonFactory.create(
                "Exit",
                EXIT_BUTTON,
                EXIT_BUTTON_PRESSED,
                170,
                56
        );
        exitButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            // タイマー停止 -> ウィンドウ破棄の順で後片付け
            backgroundPanel.stopAnimation();
            frame.dispose();
        });

        add(consoleButton);
        add(transitionButton);
        add(exitButton);
    }
}

