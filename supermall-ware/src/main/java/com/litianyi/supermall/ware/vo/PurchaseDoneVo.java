package com.litianyi.supermall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/21 2:50 PM
 */
@Data
public class PurchaseDoneVo {
    /**
     * 采购单id
     */
    @NotNull
    private Long id;
    /**
     * 采购项
     */
    private List<PurchaseItemDoneVo> items;

    @Data
    public static class PurchaseItemDoneVo {
        private Long itemId;
        private Integer status;
        private String reason;
    }
}
