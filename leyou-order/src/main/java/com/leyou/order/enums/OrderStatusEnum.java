package com.leyou.order.enums;

public enum OrderStatusEnum {

    UNPAID(1,"未付款"),
    PAID(2,"已付款"),
    DELIVERED(3,"已发货，未确认"),
    SUCCESS(4,"交易成功，未评价"),
    CLOSE(5,"交易已经关闭"),
    RATED(6,"已评价")
    ;
    private int code;
    private String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int value(){
        return this.code;
    }
    public String desc(){
        return this.desc;
    }
}
