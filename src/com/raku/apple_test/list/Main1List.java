package com.raku.apple_test.list;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Main1List {
    public static void main(String[] args){
        List<AppleSkeleton> appleList = new ArrayList<>();

        appleList.add(new HardRedApple());
        appleList.add(new SoftBlueApple());
        appleList.add(new NormalGreenApple());

        ListIterator<AppleSkeleton> it = appleList.listIterator(appleList.size());

        for (AppleSkeleton apple : appleList) {
            apple.appleState();
        }

        while (it.hasPrevious()) {
            AppleSkeleton apple = it.previous();
            if (apple instanceof NormalGreenApple) {
                continue;
            }
            apple.appleState();
        }
    }
}
