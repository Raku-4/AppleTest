package com.raku.apple_test.matrix;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.ToIntFunction;
// import java.util.function.ToIntFunction;

@SuppressWarnings({"FieldMayBeFinal", "Convert2MethodRef"})
public class AppleMatrix {
    // 配列サイズ
    private static final int ROWS = 4;
    private static final int COLS = 10;

    // リンゴの重さを格納
    private static final int[][] apples = new int[ROWS][COLS];

    // ビンの上限（≤150, ≤200, ≤250, ≤300）
    private static final int[] binEdges = {150, 200, 250, 300};
    private static final int[] tetragon_root = {11, 20, 29, 38}; // 棒を出す場所（列）

    // ビンごとの個数
    private static final int[] counts = new int[binEdges.length];

    // 必要なら実データも保持（任意）
    /*
    private static final List<List<Integer>> bins = Arrays.asList(
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
    );
     */

    private static final Random rand = new Random(); // ランダムな重さのりんごを作るためにクラスRandom をインスタンス化

    // 下地グラフ
    private static String @NotNull [] graph = {
            "                                               ",
            "  amt                                          ",
            "   |                                           ",
            "20 |                                           ",
            "   |                                           ",
            "   |                                           ",
            "   |                                           ",
            "   |                                           ",
            "15 |                                           ",
            "   |                                           ",
            "   |                                           ",
            "   |                                           ",
            "   |                                           ",
            "10 |                                           ",
            "   |                                           ",
            "   |                                           ",
            "   |                                           ",
            "   |                                           ",
            " 5 |                                           ",
            "   |                                           ",
            "   |                                           ",
            "   |                                           ",
            " __|_____________________________________ g    ",
            " 0 |     ~150     ~200     ~250     ~300       ",
            "                                               ",
            "  g : gramme  amt : amount                     "

            // 人間: 改行を入れて並べたとき、視覚的に「行」と「列」という二次元の表として捉えられます。
            // コンピュータ: メモリ上では、ただ25個の独立した文字列オブジェクトが順番に並んでいる（一次元配列）として扱われます。
            // それぞれの要素（文字列）は、内部で文字の配列（char[]）を持っていますが、外から見ると「長さ 47 の単一のオブジェクト」として振る舞います。
            // 並べ方によって二次元に見えるが、実際は ""47文字をまとめて持つ"" 要素（行）が25個並んでいる一次元配列という...なんとも面白いものですね。
    };

    private static char[][] grid; // ヒストグラム描画用

