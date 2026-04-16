package com.raku.fruitGame.interactive;

import com.raku.fruitGame.enum_color.BananaEnumColor;
import com.raku.fruitGame.fruit.functionalClass.utility.FruitInterface;

/**
 * ばなな用のインタラクティブ実装。
 * 色型を BananaEnumColor に固定して、enum 制約を効かせます。
 */
public class BananaInteractive implements FruitInterface<BananaEnumColor> {
    /** 現在の色 */
    private BananaEnumColor color;

    /** 現在ロットの重量 */
    private long weight;

    /** 累積摂取量 */
    private long ate;

    public BananaInteractive(BananaEnumColor color, long weight, long ate) {
        this.color = color;
        this.weight = Math.max(0, weight);
        this.ate = Math.max(0, ate);
    }

    @Override
    public void setColor(BananaEnumColor color) {
        this.color = color;
    }

    @Override
    public void setAte(long ate) {
        this.ate = Math.max(0, ate);
    }

    @Override
    public void setWeight(long weight) {
        this.weight = Math.max(0, weight);
    }

    @Override
    public long getRemainingWeight() {
        return weight - ate;
    }

    @Override
    public long getAte() {
        return ate;
    }

    @Override
    public String getColor() {
        return color.getLabel();
    }
}
