package com.raku.fruitGame.fruit.functionalClass.utility;

/**
 * FruitGame で扱う果物オブジェクトの共通契約。
 *
 * @param <E> 色の型 (enum または String など)
 */
public interface FruitInterface<E> {
    /** 色を設定します。 */
    void setColor(E color);

    /** 累積摂取量を設定します。 */
    void setAte(long ate);

    /** 総重量を設定します。 */
    void setWeight(long weight);

    /** 残量を返します。通常は weight - ate です。 */
    long getRemainingWeight();

    /** 現在の累積摂取量を返します。 */
    long getAte();

    /** 表示用の色文字列を返します。 */
    String getColor();
}
