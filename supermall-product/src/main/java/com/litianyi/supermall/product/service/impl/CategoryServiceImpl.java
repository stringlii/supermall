package com.litianyi.supermall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.litianyi.common.utils.SnowflakeIdWorker;
import com.litianyi.supermall.product.service.CategoryBrandRelationService;
import com.litianyi.supermall.product.vo.Catalog2Vo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litianyi.common.utils.PageUtils;
import com.litianyi.common.utils.Query;

import com.litianyi.supermall.product.dao.CategoryDao;
import com.litianyi.supermall.product.entity.CategoryEntity;
import com.litianyi.supermall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    public static final String CATEGORY_CACHE_NAME = "category";

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryList = baseMapper.selectList(null);
        return categoryList.stream()
                .filter(item -> item.getParentCid() == 0)
                .peek(item -> item.setChildren(CategoryService.getChildren(item, categoryList)))
                .sorted(Comparator.comparingInt(o -> (o.getSort() == null ? 0 : o.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = CATEGORY_CACHE_NAME, allEntries = true)
    public boolean removeCategoryByIds(List<Long> ids) {
        // todo 检查当前删除的菜单是否被别处引用
        baseMapper.deleteBatchIds(ids);
        return true;
    }

    @Override
    public Long[] findCatalogPath(Long catalogId) {
        List<Long> path = this.findParentPath(catalogId);
        Collections.reverse(path);
        return path.toArray(new Long[0]);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {CATEGORY_CACHE_NAME}, key = "'listLevel1Categories'"),
            @CacheEvict(value = {CATEGORY_CACHE_NAME}, key = "'getCatalogJson'"),
    })
    public void updateCascade(CategoryEntity category) {
        baseMapper.updateById(category);
        if (StringUtils.isNotEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Cacheable(value = {CATEGORY_CACHE_NAME}, key = "#root.methodName")
    @Override
    public List<CategoryEntity> listLevel1Categories() {
        List<CategoryEntity> list = this.list(new LambdaUpdateWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, 0));
        return list;
    }

    @Override
    @Cacheable(value = {CATEGORY_CACHE_NAME}, key = "#root.methodName")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        List<CategoryEntity> all = this.list();
        List<CategoryEntity> level1List = all.stream().filter(item -> item.getParentCid() == 0).collect(Collectors.toList());
        return level1List.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
                    //二级分类
                    return all.stream()
                            .filter(item -> Objects.equals(item.getParentCid(), v.getCatId()))
                            .map(item -> new Catalog2Vo(
                                    v.getCatId(),
                                    item.getCatId(),
                                    item.getName(),
                                    //三级分类
                                    all.stream()
                                            .filter(i -> Objects.equals(i.getParentCid(), item.getCatId()))
                                            .map(i -> new Catalog2Vo.Catalog3Vo(
                                                    item.getCatId(),
                                                    i.getCatId(),
                                                    i.getName()
                                            )).collect(Collectors.toList())
                            )).collect(Collectors.toList());
                }));
    }

    public Map<String, List<Catalog2Vo>> getCatalogJsonFromRedis() {
        String json = redisTemplate.opsForValue().get("getCatalogJson");
        if (StringUtils.isEmpty(json)) {
            return this.getCatalogJsonWithRedissonLock();
        }
        return JSON.parseObject(json, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
    }

    /**
     * 上锁+再次查询缓存，解决缓存击穿
     * 分布式锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonWithRedissonLock() {
        RLock lock = redisson.getLock("catalog-json");
        lock.lock();
        Map<String, List<Catalog2Vo>> catalogJsonFromDb = null;
        try {
            catalogJsonFromDb = this.getCatalogJsonFromDb();
        } finally {
            lock.unlock();
        }
        return catalogJsonFromDb;
    }

    /**
     * 上锁+再次查询缓存，解决缓存击穿
     * 分布式锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonWithDistributedLock() {
        String token = SnowflakeIdWorker.getStrId();
        //获取锁并加过期时间，原子操作
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", token, 30, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(lock)) {
            System.out.println("获取分布式锁成功");
            Map<String, List<Catalog2Vo>> data = null;
            try {
                data = this.getCatalogJsonFromDb();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //删除自己的锁，原子操作
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<>(script, Integer.class),
                        Collections.singletonList("lock"), token);
                /*String lockValue = redisTemplate.opsForValue().get("lock");
                if (token.equals(lockValue)) {
                    redisTemplate.delete("lock");
                }*/
            }
            return data;
        } else {
            System.out.println("获取分布式锁失败");
            //加锁失败，重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonWithDistributedLock();    //自旋
        }
    }

    /**
     * 上锁+再次查询缓存，解决缓存击穿
     * 本地锁
     */
    public synchronized Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        return this.getCatalogJsonFromDb();
    }

    /**
     * 查数据库并缓存
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {
        //再次获取缓存
        String json = redisTemplate.opsForValue().get("getCatalogJson");
        if (StringUtils.isNotEmpty(json)) {
            return JSON.parseObject(json, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
        }

        List<CategoryEntity> all = this.list();
        List<CategoryEntity> level1List = all.stream().filter(item -> item.getParentCid() == 0).collect(Collectors.toList());
        Map<String, List<Catalog2Vo>> catalog = level1List.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
                    //二级分类
                    return all.stream()
                            .filter(item -> Objects.equals(item.getParentCid(), v.getCatId()))
                            .map(item -> new Catalog2Vo(
                                    v.getCatId(),
                                    item.getCatId(),
                                    item.getName(),
                                    //三级分类
                                    all.stream()
                                            .filter(i -> Objects.equals(i.getParentCid(), item.getCatId()))
                                            .map(i -> new Catalog2Vo.Catalog3Vo(
                                                    item.getCatId(),
                                                    i.getCatId(),
                                                    i.getName()
                                            )).collect(Collectors.toList())
                            )).collect(Collectors.toList());
                }));
        //缓存
        redisTemplate.opsForValue().set("getCatalogJson", JSON.toJSONString(catalog), 5, TimeUnit.MINUTES);
        return catalog;
    }

}