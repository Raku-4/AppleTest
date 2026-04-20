# Swing 学習パッケージ `com.raku.apple_test.swing`

このパッケージは、**Swing を初心者向けに分解して学ぶため**のサンプルです。

## 見る順番

1. `Main1Swing`
   - 入口クラスです。
   - `main()` から画面作成を始めます。

2. `SwingLessonFrame`
   - `JFrame` を作るクラスです。
   - 画面全体の枠を担当します。

3. `SwingBackgroundPanel`
   - `JPanel` と `paintComponent()` を学ぶクラスです。
   - 背景画像とブラックアウト演出を描きます。

4. `SwingControlPanel`
   - `FlowLayout` と `JButton` を学ぶクラスです。
   - 上部ボタンの並べ方とクリック処理を担当します。

5. `SwingImageButtonFactory`
   - 画像つきボタンをまとめて作る工場です。
   - 同じ見た目の処理を何度も書かずに済みます。

6. `SwingDemoTimer`
   - `Timer` を学ぶクラスです。
   - 一定間隔で `repaint()` を呼びます。

7. `SwingAssetLoader`
   - `assets` 配下の画像を読むための共通処理です。

## 実行方法

`Main1Swing` を起動してください。

## 学べること

- `JFrame` は「ウィンドウ本体」
- `JPanel` は「中身を描く場所」
- `JButton` は「押せるボタン」
- `Timer` は「一定間隔で処理を呼ぶ仕組み」
- `paintComponent()` は「Swing が呼ぶ描画メソッド」
- `BorderLayout` は「上下左右中央に置くレイアウト」
- `FlowLayout` は「左から順に並べるレイアウト」

## まず注目すると分かりやすい点

- ボタンを押すと何が起きるか
- 背景はどこで描いているか
- 画面がどうやって何度も更新されるか
- 画像読み込みに失敗したとき、どうフォールバックするか

