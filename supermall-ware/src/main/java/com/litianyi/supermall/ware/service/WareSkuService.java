package com.litianyi.supermall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.ware.entity.WareSkuEntity;
import com.litianyi.supermall.ware.vo.SkuHasStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:52:09
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    void updateAndEsById(WareSkuEntity wareSku);
}

