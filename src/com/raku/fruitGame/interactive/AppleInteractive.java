package com.raku.fruitGame.interactive;

import com.raku.fruitGame.enum_color.AppleEnumColor;
import com.raku.fruitGame.fruit.functionalClass.utility.FruitInterface;

/**
 * りんご用のインタラクティブ実装。
 * 色型を AppleEnumColor に固定して、型安全に扱います。
 */
public class AppleInteractive implements FruitInterface<AppleEnumColor> {
    /** 現在の色 */
    private AppleEnumColor color;

    /** 初期重量または再生成時重量 */
    private long weight;

    /** これまでに食べた合計量 */
    private long ate;

    public AppleInteractive(AppleEnumColor color, long weight, long ate) {
        this.color = color;
        this.weight = Math.max(0, weight);
        this.ate = Math.max(0, ate);
    }

    @Override
    public void setColor(AppleEnumColor color) {
        this.color = color;
    }

    @Override
    public void setAte(long ate) {
        // 負値が入っても 0 以上に丸めるガード
        this.ate = Math.max(0, ate);
    }

    @Override
    public void setWeight(long weight) {
        this.weight = Math.max(0, weight);
    }

    @Override
    public long getRemainingWeight() {
        // 残量 = 総重量 - 食べた量
        return weight - ate;
    }

    @Override
    public long getAte() {
        return ate;
    }

    @Override
    public String getColor() {
        // enum の表示ラベルを返す設計
        return color.getLabel();
    }
}
