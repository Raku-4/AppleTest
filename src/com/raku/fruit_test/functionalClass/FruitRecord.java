package com.raku.fruit_test.functionalClass;

/**
 * 果物の生成履歴を保持するデータモデル
 */
public record FruitRecord(String fruitName, String color, long weight) {
}

/*
 * プログラムを書いていると、「計算ロジックなどは持たず、ただデータを運ぶだけの箱」が必要な場面がよくあります。
 * <p>
 * 普通のクラスの場合: フィールド、コンストラクタ、ゲッター、toString、equals などをすべて手書きする必要があり、コードが非常に長くなります。
 * record の場合: 1行書くだけで、Javaがこれらすべてを自動的に、かつ「安全な形」で生成してくれます。
 */
