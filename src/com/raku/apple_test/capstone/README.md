# Capstone (統合課題ひな形)

`apple_test` / `banana_test` / `fruit_test` で学んだ内容を、1つの小さな実行可能サンプルにまとめたパッケージです。

## 学習の要点：Minecraft エンティティ登録パターン

ここで学ぶ `EntityFactory` パターンは、**Minecraft のエンティティ登録方式と同じ思想** です。

- **Minecraft では**、新しいエンティティ（例：CustomEntity）は、自身のコンストラクタ参照を登録してのみ生成可能です
- **本パッケージでも**、`Catalog` に登録されたコンストラクタ参照 (`AppleEntity::new` など) のみが生成許可される
- こうすることで、「どの型が許可されているか」をシステムが管理でき、**型安全性と制御可能性** の両立が実現されます

つまり、`create(...)` を通さない直接 `new` では、本当は何でも生成できますが、  
**登録側が「許可する型だけ」を `Catalog` に書く** ことで、意図しない型の生成を防ぐわけです。

## 含めた要素

- `EntityType<T>` + `EntityFactory<T>`（`AppleEntity::new` など）
- 果物/野菜カテゴリ（`enum FoodCategory`）
- `record`（`Zone`, `SpawnLog`, `Summary`）
- `List` と `Map` を使った集計
- `create(level)` と `new` の比較
- 例外処理（負の値を弾く）
- CSV の取り込み（`fruit_history.csv` のデータ行数を読む）

## 主要ファイル

- `EntityType.java`: 型情報 + 生成手順
- `Catalog.java`: 4エンティティ登録（apple, banana, onion, broccoli）
- `MainCapstone.java`: 生成・表示・集計を一気通しで実行
- `CapstoneSelfTest.java`: 最小自己テスト

## 解説コメント版の見どころ

以下のファイルに、`create(...)` と `new` の違い、`this` が何を表すか、
コンストラクタ参照がいつ実行されるか、Minecraft との共通性などの説明コメントを追加しました。

- `EntityFactory.java`
- `EntityType.java`
- `AbstractFood.java`
- `Catalog.java`
- `OnionEntity.java`
- `MainCapstone.java`

## 完成状態

すべてのTODOが解答済みです。以下を実行してください。

## 実行

このパッケージは現在、宿題版として主要箇所が `TODO` 化されています。
そのため実行時に `UnsupportedOperationException` が発生します。

## TODO の場所（優先順）

- `EntityType.java`: `labeledId()`, `create(Zone)`
- `Catalog.java`: 4種類の登録定数と `ALL`
- `MainCapstone.java`: `summarize(...)`
- `CsvSupport.java`: `countDataLines(...)`
- `CapstoneSelfTest.java`: 検証ロジック

## 実行（途中確認用）

```powershell
Push-Location "C:\Users\yoush\AppData\Roaming\.minecraft\Mods\AppleTest"
$files = Get-ChildItem "src\com\raku\apple_test\capstone\*.java" | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d target\classes $files
java -cp target\classes com.raku.apple_test.capstone.MainCapstone
java -cp target\classes com.raku.apple_test.capstone.CapstoneSelfTest
Pop-Location
```

