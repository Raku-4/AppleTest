package com.raku.apple_test.generics;

/**
 * 緑色のばななを表す、具体的なジェネリクス利用例。
 *
 * <p>型引数自体は String に固定し、
 * "このクラスは緑色のばななを作る" という意図を明確にしています。</p>
 */
public final class GreenBanana extends GenericsBanana<String> {
    public GreenBanana(String name, int weight) {
        super(name, "GREEN", weight);
    }
}
