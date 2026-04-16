package com.raku.banana_test;

import com.raku.fruit_test.functionalClass.AbstractInteractiveFruit;
import org.jetbrains.annotations.NotNull;

public class BananaInteractive extends AbstractInteractiveFruit<BananaEnumColor> {

    public BananaInteractive(@NotNull BananaEnumColor color, long weight, long ate){
        super(color, weight, ate);
    }

    @Override
    protected @NotNull String colorLabel(@NotNull BananaEnumColor color) {
        return color.getLabel();
    }

}
