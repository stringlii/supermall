package com.litianyi.common.constant;

import lombok.Getter;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/14 2:42 PM
 */
public class ProductConstant {

    @Getter
    public enum AttrEnum {
        ATTR_TYPE_BASE(1, "base", "基本属性"), ATTR_TYPE_SALE(0, "sale", "销售属性");
        private final int code;
        private final String msg;
        private final String desc;

        AttrEnum(int code, String msg, String desc) {
            this.code = code;
            this.msg = msg;
            this.desc = desc;
        }
    }

    @Getter
    public enum StatusEnum {
        NEW_SPU(0, "新建"), SPU_UP(1, "商品上架"), SPU_DOWN(2, "商品下架");
        private final int code;
        private final String msg;

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
