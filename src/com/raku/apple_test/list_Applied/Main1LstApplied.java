package com.raku.apple_test.list_Applied;

import java.util.Scanner;

public class Main1LstApplied {
    public static void main(String[] args){
        String scanner1;
        int scanner2;
        int counter = 0;
        Scanner scanner = new Scanner(System.in);
        AppleListApplied appleList = new AppleListApplied();

        while (counter < 3) {
            System.out.println("色を指定してください。");
            scanner1 = scanner.nextLine();

            System.out.println("重さを指定してください。");
            scanner2 = Integer.parseInt(scanner.nextLine());

            appleList.addApple(scanner1, scanner2);
            counter++;
        }

        appleList.eat();
    }
}
