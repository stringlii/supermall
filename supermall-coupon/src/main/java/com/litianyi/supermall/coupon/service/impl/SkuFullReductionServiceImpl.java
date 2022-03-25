package com.litianyi.supermall.coupon.service.impl;

import com.litianyi.common.to.MemberPrice;
import com.litianyi.common.to.SpuReductionTo;
import com.litianyi.supermall.coupon.entity.MemberPriceEntity;
import com.litianyi.supermall.coupon.entity.SkuLadderEntity;
import com.litianyi.supermall.coupon.service.MemberPriceService;
import com.litianyi.supermall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.coupon.dao.SkuFullReductionDao;
import com.litianyi.supermall.coupon.entity.SkuFullReductionEntity;
import com.litianyi.supermall.coupon.service.SkuFullReductionService;

@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * sku优惠信息满减信息 supermall_sms: sms_sku_ladder、sms_sku_full_reduction、sms_member_price
     *
     * @param spuReductionTo
     */
    @Override
    public void saveSkuReduction(SpuReductionTo spuReductionTo) {
        //sms_sku_ladder
        SkuLadderEntity ladderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(spuReductionTo, ladderEntity);
        if (ladderEntity.getFullCount() > 0) {
            ladderEntity.setAddOther(spuReductionTo.getCountStatus());
            skuLadderService.save(ladderEntity);
        }

        //sms_sku_full_reduction
        SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(spuReductionTo, fullReductionEntity);
        if (fullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            this.save(fullReductionEntity);
        }

        //sms_member_price
        List<MemberPriceEntity> memberPriceEntityList = spuReductionTo.getMemberPrice().stream()
                .filter(item -> item.getPrice().compareTo(new BigDecimal("0")) > 0)
                .map(item -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(spuReductionTo.getSkuId());
                    memberPriceEntity.setMemberLevelId(item.getId());
                    memberPriceEntity.setMemberLevelName(item.getName());
                    memberPriceEntity.setMemberPrice(item.getPrice());
                    memberPriceEntity.setAddOther(1);
                    return memberPriceEntity;
                }).collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntityList);
    }

}