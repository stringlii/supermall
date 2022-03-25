package com.litianyi.supermall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.supermall.ware.entity.PurchaseEntity;
import com.litianyi.supermall.ware.vo.MergeVo;
import com.litianyi.supermall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author litianyi
 * @email stringli@qq.com
 * @date 2022-01-02 13:52:09
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceived(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo) throws Exception;

    void received(List<Long> ids);

    void purchaseDone(PurchaseDoneVo vo);
}

