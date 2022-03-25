package com.litianyi.supermall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.ware.dao.WareInfoDao;
import com.litianyi.supermall.ware.entity.WareInfoEntity;
import com.litianyi.supermall.ware.service.WareInfoService;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> w
                    .eq(WareInfoEntity::getId, key)
                    .or().eq(WareInfoEntity::getAreacode, key)
                    .or().like(WareInfoEntity::getName, key)
                    .or().like(WareInfoEntity::getAddress, key));
        }

        IPage<WareInfoEntity> page = this.page(new Query<WareInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}