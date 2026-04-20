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
import java.io.IOException;

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

        SwingUtilities.invokeLater(() -> {
            Frame frame = null;
            try {
                frame = new Frame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            frame.showFrame();
        });
    }
}
