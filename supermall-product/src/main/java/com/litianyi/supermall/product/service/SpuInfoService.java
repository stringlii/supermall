package com.litianyi.supermall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.product.entity.SpuInfoEntity;
import com.litianyi.supermall.product.vo.spu.save.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-01 20:36:17
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo saveVo) throws Exception;

    void up(Long spuId) throws Exception;
}

