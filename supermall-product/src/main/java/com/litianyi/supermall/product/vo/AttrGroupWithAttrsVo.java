package com.litianyi.supermall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.litianyi.supermall.product.entity.AttrEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/15 7:53 PM
 */
@Data
public class AttrGroupWithAttrsVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String description;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catalogId;

    private List<AttrEntity> attrs = new ArrayList<>();
}
