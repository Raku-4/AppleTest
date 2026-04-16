# amateur パッケージ - 学習用の基本形

## ファイル構成

```
src/com/raku/apple_test/double_constructor/amateur/
  ├── SimpleEntityType.java   ... EntityType を直接継承するだけの最小形
  ├── SimpleApple.java         ... 簡単なエンティティ（Onion/Broccoli の前に理解する）
  ├── SimpleCatalog.java       ... 登録（FruitCatalog/VegetableCatalog の前段階）
  └── Main.java                ... 実行例
```

## 学習の流れ

### pro パッケージとの違い

| 項目 | amateur | pro |
|------|---------|-----|
| 分類 | なし | FruitType / VegetableType |
| マーク | [?] | [F] / [V] |
| 複雑さ | シンプル | カテゴリ分けで安全性向上 |

### 実装パターン

**amateur（シンプル）:**
```java
SimpleEntityType.of("apple", SimpleApple::new)
```

**pro（分類あり）:**
```java
FruitType.of("apple", Apple::new)    // [F] マーク
VegetableType.of("onion", Onion::new)   // [V] マーク
```

## 実行方法

### IDE ビルド後（推奨）
IDE の `Build` ボタンでプロジェクト全体をビルドしてから、以下で実行：

```bash
# IDE ターミナルから
java -cp target/classes com.raku.apple_test.double_constructor.amateur.Main
```

### 期待される出力
```
SimpleApple{type=[?]apple, level=畑, color='赤', sugarBrix=12}
SimpleApple{type=[?]apple, level=畑, color='青', sugarBrix=14}
appleFactory type: [?]apple
appleFactory level: 畑
```

## 理解のポイント

1. `SimpleEntityType` は `EntityType` を直接継承
2. `pro` パッケージの分類（Fruit/Vegetable）がない
3. `SimpleCatalog` で `SimpleApple::new` を登録
4. `APPLE.create(level)` で実際のインスタンス生成

この流れを理解してから `pro` パッケージの `FruitType`/`VegetableType` を見ると、
「ああ、分類を足しただけか」と腹落ちします。

