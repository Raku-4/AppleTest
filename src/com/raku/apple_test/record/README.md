# Record Learning Notes

このフォルダは Java `record` の学習用です。

## Files

- `AppleRecord.java`: record の本体（compact constructor, validation, 通常メソッド）
- `AppleDataClass.java`: 同じデータを通常 class で表した比較用
- `MainRecord.java`: 動作確認用のエントリポイント

## What to Observe

1. `record` は `equals/hashCode/toString/accessor` を自動生成する
2. compact constructor で入力検証や補正ができる
3. 通常 class は同等機能を手書きするためコード量が増える

