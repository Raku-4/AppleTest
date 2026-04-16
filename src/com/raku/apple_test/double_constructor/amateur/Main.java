package com.raku.apple_test.double_constructor.amateur;

import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import com.raku.apple_test.double_constructor.pro.Level;
import com.raku.apple_test.double_constructor.pro.food.fruit.fruits.Apple;

/**
 * amateur 版の Main。
 * pro の複雑な分類がなく、シンプルに SimpleEntityType -> SimpleApple を使う流れを見る。
 */
public class Main {
    public static void main(String[] args) {
        Level field = new Level("畑");

        // 1) factory 経由で生成（pro と同じ流れ）
        // 入力: level=field
        // 戻り値: SimpleApple インスタンス（内部で SimpleApple::new が呼ばれる）
        Apple appleFactory = Catalog.APPLE.create(field);
        appleFactory.setColor(AppleEnumColor.RED);
        appleFactory.setSugarBrix(12);

        // 2) 便利コンストラクタ経由で生成
        // new SimpleApple(...) の戻り値: SimpleApple インスタンス
        Apple appleDirect = new Apple(field, AppleEnumColor.BLUE, 14);

        System.out.println(appleFactory.describe());
        System.out.println(appleDirect.describe());

        // getType() の戻り値: EntityType<? extends AllEntity>
        System.out.println("appleFactory type: " + appleFactory.getType().getLabeledId());
        // getLevel().name() の戻り値: String
        System.out.println("appleFactory level: " + appleFactory.getLevel().name());
    }
}

