package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.interactive.MainInteractive;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Swing版 FruitGame のメインメニュー。
 *
 * <p>機能:</p>
 * <ul>
 *   <li>Consoleモード起動ボタン</li>
 *   <li>画面遷移エフェクト確認ボタン</li>
 *   <li>終了ボタン</li>
 * </ul>
 */
public class MainMenu {
    public static void main(String[] args) {
        // Swingコンポーネントは EDT (Event Dispatch Thread) 上で生成するのが原則です。
        SwingUtilities.invokeLater(MainMenu::createAndShow);
    }

    /**
     * 画面全体を作成して表示します。
     */
    private static void createAndShow() {
        JFrame frame = new JFrame("FruitGame Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        OrchardPanel orchardPanel = new OrchardPanel();
        JPanel controls = getPanel(orchardPanel, frame);

        // 北側に操作ボタン、中央に描画パネルという構成
        frame.setLayout(new BorderLayout());
        frame.add(controls, BorderLayout.NORTH);
        frame.add(orchardPanel, BorderLayout.CENTER);

        // null で中央配置
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * ボタン群パネルを生成します。
     */
    private static @NotNull JPanel getPanel(OrchardPanel orchardPanel, JFrame frame) {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        /* ボタンの生成には名前が必要 */
        JButton playConsole = new JButton("Console Mode");

         /* addActionListener とは、
           ボタンがクリックされたときの処理を定義するためのメソッドです。
          * 引数には ActionListener インターフェースを実装したオブジェクトを渡します。
          * ここではラムダ式を使って簡潔に記述しています。
          * イベントオブジェクト (event) を受け取り、必要な処理を実行します。
          */
        playConsole.addActionListener(event -> {
            // 念のため異常時刻イベントは無視。
            if (event.getWhen() < 0L) {
                return;
            }

            // コンソール版はブロッキング入力を行うので、GUIスレッドを止めないよう別スレッドで実行。
            Thread consoleModeThread =
                    new Thread(() -> MainInteractive.main(new String[0]), "MainInteractive-ConsoleMode");
            consoleModeThread.start();
        });

        JButton transition = new JButton("Transition");
        transition.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }

            // 左から右へ 600ms のスライドオーバーレイを開始
            orchardPanel.startTransition(new SlideTransitionOverlay(600, true));
        });

        JButton exit = new JButton("Exit");
        exit.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            frame.dispose();
        });

        controls.add(playConsole);
        controls.add(transition);
        controls.add(exit);
        return controls;
    }
}
