package com.raku.fruitGame.fruit.functionalClass;

/**
 * 果物生成履歴の1件を表す不変データ。
 *
 * <p>record を使うことで、ゲッター/equals/hashCode/toString を
 * 定型コードなしで安全に利用できます。</p>
 *
 * @param fruitName 果物名
 * @param color 生成時の色
 * @param weight 生成時の重さ(g)
 */
public record FruitRecord(
		String fruitName,
		String color,
		long weight,
		String taste,
		String maturity,
		long elapsedSeconds,
		int treeId
) {
}
