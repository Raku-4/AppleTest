package com.raku.fruitGame.fruit.game;

import java.awt.Image;

/**
 * インベントリやプレイヤーが扱う果物 1 件の状態です。
 */
public record FruitState(
        String name,
        String color,
        long weight,
        int quantity,
        String description,
        Image icon
) {
}

