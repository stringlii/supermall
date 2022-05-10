package com.litianyi.supermall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.litianyi.common.constant.ProductConstant;
import com.litianyi.common.to.product.SpuBoundTo;
import com.litianyi.common.to.product.SpuReductionTo;
import com.litianyi.common.to.search.SkuEsModel;
import com.litianyi.common.utils.R;
import com.litianyi.supermall.product.entity.*;
import com.litianyi.supermall.product.feign.CouponFeignService;
import com.litianyi.supermall.product.feign.SearchFeignService;
import com.litianyi.supermall.product.feign.WareFeignService;
import com.litianyi.supermall.product.service.*;
import com.litianyi.supermall.product.vo.SkuHasStockVo;
import com.litianyi.supermall.product.vo.SpuInfoVo;
import com.litianyi.supermall.product.vo.spu.save.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service("spuInfoService")
@Slf4j
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService baseAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> w
                    .like(SpuInfoEntity::getSpuName, key)
                    .or().like(SpuInfoEntity::getSpuDescription, key)
                    .or().eq(SpuInfoEntity::getId, key));
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !"0".equals(brandId)) {
            wrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }

        String catalogId = (String) params.get("catalogId");
        if (StringUtils.isNotEmpty(catalogId) && !"0".equals(catalogId)) {
            wrapper.eq(SpuInfoEntity::getCatalogId, catalogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        List<SpuInfoVo> spuInfoVoList = page.getRecords().stream().map(item -> {
            SpuInfoVo vo = new SpuInfoVo();
            BeanUtils.copyProperties(item, vo);
            CategoryEntity categoryEntity = Optional.ofNullable(categoryService.getById(item.getCatalogId()))
                    .orElse(new CategoryEntity());
            vo.setCatalogName(categoryEntity.getName());
            BrandEntity brandEntity = Optional.ofNullable(brandService.getById(item.getBrandId()))
                    .orElse(new BrandEntity());
            vo.setBrandName(brandEntity.getName());
            return vo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(spuInfoVoList);
        return pageUtils;
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo vo) throws Exception {
        //保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);

        //保存spu描述 pms_spu_info_desc
        List<String> descriptions = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", descriptions));
        spuInfoDescService.save(spuInfoDescEntity);

        //保存spu图片集 pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //保存spu属性 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        baseAttrValueService.saveProductAttr(spuInfoEntity.getId(), baseAttrs);

        //保存当前spu对应的所有sku信息
        List<Skus> skus = vo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            for (Skus item : skus) {
                //sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                Images defaultImg = item.getImages().stream()
                        .filter(img -> img.getDefaultImg() == 1).findFirst()
                        .orElseGet(() -> {
                            Images img = new Images();
                            img.setImgUrl(null);
                            return img;
                        });
                skuInfoEntity.setSkuDefaultImg(defaultImg.getImgUrl());
                skuInfoService.save(skuInfoEntity);

                //sku图片信息 pms_sku_images
                List<SkuImagesEntity> skuImagesEntityList = item.getImages().stream()
                        .filter(img -> StringUtils.isNotEmpty(img.getImgUrl()))
                        .map(img -> {
                            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                            skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                            BeanUtils.copyProperties(img, skuImagesEntity);
                            return skuImagesEntity;
                        }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntityList);

                //sku属性 pms_sku_sale_attr_value
                List<SkuSaleAttrValueEntity> saleAttrs = item.getAttr().stream().map(attr -> {
                    SkuSaleAttrValueEntity saleAttr = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, saleAttr);
                    saleAttr.setSkuId(skuInfoEntity.getSkuId());
                    return saleAttr;
                }).collect(Collectors.toList());
                saleAttrValueService.saveBatch(saleAttrs);

                //sku优惠信息满减信息 supermall_sms: sms_sku_ladder、sms_sku_full_reduction、sms_member_price
                SpuReductionTo spuReductionTo = new SpuReductionTo();
                BeanUtils.copyProperties(item, spuReductionTo);
                spuReductionTo.setSkuId(skuInfoEntity.getSkuId());
                if (spuReductionTo.getFullCount() > 0 ||
                        spuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
                    R r = couponFeignService.saveSkuReduction(spuReductionTo);
                    if (r.getCode() != 0) {
                        throw new Exception("远程保存sku优惠信息满减信息失败");
                    }
                }
            }
        }

        //保存spu积分 supermall_sms: sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            throw new Exception("远程保存spu积分信息失败");
        }
    }

    @Override
    @Transactional
    public void up(Long spuId) throws Exception {
        // 根据spuid查出对应的sku
        List<SkuInfoEntity> SkuInfoEntities = skuInfoService.listBySpuId(spuId);
        // 可以被检索的sku规格属性
        List<ProductAttrValueEntity> baseAttrValues = baseAttrValueService.ListBySpuId(spuId);
        List<Long> attrIds = baseAttrValues.stream()
                .map(ProductAttrValueEntity::getAttrId)
                .collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.listSearchAttrIdsByIds(attrIds);

        List<SkuEsModel.Attr> attrs = baseAttrValues.stream()
                .filter(attr -> searchAttrIds.contains(attr.getAttrId()))
                .map(sku -> {
                    SkuEsModel.Attr attr = new SkuEsModel.Attr();
                    BeanUtils.copyProperties(sku, attr);
                    return attr;
                }).collect(Collectors.toList());

        // 查是否有库存
        R stock = wareFeignService.hasStock(SkuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));
        if (stock.getCode() != 0) {
            throw new Exception("库存查询异常");
        }
        Map<Long, Boolean> hasStockList = stock.getData(new TypeReference<List<SkuHasStockVo>>() {
        }).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        // 封装sku信息
        List<SkuEsModel> upProducts = SkuInfoEntities.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            esModel.setHotScore(0L);
            // 设置检索属性
            esModel.setAttrs(attrs);
            // 查是否有库存
            Boolean hasStock = hasStockList.get(sku.getSkuId());
            esModel.setHasStock(hasStock);
            // 品牌
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());
            // 分类
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            return esModel;
        }).collect(Collectors.toList());

        //  保存到es
        log.info("{}", upProducts);
        R r = searchFeignService.productUp(upProducts);
        if (r.getCode() == 0) {
            // 修改spu状态
            this.update(new LambdaUpdateWrapper<SpuInfoEntity>()
                    .set(SpuInfoEntity::getPublishStatus, ProductConstant.StatusEnum.SPU_UP.getCode())
                    .set(SpuInfoEntity::getUpdateTime, new Date())
                    .eq(SpuInfoEntity::getId, spuId));
        } else {
            // todo 重复调用，接口幂等性
            throw new Exception("保存到es失败");
        }
    }

}