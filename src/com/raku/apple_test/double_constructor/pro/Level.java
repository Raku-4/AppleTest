package com.raku.apple_test.double_constructor.pro;

/**
 * Level は「Minecraftのワールド（世界）を表す最小学習版」です。
 *<p></>
 * Minecraftでは、ゲーム内に複数の「世界」があります：
 * Overworld（オーバーワールド）：地表の世界
 * Nether（ネザー）：地獄
 * End（エンド）：エンドの次元
 *<p></>
 * Level はこれらの**「世界」を表すオブジェクト**です。
 * エンティティ（リンゴ、モンスターなど）は必ずどこかの Level 内に存在します。
 * <p></>
 * 例えば、Main クラスで、
 * Level orchard = new Level("果樹園");
 * <p></>
 * ここで作ったLevel は：
 * 名前：「果樹園」という世界
 * 役割：このレベル内でりんご が生成・存在する「世界」として機能する
 * <p></>
 * つまり、「Apple はこの果樹園というワールドの中に存在する」という関係です。
 * <p></>
 * なぜ必要か。
 * エンティティ（物など）を作るとき、「どこの世界で生成するのか」を指定する必要があります。
 * だからcreate(Level level) にlevel を渡します。
 */
public record Level(String name) {
    public Level {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名前が空です。");
        }
    }
}

