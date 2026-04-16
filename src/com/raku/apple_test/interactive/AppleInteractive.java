package com.raku.apple_test.interactive;


import com.raku.apple_test.all_argument.enum_color.AppleEnumColor;
import com.raku.fruit_test.functionalClass.AbstractInteractiveFruit;
import org.jetbrains.annotations.NotNull;

public class AppleInteractive extends AbstractInteractiveFruit<AppleEnumColor> {
    // コンストラクタ
    public AppleInteractive(@NotNull AppleEnumColor color, int weight, int ate){
        super(color, weight, ate);
    }

    @Override
    protected @NotNull String colorLabel(@NotNull AppleEnumColor color) {
        return color.getLabel();
    }

}