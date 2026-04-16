package com.raku.apple_test.argument;

import org.jetbrains.annotations.NotNull;

public class Main1Argument {
    public static void main(String @NotNull [] args){
        System.out.println("Argument :" + args.length);
        for (int i = 0; i < args.length; i++){
            System.out.println("args[" + i + "] = " + args[i]);
        }
    }
}
