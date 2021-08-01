package com.rita.common.constant;

/**
 * @Author: Rita
 * @Date:7/6/2021 11:51 AM
 */

//因为这些常量比较常用，分散到很多层和类中，把它们提取到一个文件中，方便以后修改维护

public class ProductConstant {
    //这里只能手动加上getter setter, 因为在一个类中
    public enum AttrEnum{
        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");
        private int code;
        private String msg;
        AttrEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum StatusEnum{
        New_SPU(0, "新建"),
        New_UP(1, "商品上架"),
        New_DOWN(2, "商品下架");

        private int code;
        private String msg;
        StatusEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
