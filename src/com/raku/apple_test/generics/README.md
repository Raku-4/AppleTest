# generics 学習メモ

このパッケージは、Java のジェネリクスを「りんご」と「ばなな」で学ぶためのサンプルです。

## 見るポイント

- `GenericsApple<TColor>` / `GenericsBanana<TColor>`
  - 色の型を型引数で受け取る
- `RedApple<M extends GenericsApple<?>>` / `BlueBanana<M extends GenericsBanana<?>>`
  - 上限制約 (`extends`) の例
- `MainGenerics`
  - `List<? extends ...>` と汎用メソッドの例

## 実行

`MainGenerics` を起動すると、
- 個別オブジェクトの表示
- `String` 以外の色型の例
- `List` とワイルドカードの一覧表示

が順番に出ます。

