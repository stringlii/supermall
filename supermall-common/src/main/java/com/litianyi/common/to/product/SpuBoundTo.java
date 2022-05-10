package com.litianyi.common.to.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/17 6:02 PM
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
