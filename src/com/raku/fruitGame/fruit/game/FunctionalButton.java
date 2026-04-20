package com.raku.fruitGame.fruit.game;

import com.raku.apple_test.swing.SwingAssetLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class FunctionalButton {
    private FunctionalButton() {}

    public static @NotNull JButton createButton(String text, String normalPath, String pressedPath, int width, int height) {
        JButton button = new JButton(text);
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
