package com.stone.ct.common.constant;

import com.stone.ct.common.bean.Val;

/**
 * 名称常量枚举类
 */
public enum Names implements Val {
    NAMESPACE("ct"),
    Table("ct:calllog"),
    CF_CALLER("caller"),
    CF_CALLEE("callee"),
    Topic("calllog");

    private String name;

    private Names(String name){
        this.name = name;
    }


    @Override
    public void setValue(Object val) {
        this.name = (String) val;
    }

    @Override
    public String getValue() {
        return name;
    }
}