    /** ① 重さのあるリンゴの行列を作る */
    public static void addApples() {
        // 例：120〜320 g に設定して全ビンが出現しうるようにする
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                apples[i][j] = 100 + rand.nextInt(201); // 120..320
            }
        }
    }

    /** ② 重さ順（ビン）に分類する */
    public static void arrange() {
        Arrays.fill(counts, 0);

        //for (List<Integer> b : bins) b.clear();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int weight = apples[i][j];
                int k = binIndex(weight); // どのビンか
                if (k >= 0) {
                    counts[k]++;
                    //bins.get(k).add(weight); // 実データを持ちたい場合
                }
                // 300 を超える場合（>300）はここでは捨てるか、別ビンを用意しても良い
            }
        }
    }

    /** 重さ w が属するビンのインデックスを返す（なければ -1） */
    private static int binIndex(int w) {
        for (int k = 0; k < binEdges.length; k++) {
            if (w <= binEdges[k]) return k;
        }
        return -1; // すべての上限を超えた場合
    }

    /** ③ 表示の準備（下地グラフにヒストグラムを重ねる） */
    public static void histogram() {
        // 下地グラフの行・列数を確定
        int rows = graph.length;
        // 各行（文字列）の長さを調べ、その最大値をグラフの列数（cols）とする
        int cols = Arrays.stream(graph).mapToInt(String::length).max().orElse(0);

        //メソッド参照を使わずに書くと、
        /*
        cols = Arrays.stream(graph).mapToInt((String s) -> s.length()).max().orElse(0);
         */

        // ラムダ式を使わすに書くと、
        /*
        cols = Arrays.stream(graph).mapToInt(new ToIntFunction<String>() {
            @Override
            public int applyAsInt(String value) {
                return value.length();
            }
        }).max().orElse(0);
         */


        // 上記でやっている、一見良くわからない処理は、以下のような処理と同じ。
        /*

        int maxLen = 0;
        for (String line : graph) {
            if (line.length() > maxLen) {
                maxLen = line.length();
            }
        }
        cols = maxLen;
        */

        /*

        今回は、この「ストリームAPI」と呼ばれる一行のコードが持つ"真の価値"が、この単純な
        最大値探索では見えにくいですが本来の最大のメリットは、"複雑なパイプライン処理"と"並列化"の容易さにあります。

        もし処理が以下のように複雑になったらどうでしょうか？

        「graph の【行】のうち、空白文字で始まらない【行】だけを対象とし、その文字列の長さを取得した後、その長さが偶数のものだけを残して、最後に最大値を求めよ。但し、graph は一万の【行】を用意し、1秒で処理を終わらせること。」

        ストリーム API を使って書くと、
        */

        // cols = Arrays.stream(graph).parallel().filter((String line) -> !line.startsWith(" ")).mapToInt((String s) -> s.length()).filter((int len) -> len % 2 == 0).max().orElse(0);

        /*
        最大値 = graph の文字列（行）をストリーム型に変換、その後.filter() 内でstartsWith(" ") により
        空白文字で始まらない【行】を対象にしてストリームする、そして、graph 内の【行】に対する処理を並列に行うために.parallel() を追加する（処理速度向上のため）。
        その後、対象の文字列（行）をString s に代入、代入された文字列の長さをString のインスタンスメソッド length() で取得し、返す。
        その後、.filter() 内で得た値を2 でわってあまりがないか(偶数か)どうかを計算し、偶数ならその値の結果を.max() に代入し、代入された値の中から最大値を調べて、その結果を.orElse(0) を用いて
        OptionalInt 型からint 型に変換してcols に代入する。
        結果、「空白文字で始まらず且つ、偶数長である行の中から最大の長さの値を返す」という流れが、この
        ストリームAPI によって、まるで一本のパイプラインを通って行くように処理が行われ、コードもそのような手順で
        処理が進んでいることが分かりやすく明記されています。
        このように、コードが一本のパイプのように書かれています。
                       、
        もしこの仕組みをfor 文で書くと、

        for (String line : graph) {
            if (!line.startsWith(" ")) {
                int currentLength = line.length();
                if (currentLength % 2 == 0) {
                    if (currentLength > maxLen) {
                        maxLen = currentLength;
                    }
                }
            }
        }
        cols = maxLen;

        for 文では速度が遅くなる可能性もあり、バグが生まれる可能性も高まります。
        それよりは、Java が最初から用意しているものを利用した方が、処理速度も上がりやすくなり、バグが起きる確率は格段に減ります。
         */

        // グリッド確保＆空白で初期化
        grid = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            Arrays.fill(grid[r], ' ');
            char[] row = graph[r].toCharArray();
            System.arraycopy(row, 0, grid[r], 0, row.length);
        }

        // 各ビンの高さ分だけ '■' を縦に積む（1 個 = 1 高さ）
        for (int k = 0; k < counts.length; k++) {
            for (int y = 1; y <= counts[k]; y++) {
                // y=1 がcount[k] の値を上回る直前までつまり、count のk 番目のインデックスの値がy を終わまわる直前までｙを増やすつまり、■ を一行ずつずらして積み上げる
                // 超えたら次のcount のインデックスの値を参照し、同じことを行う。
                // これらは、k の値がcount の要素数を上回るまで続く。
                // 但しその処理は、以下のメソッドに引数を渡すことで行われる。

                setCharAxis(grid, tetragon_root[k], y);
                //  setCharAxis(配列名、棒を出す根元の場所、棒の上向きの高さ)
            }
        }
    }

    /*
    座標系の理解

    数学的坐標とは、皆さんがxy 坐標について学校で学ぶように、左下に原点（0,0）を持ち、縦軸y の向きは上向きに増加します。
    これは人間がグラフのデータを考えるときに用いられます。

    対して画面坐標では、原点（0,0）の位置が左上にあり、縦軸y の値は下向きに増加します。
    これはコンピューターが配列や画面を出力するときに用いられます。

    数学的坐標では、行の数値(インデックスの番号 + 1) の値が大きくなるごとに上がっていくが、
    画面坐標では、行の数値(インデックスの番号 + 1) の値が大きくなるごとに下に下がっていく。

    以下の二つのメソッドは、人間の直感的な坐標の間隔とコンピューターとのギャップを乗り越えるためのものです。
     */

    /**このメソッドsetCharAxisは、 setCharScreen に値を渡す前に、"数学的座標"（左下が(0,0)) を"画面坐標"(左上が(0,0)) に変換するもの */
    private static void setCharAxis(char[] @NotNull [] g, int x, int y) {
        int rows = g.length;        // グリッドの全行数つまり、高さを取得する。
        int rowFromTop = rows - y; // rowFromTop という名前は、
        /* 『「数学的坐標」にとっての"最下行"を、「画面坐標」にとっての"最上行"に変える 』 という意味で付けられました。
         これにより、setCharScreen に渡す値は画面坐標にとって『数学坐標にとっての「最上行」』になります。

        rows は、グリッド配列の行の長さ（今回は25）を意味する。
        rows - y で、行数から、棒の高さ(引数 y) を引く。これは数学的座標にとっては「行の上に向かって伸びる棒」になります。
        一方、画面坐標にとっては『数学的坐標にとっての「下向きに伸びる棒」』になります。

        例で、rows(行つまり、高さ) が 25 とする。
        rows - y の式を具体的な数値で見て行こう。


        例；数学的坐標にとっての「最下行」（棒の高さ0）
        ・数学的坐標: y = 0
        但しこれは、画面坐標にとって、最上行（縦の座標が反転しているから。
        ・計算; 25 - 0 = 25
        これは、数学的坐標にとっては「最上行」だが、画面坐標にとっては「数学的坐標にとっての最下行」。


        例２；数学的坐標にとっての「4行目」（高さ4の棒が伸びるイメージ）
        ・数学的坐標: y = 4
        但しこれは、画面坐標にとって「数学坐標にとっての21行目」
        ・計算: 25 - y = 21
        これは、数学坐標にとっては「21行目」だが、画面坐標にとっては「数学的坐標にとっての4行目」
        */

        setCharScreen(g, rowFromTop - 1, x);
        // setCharScreen(配列名、画面坐標上のインデックスの番号、棒が伸びる根本)
        // setCharScreen に引数を渡す際に、第二引数は「行」ではなく「インデックス」を渡す必要がある。その補正をするために-1 をしている。
        // これにより、画面坐標にとってのインデックスが、数学的坐標にとっての最下行(rows - 1) を基準とした正確な位置になる。
        }

    /** 画面座標（左上が(0,0)) で一文字を置く */
    public static void setCharScreen(char[]  @NotNull [] g, int row, int col) {
        if (row < 0 || row >= g.length) return;
        if (col < 0 || col >= g[row].length) return;
        // どちらも範囲外なら操作を行わずにreturn する。

        g[row - 2][col] = '■';
        // row - 2 のオフセットは、グラフの軸線（0のライン）の上に棒の最初のブロック (y=1) が来るよう、
        // 画面上での位置を微調整するためのものです。
        // -2 なのは、画面坐標にとって、行の値が減るというのは上に上がること。 数学的坐標にとっては+2 をして上に上がるのと同じことです。
    }

    /** 印字 */
    public static void printer() {
        // grid の行数分だけループ
        for (int i = 0; i < grid.length; i++) {

            char[] r = grid[i]; // 二次元配列grid の一行を、一次元配列 r に丸ごと代する。

            System.out.println(new String(r));
            // char[] 型をString に変換するつまり、一次元配列という形で分割されていた文字の列を
            // 一つの文字の塊に変えます。その後にprintln で出力を行います。
        }
    }

    /** 確認用：カウントの表示 */
    public static void printCounts() {
        System.out.println("\n150g 以下: " + counts[0] +
                "  ~200g 以下: " + counts[1] +
                "  ~250g 以下: " + counts[2] +
                "  ~300g 以下: " + counts[3]
        );
    }

    /*
    String[] arrays = {"A","B","C"};
    List<String> list = Arrays.asList("A", "B", "C");
    学習用に残しておく。
     */
}