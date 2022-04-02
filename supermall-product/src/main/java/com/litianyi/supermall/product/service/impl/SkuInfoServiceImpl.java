package com.litianyi.supermall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litianyi.common.to.es.SkuEsModel;
import com.litianyi.common.utils.R;
import com.litianyi.supermall.product.entity.*;
import com.litianyi.supermall.product.feign.SearchFeignService;
import com.litianyi.supermall.product.feign.WareFeignService;
import com.litianyi.supermall.product.service.*;
import com.litianyi.supermall.product.vo.SkuHasStockVo;
import com.litianyi.supermall.product.vo.SkuItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.SkuInfoDao;
import org.springframework.transaction.annotation.Transactional;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private ProductAttrValueService baseAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> w
                    .like(SkuInfoEntity::getSkuName, key)
                    .or().like(SkuInfoEntity::getSkuDesc, key)
                    .or().eq(SkuInfoEntity::getSkuId, key));
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !"0".equals(brandId)) {
            wrapper.eq(SkuInfoEntity::getBrandId, brandId);
        }

        String catalogId = (String) params.get("catalogId");
        if (StringUtils.isNotEmpty(catalogId) && !"0".equals(catalogId)) {
            wrapper.eq(SkuInfoEntity::getCatalogId, catalogId);
        }

        String min = (String) params.get("min");
        if (StringUtils.isNotEmpty(min)) {
            wrapper.ge(SkuInfoEntity::getPrice, min);
        }

        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(max)) {
            try {
                BigDecimal maxDecimal = new BigDecimal(max);
                if (maxDecimal.compareTo(new BigDecimal("0")) > 0) {
                    wrapper.le(SkuInfoEntity::getPrice, max);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> listBySpuId(Long spuId) {
        return this.list(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));
    }

    @Override
    @Transactional
    public void up(Long skuId) throws Exception {
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();

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
        R stock = wareFeignService.hasStock(Collections.singletonList(skuId));
        if (stock.getCode() != 0) {
            throw new Exception("库存查询异常");
        }
        Map<Long, Boolean> hasStockList = stock.getData(new TypeReference<List<SkuHasStockVo>>() {
        }).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        SkuEsModel esModel = new SkuEsModel();
        BeanUtils.copyProperties(skuInfoEntity, esModel);
        esModel.setSkuPrice(skuInfoEntity.getPrice());
        esModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
        esModel.setHotScore(0L);
        // 设置检索属性
        esModel.setAttrs(attrs);
        // 查是否有库存
        Boolean hasStock = hasStockList.get(skuId);
        esModel.setHasStock(hasStock);
        // 品牌
        BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
        esModel.setBrandName(brandEntity.getName());
        esModel.setBrandImg(brandEntity.getLogo());
        // 分类
        CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getCatalogId());
        esModel.setCatalogName(categoryEntity.getName());

        // 保存到es
        R r = searchFeignService.skuUp(esModel);
        if (r.getCode() != 0) {
            throw new Exception("保存到es失败");
        }
    }

    @Override
    @Transactional
    public void updateStock(Long skuId, Boolean hasStock) throws Exception {
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();

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
        R stock = wareFeignService.hasStock(Collections.singletonList(skuId));
        if (stock.getCode() != 0) {
            throw new Exception("库存查询异常");
        }
        Map<Long, Boolean> hasStockList = stock.getData(new TypeReference<List<SkuHasStockVo>>() {
        }).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        SkuEsModel esModel = new SkuEsModel();
        BeanUtils.copyProperties(skuInfoEntity, esModel);
        esModel.setSkuPrice(skuInfoEntity.getPrice());
        esModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
        esModel.setHotScore(0L);
        // 设置检索属性
        esModel.setAttrs(attrs);
        // 查是否有库存
        esModel.setHasStock(hasStock);
        // 品牌
        BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
        esModel.setBrandName(brandEntity.getName());
        esModel.setBrandImg(brandEntity.getLogo());
        // 分类
        CategoryEntity categoryEntity = categoryService.getById(skuInfoEntity.getCatalogId());
        esModel.setCatalogName(categoryEntity.getName());

        // 保存到es
        R r = searchFeignService.updateSku(esModel);
        if (r.getCode() != 0) {
            throw new Exception("保存到es失败");
        }
    }

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo vo = new SkuItemVo();

        // sku 基本信息 pms_sku_info
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        vo.setInfo(skuInfoEntity);

        Long spuId = skuInfoEntity.getSpuId();

        // sku 图片信息 pms_sku_images
        List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
        vo.setImages(imagesEntities);

        // spu 销售属性组合
       List<SkuItemVo.ItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
       vo.setSaleAttrs(saleAttrVos);

        // spu 介绍
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        vo.setDesc(spuInfoDescEntity);

        // spu 规格参数信息
        List<SkuItemVo.ItemAttrGroupVo> attrGroupVos = attrGroupService.listAttrGroupWithAttrsBySpuId(spuId,
                skuInfoEntity.getCatalogId());
        vo.setGroupAttrs(attrGroupVos);

        return vo;
    }

}