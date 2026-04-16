package com.raku.apple_test.generics;

/**
 * 黄色のばななを表す、具体的なジェネリクス利用例。
 */
public final class YellowBanana extends GenericsBanana<String> {
    public YellowBanana(String name, int weight) {
        super(name, "YELLOW", weight);
    }
}
