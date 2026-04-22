package com.raku.fruitGame.fruit.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private static Clip typeTickClip;
    private static boolean typeTickTried;

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
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        in.close();
                    } catch (Exception ignored) {
                    }
                }
            });
            clip.start();
        } catch (Exception ignored) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public static synchronized void playTypeTick(char ch) {
        if (Character.isWhitespace(ch)) {
            return;
        }
        Clip clip = getTypeTickClip();
        if (clip == null) {
            return;
        }
        try {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception ignored) {
            // タイプ音が鳴らなくても本文表示は継続
        }
    }

    private static @Nullable Clip getTypeTickClip() {
        if (typeTickTried) {
            return typeTickClip;
        }
        typeTickTried = true;
        try {
            AudioInputStream in = openAudio("assets/fruitGame/sounds/type_tick.wav");
            if (in == null) {
                return null;
            }
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            in.close();
            typeTickClip = clip;
            return typeTickClip;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static @Nullable AudioInputStream openAudio(@NotNull String resourcePath) {
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

