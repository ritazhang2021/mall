package com.rita.common.constant;

import lombok.Getter;

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
}
