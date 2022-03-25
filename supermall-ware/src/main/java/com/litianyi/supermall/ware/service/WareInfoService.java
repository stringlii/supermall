package com.litianyi.supermall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:52:09
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

