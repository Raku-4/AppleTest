package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DemoTimer {
    private final @NotNull Timer timer;

    public DemoTimer(@NotNull Background backGround) {
        this.timer = new Timer(16, event -> backGround.repaint());
        this.timer.setRepeats(true);
    }

    public void start() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
}
