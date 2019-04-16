package com.stone.ct.common.util;

public class ProduceDateData {
    public static void main(String[] args) {
        System.out.println(2018);
        for(int i=1;i<=12;i++){
            System.out.println("2018" + "," + i);
            for (int j=1;j<=30;j++){
                System.out.println("2018" + "," + i + "," + j);
            }
        }
    }
}
