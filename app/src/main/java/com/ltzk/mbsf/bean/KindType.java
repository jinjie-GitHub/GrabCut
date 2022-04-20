package com.ltzk.mbsf.bean;


/**
 * 描述：
 * 作者： on 2020-6-15 23:01
 * 邮箱：499629556@qq.com
 */
public enum KindType {
    MAOBI("毛笔",1),YINGBI("硬笔",2),ZHUANGKE("篆刻",4),ZIDIAN("字典",3);
    private String name;
    private int kind;
    KindType(String name,int kind) {
        this.name = name;
        this.kind = kind;
    }

    public static KindType getKindTypeByKind(int kind){
        for (KindType enums : KindType.values()) {
            if (enums.getKind() == kind) {
                return enums;
            }
        }
        return null;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}
