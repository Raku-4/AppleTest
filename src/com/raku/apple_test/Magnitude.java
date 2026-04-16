package com.raku.apple_test;

import java.util.Scanner;

public class Magnitude{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

         while (true) {
             System.out.printf("マグニチュードAの値を3以上且つ9以下の範囲で教えてください。>>");
             int input1 = Integer.parseInt(scanner.nextLine());
             if (input1 < 3 || input1 > 9){
                 System.out.printf("正しい値をもう一度入力してください。>>");
                 continue;
             }


             System.out.printf("今度は、マグニチュードBの値を3以上且つAの値以下の範囲で教えてください。>>");
             int input2 = Integer.parseInt(scanner.nextLine());
             if (input2 > input1 || input2 < 3){
                 System.out.println("正しい値をもう一度入力してください。>>");
                 continue;
             }

             int A = input1-input2;
             int B = (int) Math.pow(32,A);

             System.out.println(input1 + "は" + input2 + "より" + A + " だけ大きいので、マグニチュード" + input1 + "の地震はマグニチュード " + input2 + " の地震と比べて" + B + "倍のエネルギーを持っています。");
             break;
         }
    }
}
