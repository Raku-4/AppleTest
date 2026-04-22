/**
 * Code Written By Raku.
 * Code's Description Written By GitHub Copilot.
 */
package com.raku.apple_test.swing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * assets 配下の画像を読み込むための小さな道具箱です。
 *
 * <p>【このクラスの役割】</p>
 *
 * <p>このクラスは、画像ファイルを読み込む「共通処理」をまとめたものです。</p>
 *
 * <p>【何をするか】
 * 1. 画像ファイルパスを受け取る
 * 2. classpath（ビルド時に入るもの）から探す
 * 3. 見つからなければ src/resources からファイルシステムで探す
 * 4. 見つかった画像をキャッシュに保存して、以降は同じファイルなら
 *    キャッシュから返す</p>
 *
 * <p>【なぜこうするのか】
 * - 「毎回ファイルを読む」と無駄が大きい
 * - 複数回使う画像なら、最初に読んだら 2 回目以降は保存済みのを返すほうが速い
 * - 同じ画像を何度も読み込みしない最適化が「キャッシング」です</p>
 *
 * <p>【public static Image load(...) だけ使う】
 * 他のメソッドは private です。外から直接呼ぶことはありません。</p>
 */
public final class SwingAssetLoader {

    /**
     * 【CACHE ってなに？】
     *
     * <p>`Map<String, Image>` は、「画像パスの文字列 → 読み込んだ画像」の
     * 対応表です。</p>
     *
     * <p>例）<br>
     * キー: "assets/fruitGame/textures/main_menu/main_menu.png"<br>
     * 値: その画像ファイルを読み込んだ Image オブジェクト</p>
     *
     * <p>【ConcurrentHashMap ってなに？】<br>
     * 普通の HashMap は、複数スレッドからアクセスされると壊れることがあります。</p>
     *
     * <p>ConcurrentHashMap は、複数スレッドから同時にアクセスされても
     * 安全に動くマップです。</p>
     *
     * <p>Swing では、ボタンクリック、画面描画、タイマーなどが
     * 別々のスレッドで動くことがあるため、
     * 安全なマップを使うのが大事です。</p>
     */
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    /**
     * 【プライベートコンストラクタの意味】
     *
     * <p>このクラスは new できません。<br>
     * 理由：このクラスは「便利な道具」であって、
     * インスタンス化する意味がないからです。</p>
     *
     * <p>例）Calculator 計算機は、「new Calculator()」してから
     * 使うのではなく、Calculator.add(2, 3) みたいに
     * 直接メソッドを呼ぶほうが自然です。</p>
     *
     * <p>このパターンを「ユーティリティクラス」と呼びます。</p>
     */
    private SwingAssetLoader() {
    }

    /**
     * 【このメソッドが外から呼ぶ唯一のメソッド】
     *
     * <p>使い方: `Image img = SwingAssetLoader.load("assets/.../image.png");`</p>
     *
     * <p>【computeIfAbsent(...) の動作】</p>
     *
     * <p>「もし CACHE に resourcePath がなかったら、loadUncached() を呼んで
     * その結果を CACHE に入れて、返す。
     * もしあったら、その結果を返す」</p>
     *
     * <p>つまり：<br>
     * 1 回目に "main_menu.png" を load(...) する<br>
     *   → CACHE にない<br>
     *   → loadUncached() でファイルから読み込み<br>
     *   → CACHE に保存<br>
     *   → 返す</p>
     *
     * <p>2 回目に "main_menu.png" を load(...) する<br>
     *   → CACHE にある<br>
     *   → ファイルを読まず、キャッシュから返す（高速！）</p>
     *
     * <p>【メリット】<br>
     * 同じ画像を何度も読み込まないから、パフォーマンスが良くなります。</p>
     */
    public static Image load(@Nullable String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            // null や空文字列が来たら、読み込みようがないので null を返す
            return null;
        }

