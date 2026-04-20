package com.raku.apple_test.for_sentence;

public enum Sweetness {
    INSANELY_SWEET("狂うほど甘い"),
    VERY_SWEET("とても甘い"),
    SWEET("甘い"),
    A_LITTLE_SWEET("少し甘い"),
    NOT_SWEET("全く甘くない");

    private final String sweetness;

    Sweetness(String sweetness) {
        this.sweetness = sweetness;
    }

    public String getSweetness() {
        return sweetness;
    }
}
