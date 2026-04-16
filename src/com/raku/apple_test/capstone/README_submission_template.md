# Capstone 提出用READMEテンプレート

このファイルは提出時に埋めるためのテンプレートです。
`[ ]` や `<...>` を自分の内容に置き換えてください。

## 1. 提出情報

- 提出日: `<2026-04-12>`
- 氏名/ハンドル: `<raku>`
- 対象パッケージ: `com.raku.*;
- 目的(1行): `<Minecraft のMod開発に向けた知識の集積と実装演習>`

## 2. 実行手順(再現性)

前提:
- Java: `<例: 17>`
- 実行場所: プロジェクトルート

手順:

```powershell
javac -d . src/com/raku/apple_test/*.java
```

実行結果:
- C:\Users\yoush\.jdks\openjdk-24.0.2+12-54\bin\java.exe "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2025.3\lib\idea_rt.jar=56026" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath C:\Users\yoush\AppData\Roaming\.minecraft\Mods\AppleTest\out\production\AppleTest;C:\Users\yoush\.m2\repository\org\jetbrains\annotations\26.0.2\annotations-26.0.2.jar;C:\Users\yoush\.m2\repository\junit\junit\4.13.1\junit-4.13.1.jar;C:\Users\yoush\.m2\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;C:\Users\yoush\.m2\repository\org\junit\jupiter\junit-jupiter\5.14.0\junit-jupiter-5.14.0.jar;C:\Users\yoush\.m2\repository\org\junit\jupiter\junit-jupiter-api\5.14.0\junit-jupiter-api-5.14.0.jar;C:\Users\yoush\.m2\repository\org\opentest4j\opentest4j\1.3.0\opentest4j-1.3.0.jar;C:\Users\yoush\.m2\repository\org\junit\platform\junit-platform-commons\1.14.0\junit-platform-commons-1.14.0.jar;C:\Users\yoush\.m2\repository\org\apiguardian\apiguardian-api\1.1.2\apiguardian-api-1.1.2.jar;C:\Users\yoush\.m2\repository\org\junit\jupiter\junit-jupiter-params\5.14.0\junit-jupiter-params-5.14.0.jar;C:\Users\yoush\.m2\repository\org\junit\jupiter\junit-jupiter-engine\5.14.0\junit-jupiter-engine-5.14.0.jar;C:\Users\yoush\.m2\repository\org\junit\platform\junit-platform-engine\1.14.0\junit-platform-engine-1.14.0.jar com.raku.apple_test.interactive.MainInteractive
  前回の履歴を5件読み込みました。
  最初に食べたい果物を入力してください（りんご / ばなな）
  （りんご / ばなな）>> りんご
  新しく 赤 色の 150g の りんご を作りました。

食べたい量を入力してください。今現在の果物に関する生成
リスト表示は "list"、全ての果物に関する生成リスト表示は"listAll"、
やめる場合は "cancel" を入力してください。

食べたい量 >> listall

これまでに生成された りんご の情報リスト：
色：RED  重さ：150g

これまでに生成された ばなな の情報リスト：
色：YELLOW  重さ：110g

これまでに生成された いちご の情報リスト：
色：青  重さ：999g

これまでに生成された さくらんぼ の情報リスト：
色：黒  重さ：5000g

これまでに生成された ちょこ の情報リスト：
色：あかいろ  重さ：100g

食べたい量 >> 1

りんごは残り 149g です。

食べたい量 >> 1

りんごは残り 148g です。

食べたい量 >> 1

りんごは残り 147g です。

食べたい量 >> 1

りんごは残り 146g です。

食べたい量 >> 11

りんごは残り 135g です。

食べたい量 >> 11111

あなたが食べたかった量が 10976g 足りなかったようです。
まだ食べたいですか？（y/n） y

今度は何を食べたいですか？（りんご / ばなな / 新規名）

果物名 >> ばなな
何色で作りたいですか（BananaEnumColor：例 YELLOW）>> YELLOW
どのくらいの重さで作りたいですか（g）>> 1500

新たに 黄色 色の 1500g の ばなな を作りました。

食べたい量 >> 123

ばななは残り 1377g です。

食べたい量 >> 12343

あなたが食べたかった量が 10966g 足りなかったようです。
まだ食べたいですか？（y/n） y

今度は何を食べたいですか？（りんご / ばなな / 新規名）

果物名 >> みかｎ
何色で作りたいですか。 >> cancel
どのくらいの重さで作りたいですか（g）>> cancel
正しい整数値（0以上）を入力してください。
どのくらいの重さで作りたいですか（g）>> 100
あ
なたの考えた新たな果物「みかｎ」を作りました。

色：cancel / 重さ：100g

食べたい量 >> 1000

あなたが食べたかった量が 900g 足りなかったようです。
まだ食べたいですか？（y/n） 10
y / n で入力してください。
まだ食べたいですか？（y/n） o
y / n で入力してください。
まだ食べたいですか？（y/n） n

おしまい。

プロセスは終了コード 0 で終了しました


## 3. 設計の要点

### 3.1 なぜこの設計にしたか
- `EntityType<T>`: `<何を型安全にしたかったか>`
- `Catalog`: `<登録制にした理由>`
- `Summary`: `<集計責務を分離した理由>`
- `CsvSupport`: `<永続化責務を分離した理由>`

### 3.2 トレードオフ
- メリット: `<例: 追加エンティティ時の変更点が明確>`
- デメリット: `<例: クラス数が増える>`
- 今後の改善: `<例: SaveCSV命名の統一>`

## 4. テスト結果(証拠)

- 実施日: `<YYYY-MM-DD>`
- 実施者: `<your name>`
- 対象: `CapstoneSelfTest`, 手動確認

結果サマリ:
- [ ] 生成テスト
- [ ] 集計テスト
- [ ] CSVラウンドトリップ
- [ ] 重複排除
- [ ] 不正値系(例外)

失敗ケースがあれば記録:
- ケース名: `<...>`
- 原因: `<...>`
- 対応: `<...>`

## 5. 既知の制約

- 制約1: `<例: グローバルな履歴(Map)を使っている>`
- 制約2: `<例: 同時実行は未考慮>`
- 制約3: `<例: CSVフォーマットに強く依存>`

## 6. 変更履歴

- v1: `<初回提出内容>`
- v2: `<修正内容>`

## 7. 自己評価

- 良かった点(2つ):
  - `<...>`
  - `<...>`
- 次回改善(2つ):
  - `<...>`
  - `<...>`