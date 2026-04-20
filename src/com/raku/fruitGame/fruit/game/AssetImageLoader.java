package com.raku.fruitGame.fruit.game;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * assets配下の画像を読み込むローダー。
 *
 * <p>IDE実行時は classpath と filesystem の両方を試し、見つかった画像をキャッシュします。</p>
 */
public final class AssetImageLoader {
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private AssetImageLoader() {
    }

    public static Image load(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            return null;
        }
        return CACHE.computeIfAbsent(resourcePath, AssetImageLoader::loadUncached);
    }

    private static Image loadUncached(String resourcePath) {
        Image image = loadFromClasspath(resourcePath);
        if (image != null) {
            return image;
        }
        return loadFromFileSystem(resourcePath);
    }

    private static Image loadFromClasspath(String resourcePath) {
        String normalized = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        try (InputStream stream = AssetImageLoader.class.getClassLoader().getResourceAsStream(normalized)) {
            if (stream == null) {
                return null;
            }
            return ImageIO.read(stream);
        } catch (IOException ignored) {
            return null;
        }
    }

    private static Image loadFromFileSystem(String resourcePath) {
        String normalized = resourcePath.replace("/", File.separator);
        File file = new File("src" + File.separator + "resources" + File.separator + normalized);
        if (!file.exists()) {
            return null;
        }
        try {
            return ImageIO.read(file);
        } catch (IOException ignored) {
            return null;
        }
    }
}

