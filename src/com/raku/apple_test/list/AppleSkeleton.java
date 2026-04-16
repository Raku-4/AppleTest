package com.raku.apple_test.list;

public class AppleSkeleton {
    private final String color;
    private final int weight;

    public AppleSkeleton(String color, int weight){
        this.color = color;
        this.weight = weight;
    }

    public void appleState(){
        System.out.println("こちらは、重さ" + weight + "g の" + color + "色のりんごです。");
    }
}
