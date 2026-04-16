package com.raku.apple_test.list_Applied;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AppleListApplied {

    public @NotNull List<String> list_colors = new ArrayList<>();

    public @NotNull List<Integer> list_weight = new ArrayList<>();

    public void addApple(@NotNull String color, int weight) {
        if (color == null) throw new IllegalArgumentException("色は何もなしにはできません");
        list_colors.add(color);

        if (weight <= 0) throw new IllegalArgumentException("重さは正の数でなければいけません。");
        list_weight.add(weight);
    }

    public void eat() {
        for (int i = 0; i < list_colors.size(); i++) {
            System.out.println(list_colors.get(i) +  " : " + list_weight.get(i));
        }
    }
}