package com.raku.apple_test.for_sentence;

public final class AppleFor implements FruitInterface, FruitKind {
    private Sweetness taste = Sweetness.SWEET;
    private String kind;

    public AppleFor(String color, long weight, int ID) {
        new FruitRecord("apple", color, weight, ID);
    }

    public AppleFor(String color, long weight, int ID, Sweetness taste) {
        this(color, weight, ID);
        this.taste = taste;
    }

    @Override
    public Sweetness getTaste() {
        return this.taste;
    }

    @Override
    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String getKind() {
        return this.kind;
    }
}
