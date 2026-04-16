package com.raku.apple_test.double_constructor.pro;

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import com.raku.apple_test.double_constructor.pro.food.fruit.FruitCatalog;
import com.raku.apple_test.double_constructor.pro.food.fruit.fruits.Apple;
import com.raku.apple_test.double_constructor.pro.food.fruit.fruits.Banana;
import com.raku.apple_test.double_constructor.pro.food.vegetable.VegetableCatalog;
import com.raku.apple_test.double_constructor.pro.food.vegetable.vegetables.Broccoli;
import com.raku.apple_test.double_constructor.pro.food.vegetable.vegetables.Onion;

/**
 * このクラスは、Minecraft を起動するような、疑似的クラスと思って思ってください。
 * このpro というパッケージにあるクラスで、コンストラクタの変数の意味について以下に示しておきます。
 * <p></>
 * id は登録名です。例：apple , banana
 * level はエンティティの生成場所を示します。例：果樹園、畑
 * type はそのエンティティの型を示します。例：FruitType<Apple>
 * factory はエンティティの生成方法そのものを示します。例；Apple::new
 */

public class Main {
    public static void main(String[] args) {
        Level orchard = new Level("果樹園");
        Level field = new Level("畑");

        // 1) EntityType 側の factory 経由で生成（EntityType<? extends X>, Level を使用）
        Apple AppleGreen = FruitCatalog.APPLE.create(orchard);
        AppleGreen.setColor(AppleEnumColor.GREEN);
        AppleGreen.setSugarBrix(12);

        // 2) 便利コンストラクタ経由で生成（this(...) で必須コンストラクタに委譲）
        Apple AppleRed = new Apple(orchard, AppleEnumColor.RED, 14); // FruitCatalog はすでに元のコンストラクタに代入済み

        System.out.println(AppleGreen.describe());
        System.out.println(AppleRed.describe());



        Banana BananaBlue = FruitCatalog.BANANA.create(orchard);
        BananaBlue.setColor("青");
        BananaBlue.setSugarBrix(12);
        BananaBlue.setKcal(100);

        Banana BananaYellow = new Banana(orchard, "黄色", 15);

        System.out.println(BananaBlue.describe());
        System.out.println(BananaYellow.describe());


        // new Onion(...) の戻り値: Onion インスタンス
        Onion OnionWhite = new Onion(field, "エシャロット", "白", 200);
        Broccoli BroccoliGreen = new Broccoli(field, "鮮やか", "緑", "中", 100);

        // 入力: level=field
        // 戻り値: Onion (内部で Onion::new が呼ばれる)
        Onion OnionPurple = VegetableCatalog.ONION.create(field);
        OnionPurple.setColor("紫");
        OnionPurple.setWeight(150);
        OnionPurple.setKind("チャイル");

        Broccoli BroccoliWhite = VegetableCatalog.BROCCOLI.create(field);
        BroccoliWhite.setColor("白");
        BroccoliWhite.setWeight(150);
        BroccoliWhite.setVividness("とても鮮やか");

        System.out.println(OnionWhite.describe());
        System.out.println(BroccoliGreen.describe());

        // getType() の戻り値: EntityType<? extends AllEntity>。この例では ONION の型情報。
        System.out.println(OnionWhite.getType().getLabeledId());
        // getLevel().name() の戻り値: String。ここでは "畑"。
        System.out.println(OnionWhite.getLevel().name());

    }
}

/*
 * Java 実行開始時、VegetableCatalog の定数初期化が行われる。
 * VegetableCatalog で VegetableType<Onion> などが登録される（T extends AllEntity 制約もここで効く）。
 * VegetableType.of("onion", Onion::new) で、id が String か、Onion::new が EntityFactory<Onion> に適合するかをコンパイラが確認する。
 * VegetableType は受け取った id と factory を super(id, factory) で EntityType に渡して保持する。
 * この時点ではまだ Onion は生成されない（生成レシピを保持して待機）。
 * 後で ONION.create(level) が呼ばれると、EntityType#create が factory.create(this, level) を実行する。
 * 登録済みの Onion::new が呼ばれ、実際に new Onion(type, level) が実行される。
 * 生成された Onion インスタンスが create の戻り値として呼び出し元に返り、生成完了。
 */