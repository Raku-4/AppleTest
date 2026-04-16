package com.raku.apple_test.abstract_class;


import com.raku.apple_test.abstract_apple.AbstractApple;

public class AppleAbstract extends AbstractApple {
    private final String taste;

    public AppleAbstract(int weight, String taste){
        super(weight); //抽象クラスのコンストラクタを呼ぶ
        this.taste = taste;
    }

    @Override
    public void eat(){ //必須の実装
        System.out.println(getWeight() + "g のりんごを食べた。味は" + taste);
    }

    public String setTaste(){
        return taste;
    }
}
