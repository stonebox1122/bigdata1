package com.stone.ct.common.util;

import java.util.Date;

public class ProduceCustomerData {

    /**
     * customeid
     */
     private int id;

     /**
      * customename
      */
     private String name;
     


    public static void main(String[] args) {

        String[] names = {"李", "王", "张", "刘", "陈", "杨", "赵", "黄", "周",
                "吴", "徐", "孙", "胡", "朱", "高", "林", "何", "郭", "马", "罗", "董", "于", "冯",
                "萧", "谢", "梁", "宋", "郑", "唐", "韩", "曾", "吕", "沈", "阎", "叶", "彭", "薛", "傅", "邓", "夏",
                "许", "袁", "曹", "程", "丁", "姜", "苏", "潘", "魏", "杜", "田", "余", "蒋", "卢", "任", "汪", "贾"};
        String[] lastNames = {"花", "伟", "强", "建国", "解放", "援朝"};
        String[] phones = {"13", "17", "15", "18", "16"};

        for (int i = 0; i < 50; i++) {
            String name = names[(int) (Math.random() * names.length)] + lastNames[(int) (Math.random() * 6)];
            String phone = phones[(int) (Math.random() * 4)] + (int) (Math.random() * 10) + (int) (Math.random() * (9999 - 1000 + 1) + 1000) + (int) (Math.random() * 10) + (int) (Math.random() * 10) + (int) (Math.random() * 10);
            System.out.println(phone + "\t" + name);
        }

        System.out.println(1);

        System.out.println(2);
        System.out.println();

        Date date = new Date();
    }


}