        return CACHE.computeIfAbsent(resourcePath, SwingAssetLoader::loadUncached);
    }

    /**
     * 【このメソッドの役割】
     *
     * <p>キャッシュを無視して、実際にファイルを読み込みます。</p>
     *
     * <p>「classpath から探す」と「ファイルシステムから探す」の
     * 2 つを試します。
     * 最初に見つかったほうを返します。</p>
     */
    private static @Nullable Image loadUncached(@NotNull String resourcePath) {
        Image image = loadFromClasspath(resourcePath);
        // classpath から探す試みが成功したか？
        if (image != null) {
            // 見つかった！ これを返す
            return image;
        }
        // 見つからなかった。ファイルシステムから探す
        return loadFromFileSystem(resourcePath);
    }

    /**
     * 【このメソッドの役割】
     *
     * <p>classpath（ビルドされたクラスパス）から画像を探します。</p>
     *
     * <p>【@NotNull と @Nullable の意味】</p>
     *
     * <p>@NotNull：「このパラメータ/戻り値は null ではない」という宣言<br>
     *            IDE が null チェック漏れを警告してくれます</p>
     *
     * <p>@Nullable：「このメソッドの戻り値は null かもしれない」という宣言<br>
     *             呼び出し側で null チェックする必要があります</p>
     */
    private static @Nullable Image loadFromClasspath(@NotNull String resourcePath) {
        // 【なぜ先頭の "/" を削除するのか？】
        // classpath 上のファイルパスは "/" で始まらないのが慣例です。
        // 例）"assets/fruitGame/..." という形で使います。
        String normalized = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;

        try (InputStream stream = SwingAssetLoader.class.getClassLoader().getResourceAsStream(normalized)) {
            // 【getResourceAsStream(...) とは】
            // classpath 内のファイルをストリームで開きます。
            // jar ファイル内や、IDE の実行時設定からも読めます。

            if (stream == null) {
                // ストリームが null = ファイルが見つからなかった
                return null;
            }
            // ストリームから画像を読み込みます
            return ImageIO.read(stream);
        } catch (IOException ignored) {
            // 【IOException を無視する理由】
            // ファイルを読むときに IO エラーが起きた
            //（ファイル権限がない、破損している、など）
            // この場合、「読めなかった = null を返す」と扱うのが
            // このクラスの設計なので、例外を無視して null を返します。
            return null;
        }
    }

    /**
     * 【このメソッドの役割】
     *
     * <p>ファイルシステムから画像を直接読み込みます。<br>
     * （IDE でデバッグ実行しているときなど、
     *  classpath に見つからない場合の予備策として機能します）</p>
     */
    private static @Nullable Image loadFromFileSystem(@NotNull String resourcePath) {
        // 【File.separator の意味】
        //
        // ファイルパスの区切り文字は、OS によって違います：
        // - Windows: "\"（バックスラッシュ）
        // - Linux/Mac: "/"（スラッシュ）
        //
        // コード内では "/" で統一して書きますが、
        // ファイルシステムに渡すときは OS に合わせて変換する必要があります。
        //
        // 例）
        // 入力: "assets/fruitGame/textures/main_menu.png"
        //
        // Windows の場合:
        //   File.separator = "\"
        //   → "assets\fruitGame\textures\main_menu.png"
        //
        // Linux の場合:
        //   File.separator = "/"
        //   → "assets/fruitGame/textures/main_menu.png" （変わらない）

        String normalized = resourcePath.replace("/", File.separator);

        // 【ファイルパスの構築】
        // "src/resources/" 配下から探します。
        //
        // 例）
        // 最終的には "src\resources\assets\fruitGame\textures\main_menu.png"
        // のようなパスを作ります（Windows の場合）

        File file = new File("src" + File.separator + "resources" + File.separator + normalized);
        if (!file.exists()) {
            // ファイルが存在しない
            return null;
        }
        // ファイルが存在するなら、読み込みを試みる
        try {
            return ImageIO.read(file);
        } catch (IOException ignored) {
            // ファイル読み込み時の IO エラー（権限不足、破損、など）
            // これも例外を無視して null を返します
            return null;
        }
    }
}

