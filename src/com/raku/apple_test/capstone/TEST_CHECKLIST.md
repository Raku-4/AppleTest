# Capstone テスト観点チェックリスト

提出前にこのチェックを埋めると、品質の説明がしやすくなります。

## 0. 実施情報

- 実施日: `<YYYY-MM-DD>`
- 実施者: `<your name>`
- 対象コード: `com.raku.apple_test.capstone`
- 実行環境: `<IDE/Java version>`

## 1. 生成(EntityType/Catalog)

- [ ] `Catalog.APPLE.create(...)` で `AppleEntity` が生成される
- [ ] `Catalog.BANANA.create(...)` で `BananaEntity` が生成される
- [ ] `Catalog.ONION.create(...)` で `OnionEntity` が生成される
- [ ] `Catalog.BROCCOLI.create(...)` で `BroccoliEntity` が生成される
- [ ] `labeledId()` が想定どおり(`"[F]apple"` 等)

証跡メモ: `<出力または確認方法>`

## 2. 集計(Summary)

- [ ] `countByCategory` の件数が期待値どおり
- [ ] `averageMetricByType` の平均値が期待値どおり
- [ ] データ0件時に例外なく動く

証跡メモ: `<入力データと結果>`

## 3. CSV保存/読込(CsvSupport)

- [ ] `saveCSV` 後にファイルが作成される
- [ ] `loadCSV` 後に履歴が復元される
- [ ] CSVのヘッダ行が正しい
- [ ] 二重引用符やカンマを含む値を壊さない
- [ ] 同一データが重複保存されない

証跡メモ: `<ファイル内容またはassert結果>`

## 4. 例外/境界値

- [ ] 負値など不正入力で例外または防御が働く
- [ ] 空ファイル/存在しないCSVで異常終了しない
- [ ] `null` 相当入力時の挙動が把握できている

証跡メモ: `<どの入力でどうなったか>`

## 5. 回帰確認

- [ ] 変更前に通っていた `CapstoneSelfTest` が変更後も通る
- [ ] 既存の表示/保存仕様を壊していない

証跡メモ: `<実施した回帰テスト>`

## 6. 提出判定

- [ ] 重大不具合なし
- [ ] READMEに実行手順を記載済み
- [ ] 未対応課題を既知制約として明記済み

最終コメント(3行):
- `<1行目>`
- `<2行目>`
- `<3行目>`

