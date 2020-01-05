package com.imooc.mall.other;

public class Main {
    public static void main(String[] args) {
        String s = "cart_%d";
        String str = String.format(s, 123);
        System.out.println(str);
    }
}
