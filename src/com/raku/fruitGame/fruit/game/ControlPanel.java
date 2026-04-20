package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import com.raku.fruitGame.interactive.MainInteractive;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ControlPanel extends JPanel {

    private static boolean called =  false;
    private static ControlPanel INSTANCE;

    private static final Path CSV_PATH = Path.of("fruit_history.csv");
    private static final String CSV_HEADER = "timestamp,fruitName,color,weight\n";
    private static final String SETTING_ICON = "assets/fruitGame/textures/misc/setting_icon.png";
    private static final String MESSAGE_ICON = "assets/fruitGame/textures/misc/message_icon.png";
    private static final String BAG_ICON = "assets/fruitGame/textures/human/bag.png";

    private final JPanel menuPanel;
    private final JPanel orchardHudPanel;
    private MessageConsoleDialog messageDialog;
    private InventoryDialog inventoryDialog;

    public static String[] buttons = {
            "assets/fruitGame/textures/main_menu/start_button.png",
            "assets/fruitGame/textures/main_menu/sunk_start_button.png",
            "assets/fruitGame/textures/main_menu/continue_button.png",
            "assets/fruitGame/textures/main_menu/sunk_continue_button.png",
            "assets/fruitGame/textures/main_menu/hide_continue_button.png",
            "assets/fruitGame/textures/main_menu/exit_button.png",
            "assets/fruitGame/textures/main_menu/sunk_exit_button.png"
    };

    public ControlPanel(Background background, JFrame frame) {
        INSTANCE = this;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setBackground(new Color(0, 0, 0, 0));
        setOpaque(false);

        this.menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        this.menuPanel.setOpaque(false);
        this.orchardHudPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        this.orchardHudPanel.setOpaque(false);

        JButton startButton = FunctionalButton.createButton(
                "Console mode",
                buttons[0],
                buttons[1],
                170,
                56
        );
        startButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            try {
                // Start は毎回新規ゲーム開始なので、履歴CSVをヘッダーだけに初期化する。
                Files.writeString(
                        CSV_PATH,
                        CSV_HEADER,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE
                );
            } catch (IOException e) {
                showError(frame, "CSV の作成に失敗しました: " + CSV_PATH.toAbsolutePath());
                return;
            }

            Thread consoleThread = new Thread(() -> MainInteractive.main(new String[0]), "MainInteractive-ConsoleMode");
            consoleThread.start();

            background.startTransition();
        });

        boolean hasHistory = Files.exists(CSV_PATH);
        JButton continueButton = FunctionalButton.createButton(
                "Continue",
                hasHistory ? buttons[2] : buttons[4],
                hasHistory ? buttons[3] : null,
                170,
                56
        );
        continueButton.setEnabled(hasHistory);

        if (hasHistory) {
            continueButton.addActionListener(event -> {
                if (event.getWhen() < 0L) {
                    return;
                }
                try {
                    FruitHistory.loadCsv(CSV_PATH);
                } catch (IOException e) {
                    showError(frame, "CSV の読み込みに失敗しました: " + CSV_PATH.toAbsolutePath());
                    return;
                }

                Thread consoleThread = new Thread(() -> MainInteractive.main(new String[0]), "MainInteractive-ConsoleMode");
                consoleThread.start();
                background.startTransition();
            });
        }

        JButton exitButton = FunctionalButton.createButton(
            "Exit",
            buttons[5],
            buttons[6],
            170,
            56
        );
        exitButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            background.stopAnimation();
            frame.dispose();
        });

        menuPanel.add(startButton);
        menuPanel.add(continueButton);
        menuPanel.add(exitButton);

        JButton settingButton = FunctionalButton.createButton("Setting", SETTING_ICON, null, 34, 34);
        settingButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            JOptionPane.showMessageDialog(frame, "Setting icon clicked", "Setting", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton messageButton = FunctionalButton.createButton("Message", MESSAGE_ICON, null, 34, 34);
        messageButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            if (messageDialog == null) {
                messageDialog = new MessageConsoleDialog(frame);
            }
            messageDialog.setVisible(true);
        });

        JButton bagButton = FunctionalButton.createButton("Bag", BAG_ICON, null, 34, 34);
        bagButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            if (inventoryDialog == null) {
                inventoryDialog = new InventoryDialog(frame, background.getPlayer()::setHeldFruit);
            }
            inventoryDialog.open();
        });

        orchardHudPanel.add(settingButton);
        orchardHudPanel.add(messageButton);
        orchardHudPanel.add(bagButton);
        orchardHudPanel.setVisible(false);

        add(menuPanel, BorderLayout.WEST);
        add(orchardHudPanel, BorderLayout.EAST);

        if (called) {
            menuPanel.setVisible(false);
            orchardHudPanel.setVisible(true);
            revalidate();
            repaint();

            called = false;
        }
    }

    private static void showError(JFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "CSV Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void resetAllButton() {
        called = true;

        // 既に生成済みのControlPanelへ即時反映（コンストラクタ再実行を待たない）
        if (INSTANCE != null) {
            SwingUtilities.invokeLater(() -> {
                INSTANCE.menuPanel.setVisible(false);
                INSTANCE.orchardHudPanel.setVisible(true);
                INSTANCE.revalidate();
                INSTANCE.repaint();
                called = false;
            });
        }
    }
}
