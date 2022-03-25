package com.litianyi.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/17 6:29 PM
 */
@Data
public class SpuReductionTo {
    private Long skuId;
    private Integer fullCount;
    private BigDecimal discount;
    private Integer countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer priceStatus;
    private List<MemberPrice> memberPrice;
}
