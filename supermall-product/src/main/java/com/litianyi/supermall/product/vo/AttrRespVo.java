package com.litianyi.supermall.product.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/13 3:58 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AttrRespVo extends AttrVo {
    private String catalogName;
    private String groupName;
    /**
     * 分类路径
     */
    private Long[] catalogPath;
}
