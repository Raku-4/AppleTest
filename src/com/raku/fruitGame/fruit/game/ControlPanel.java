package com.raku.fruitGame.fruit.game;

import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import org.jetbrains.annotations.NotNull;

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
    private static final String CSV_HEADER = "timestamp,fruitName,color,weight,taste,maturity,elapsedSeconds,treeId\n";
    private static final String SETTING_ICON = "assets/fruitGame/textures/misc/setting_icon.png";
    private static final String MESSAGE_ICON = "assets/fruitGame/textures/misc/message_icon.png";
    private static final String BAG_ICON = "assets/fruitGame/textures/human/bag.png";
    private static final String SETTING_EXIT = "assets/fruitGame/textures/setting_menu/exit_button.png";
    private static final String SETTING_EXIT_SUNK = "assets/fruitGame/textures/setting_menu/sunk_exit_button.png";
    private static final String SETTING_RETURN = "assets/fruitGame/textures/setting_menu/return_button.png";
    private static final String SETTING_RETURN_SUNK = "assets/fruitGame/textures/setting_menu/sunk_return_button.png";

    private final @NotNull JPanel menuPanel;
    private final @NotNull JPanel orchardHudPanel;
    private final @NotNull JPanel settingPopupPanel;
    private final JFrame ownerFrame;
    private MessageConsoleDialog messageDialog;
    private InventoryDialog inventoryDialog;

    public static String @NotNull [] buttons = {
            "assets/fruitGame/textures/main_menu/start_button.png",
            "assets/fruitGame/textures/main_menu/sunk_start_button.png",
            "assets/fruitGame/textures/main_menu/continue_button.png",
            "assets/fruitGame/textures/main_menu/sunk_continue_button.png",
            "assets/fruitGame/textures/main_menu/hide_continue_button.png",
            "assets/fruitGame/textures/main_menu/exit_button.png",
            "assets/fruitGame/textures/main_menu/sunk_exit_button.png"
    };

    public ControlPanel(@NotNull Background background, @NotNull JFrame frame) {
        INSTANCE = this;
        this.ownerFrame = frame;

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
            SoundPlayer.playUi("click.wav");
            try {
                // Start は毎回新規ゲーム開始なので、履歴CSVをヘッダーだけに初期化する。
                Files.writeString(
                        CSV_PATH,
                        CSV_HEADER,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE
                );
                FruitHistory.clearAll();
                background.resetForNewStart();
            } catch (IOException e) {
                showError(frame, "CSV の作成に失敗しました: " + CSV_PATH.toAbsolutePath());
                return;
            }

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
                SoundPlayer.playUi("click.wav");
                try {
                    FruitHistory.loadCsv(CSV_PATH);
                    background.reloadHistoryAndOrchard();
                } catch (IOException e) {
                    showError(frame, "CSV の読み込みに失敗しました: " + CSV_PATH.toAbsolutePath());
                    return;
                }

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
            SoundPlayer.playUi("close.wav");
            background.stopAnimation();
            frame.dispose();
        });

        menuPanel.add(startButton);
        menuPanel.add(continueButton);
        menuPanel.add(exitButton);

        JButton settingButton = FunctionalButton.createButton("Setting", SETTING_ICON, null, 34, 34);
        settingPopupPanel = createSettingPopup(background, frame);
        settingButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            SoundPlayer.playUi("click.wav");
            settingPopupPanel.setVisible(!settingPopupPanel.isVisible());
            revalidate();
            repaint();
        });

        JButton messageButton = FunctionalButton.createButton("Message", MESSAGE_ICON, null, 34, 34);
        messageButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            SoundPlayer.playUi("open.wav");
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
            SoundPlayer.playUi("open.wav");
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
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setOpaque(false);
        eastPanel.add(orchardHudPanel, BorderLayout.NORTH);
        eastPanel.add(settingPopupPanel, BorderLayout.SOUTH);
        add(eastPanel, BorderLayout.EAST);

        if (called) {
            menuPanel.setVisible(false);
            orchardHudPanel.setVisible(true);
            revalidate();
            repaint();

            called = false;
        }
    }

    private @NotNull JPanel createSettingPopup(@NotNull Background background, @NotNull JFrame frame) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);
        panel.setVisible(false);

        JButton returnButton = FunctionalButton.createButton("Return", SETTING_RETURN, SETTING_RETURN_SUNK, 120, 40);
        returnButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            SoundPlayer.playUi("click.wav");
            panel.setVisible(false);
            revalidate();
            repaint();
        });

        JButton exitButton = FunctionalButton.createButton("Exit", SETTING_EXIT, SETTING_EXIT_SUNK, 120, 40);
        exitButton.addActionListener(event -> {
            if (event.getWhen() < 0L) {
                return;
            }
            SoundPlayer.playUi("close.wav");
            background.stopAnimation();
            frame.dispose();
        });

        panel.add(returnButton);
        panel.add(exitButton);
        return panel;
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

    public static void showAteLog(String fruitName) {
        if (INSTANCE == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (INSTANCE.messageDialog == null) {
                INSTANCE.messageDialog = new MessageConsoleDialog(INSTANCE.ownerFrame);
            }
            INSTANCE.messageDialog.showAteLog(fruitName);
        });
    }

    public static void showMessageLog(String message) {
        if (INSTANCE == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (INSTANCE.messageDialog == null) {
                INSTANCE.messageDialog = new MessageConsoleDialog(INSTANCE.ownerFrame);
            }
            INSTANCE.messageDialog.setVisible(true);
            INSTANCE.messageDialog.appendExternalSystemLog(message);
        });
    }
}
