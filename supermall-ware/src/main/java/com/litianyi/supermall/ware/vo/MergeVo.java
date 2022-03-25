package com.litianyi.supermall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/20 10:36 AM
 */
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
