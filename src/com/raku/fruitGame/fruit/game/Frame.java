package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Frame {
    private final @NotNull JFrame frame;
    private final @NotNull Background background;
    private final @NotNull ControlPanel controlPanel;

    public Frame() throws IOException {
        this.frame = new JFrame("FruitGame");
        this.background = new Background();
        background.gotoOrchard();
        this.controlPanel = new ControlPanel(background, frame);
        initializeFrame();
    }

    private void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // ControlPanel を Background の子にして、オーバーレイで画面全体を覆えるようにする。
        background.setLayout(new BorderLayout());
        background.add(controlPanel, BorderLayout.NORTH);
        frame.add(background, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
    }

    public void showFrame() {
        frame.setVisible(true);
    }
}