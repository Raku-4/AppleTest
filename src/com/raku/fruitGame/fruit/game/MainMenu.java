package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.interactive.MainInteractive;
import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.RenderingHints;

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
    private static final String MAIN_MENU_BG = "assets/fruitGame/textures/main_menu/main_menu.png";

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

        MenuPanel panel = new MenuPanel();

        JPanel controls = getPanel(panel, frame);
        controls.setOpaque(false);

        // 北側に操作ボタン、中央に描画パネルという構成
        frame.setLayout(new BorderLayout());
        frame.add(controls, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        // null で中央配置
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * ボタン群パネルを生成します。
     */
    private static @NotNull JPanel getPanel(MenuPanel panel, JFrame frame) {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        controls.setBackground(new Color(0, 0, 0, 0));

        JButton playConsole = createImageButton(
                "Console Mode",
                "assets/fruitGame/textures/main_menu/start_button.png",
                "assets/fruitGame/textures/main_menu/start_button_sinked.png",
                170,
                56
        );
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

        JButton transition = createImageButton(
                "Transition",
                "assets/fruitGame/textures/main_menu/continue_button.png",
                "assets/fruitGame/textures/main_menu/sinked_continue_button.png",
                170,
                56
        );
        transition.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }

            panel.startTransition(new BlackoutOverlay(600, 2000L));
        });

        JButton exit = createImageButton(
                "Exit",
                "assets/fruitGame/textures/main_menu/exit_button.png",
                "assets/fruitGame/textures/main_menu/sinked_exit_button.png",
                170,
                56
        );
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

    /**
     * メニュー背景と遷移オーバーレイを描画する専用パネル。
     */
    private static final class MenuPanel extends JPanel {
        private final Image backgroundImage = AssetImageLoader.load(MAIN_MENU_BG);
        private TransitionOverlay overlay;

        private MenuPanel() {
            setOpaque(true);
            setBackground(new Color(210, 245, 255));

            // 約60fps で repaint してアニメーションを滑らかにします。
            javax.swing.Timer timer = new javax.swing.Timer(16, event -> repaint());
            timer.start();
        }

        private void startTransition(TransitionOverlay overlay) {
            this.overlay = overlay;
            this.overlay.start();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (backgroundImage != null) {
                g2.drawImage(backgroundImage, 0, 0, w, h, this);
            } else {
                g2.setColor(getBackground());
                g2.fillRect(0, 0, w, h);
            }

            if (overlay != null) {
                if (!overlay.isFinished()) {
                    overlay.paint(g2, w, h);
                } else {
                    overlay = null;
                }
            }

            g2.dispose();
        }
    }

    private static @NotNull JButton createImageButton(String fallbackText, String normalPath, String pressedPath, int width, int height) {
        JButton button = new JButton(fallbackText);
        button.setPreferredSize(new Dimension(width, height));

        Image normal = AssetImageLoader.load(normalPath);
        Image pressed = AssetImageLoader.load(pressedPath);
        if (normal != null) {
            button.setIcon(new ImageIcon(normal.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            button.setText("");
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setOpaque(false);
        }

        if (pressed != null) {
            button.setPressedIcon(new ImageIcon(pressed.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        }

        return button;
    }
}
