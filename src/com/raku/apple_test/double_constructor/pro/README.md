# double_constructor/pro

学習用に `EntityType<? extends X>, Level` パターンを最小再現したサンプルです。

## ファイル
- `EntityType.java`: `EntityFactory<T>` と `create(Level)` を持つ登録型
- `FruitType.java`: `FruitEntity` 専用の型
- `FruitEntity.java`: 基底エンティティ (`type` と `level` を保持)
- `ApplePro.java`: 必須コンストラクタ + 便利コンストラクタを持つ具体クラス
- `FruitCatalog.java`: 学習用レジストリ
- `Main.java`: 実行確認

## 実行例
PowerShell でワークスペースルートから実行:

```powershell
javac -encoding UTF-8 src/test/java/com/raku/apple_test/double_constructor/pro/*.java
java -cp src/test/java com.raku.apple_test.double_constructor.pro.Main
```

