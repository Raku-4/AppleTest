package com.raku.fruitGame.fruit.game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.Toolkit;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 軽量な操作音プレイヤー。wav が無い場合はビープ音で代替します。
 */
public final class SoundPlayer {
    private SoundPlayer() {
    }

    public static void playUi(String fileName) {
        String resourcePath = "assets/fruitGame/sounds/" + fileName;
        try {
            AudioInputStream in = openAudio(resourcePath);
            if (in == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            clip.addLineListener(event -> {
                switch (event.getType()) {
                    case STOP -> {
                        clip.close();
                        try {
                            in.close();
                        } catch (Exception ignored) {
                        }
                    }
                    default -> {
                    }
                }
            });
            clip.start();
        } catch (Exception ignored) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private static AudioInputStream openAudio(String resourcePath) {
        try {
            InputStream classpath = SoundPlayer.class.getClassLoader().getResourceAsStream(resourcePath);
            if (classpath != null) {
                return AudioSystem.getAudioInputStream(classpath);
            }
            Path file = Path.of("src", "resources", resourcePath.replace("/", File.separator));
            if (Files.exists(file)) {
                return AudioSystem.getAudioInputStream(file.toFile());
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }
}

