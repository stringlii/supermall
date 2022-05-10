package com.litianyi.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.litianyi.common.constant.UrlConstant;
import com.litianyi.common.to.search.SkuEsModel;
import com.litianyi.common.utils.R;
import com.litianyi.search.config.ElasticSearchConfiguration;
import com.litianyi.search.constant.EsConstant;
import com.litianyi.search.feign.ProductFeignService;
import com.litianyi.search.service.SearchService;
import com.litianyi.search.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/2/21 3:00 PM
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    RestHighLevelClient restClient;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) throws IOException {
        SearchRequest request = this.buildSearchRequest(param);
        SearchResponse response = restClient.search(request, ElasticSearchConfiguration.COMMON_OPTIONS);
        return this.buildSearchResponse(response, param);
    }

    /**
     * 构建es查询dsl
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        //模糊匹配和高亮
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            boolQuery.should(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
            boolQuery.should(QueryBuilders.matchQuery("brandName", param.getKeyword()));
            boolQuery.should(QueryBuilders.matchQuery("catalogName", param.getKeyword()));
            sourceBuilder.highlighter(new HighlightBuilder()
                    .field("skuTitle")
                    .preTags("<b style='color:red'>")
                    .postTags("</b>"));
        }
        //三级分类
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        //品牌id
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        //属性
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();

                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");

                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                boolQuery.filter(QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None));
            }
        }
        //是否有库存
        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        //价格区间
        if (StringUtils.isNotEmpty(param.getSkuPriceInterval())) {
            String[] s = param.getSkuPriceInterval().split("_");
            if (s.length == 2 && StringUtils.isNotBlank(s[0]) && StringUtils.isNotBlank(s[1])) {
                String min = s[0];
                String max = s[1];
                boolQuery.filter(QueryBuilders.rangeQuery("skuPrice").gte(min).lte(max));
            } else {
                if (param.getSkuPriceInterval().startsWith("_")) {
                    boolQuery.filter(QueryBuilders.rangeQuery("skuPrice").lte(s[1]));
                } else {
                    boolQuery.filter(QueryBuilders.rangeQuery("skuPrice").gte(s[0]));
                }
            }
        }
        sourceBuilder.query(boolQuery);
        //排序
        if (StringUtils.isNotEmpty(param.getSort())) {
            String[] s = param.getSort().split("_");
            String name = s[0];
            String order = s[1];
            sourceBuilder.sort(name, "asc".equalsIgnoreCase(order) ? SortOrder.ASC : SortOrder.DESC);
        }
        //分页
        sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);

        //品牌聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("brand_agg")
                .field("brandId").size(20)
                .subAggregation(AggregationBuilders.terms("brand_name")
                        .field("brandName").size(1))
                .subAggregation(AggregationBuilders.terms("brand_img")
                        .field("brandImg").size(1)));
        //分类聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("catalog_agg")
                .field("catalogId").size(20)
                .subAggregation(AggregationBuilders.terms("catalog_name")
                        .field("catalogName").size(1)));
        //属性聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attr_agg", "attrs")
                .subAggregation(AggregationBuilders.terms("attr_id")
                        .field("attrs.attrId").size(20)
                        .subAggregation(AggregationBuilders.terms("attr_name")
                                .field("attrs.attrName").size(1))
                        .subAggregation(AggregationBuilders.terms("attr_value")
                                .field("attrs.attrValue").size(50))));

        System.out.println(sourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * 封装为前台页面需要的数据
     */
    private SearchResult buildSearchResponse(SearchResponse response, SearchParam param) {
        SearchResult searchResult = new SearchResult();
        SearchHits hits = response.getHits();

        //商品信息
        List<SkuEsModel> products = new ArrayList<>();
        if (hits.getTotalHits().value > 0) {
            for (SearchHit hit : hits.getHits()) {
                String source = hit.getSourceAsString();
                SkuEsModel product = JSON.parseObject(source, SkuEsModel.class);
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (!CollectionUtils.isEmpty(highlightFields)) {
                    String skuTitle = highlightFields.get("skuTitle").getFragments()[0].string();
                    product.setSkuTitle(skuTitle);
                }
                products.add(product);
            }
        }
        searchResult.setProducts(products);

        //聚合信息
        Aggregations aggregations = response.getAggregations();
        List<SearchResult.CatalogVo> catalogs = new ArrayList<>();
        ParsedLongTerms catalogAgg = aggregations.get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            //catalogId
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(bucket.getKeyAsNumber().longValue());
            //catalogName
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name");
            catalogVo.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());

            catalogs.add(catalogVo);
        }
        searchResult.setCatalogs(catalogs);

        //品牌信息
        List<SearchResult.BrandVo> brands = new ArrayList<>();
        ParsedLongTerms brandAgg = aggregations.get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //brandId
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
            //brandName
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name");
            brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
            //brandImg
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img");
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());

            brands.add(brandVo);
        }
        searchResult.setBrands(brands);

        //属性信息
        List<SearchResult.AttrVo> attrs = new ArrayList<>();
        ParsedNested attrAgg = aggregations.get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            //attrId
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            //attrName
            ParsedStringTerms attrName = bucket.getAggregations().get("attr_name");
            attrVo.setAttrName(attrName.getBuckets().get(0).getKeyAsString());
            //attrValue
            ParsedStringTerms attrValue = bucket.getAggregations().get("attr_value");
            List<String> attrValues = attrValue.getBuckets().parallelStream()
                    .map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrs.add(attrVo);
        }
        searchResult.setAttrs(attrs);

        //总记录数
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
        //总页数
        searchResult.setTotalPages(total % EsConstant.PRODUCT_PAGE_SIZE == 0
                ? total / EsConstant.PRODUCT_PAGE_SIZE
                : total / EsConstant.PRODUCT_PAGE_SIZE + 1);
        //页码
        searchResult.setPageNum(param.getPageNum());
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= searchResult.getTotalPages(); i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        //构建面包屑导航功能
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //attrs=2_5寸:6寸
                String[] s = attr.split("_");
                navVo.setValue(s[1]);
                String attrId = s[0];

                //记录被选择的属性，面包屑导航用
                searchResult.getAttrIds().add(Long.valueOf(attrId));

                R r = productFeignService.attrInfo(Long.valueOf(attrId));
                if (r.getCode() == 0) {
                    AttrResponseVo attrResponseVo = r.getData("attr", AttrResponseVo.class);
                    navVo.setNavName(attrResponseVo.getAttrName());
                } else {
                    navVo.setNavName(attrId);
                }
                String link = replaceQueryString(param.get_queryString(), attr, "attrs");
                navVo.setLink(UrlConstant.SUPERMALL_SEARCH + "/list.html?" + link);

                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }

        //品牌的面包屑导航
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            List<SearchResult.NavVo> navs = searchResult.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");

            R r = productFeignService.brandInfos(param.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brandVos = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuilder brandName = new StringBuilder();
                String link = "";
                for (BrandVo brand : brandVos) {
                    brandName.append(brand.getName()).append(";");
                    link = replaceQueryString(param.get_queryString(), String.valueOf(brand.getBrandId()), "brandId");
                }
                navVo.setValue(brandName.toString());
                navVo.setLink(UrlConstant.SUPERMALL_SEARCH + "/list.html?" + link);
            }
            navs.add(navVo);
            searchResult.setNavs(navs);
        }

        //分类的面包屑导航
        if (param.getCatalog3Id() != null) {
            List<SearchResult.NavVo> navs = searchResult.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("分类");

            R r = productFeignService.categoryInfo(param.getCatalog3Id());
            if (r.getCode() == 0) {
                CategoryVo category = r.getData(new TypeReference<CategoryVo>() {
                });
                navVo.setValue(category.getName());
            } else {
                navVo.setValue(String.valueOf(param.getCatalog3Id()));
            }
            String link = replaceQueryString(param.get_queryString(), String.valueOf(param.getCatalog3Id()), "catalog3Id");
            navVo.setLink(UrlConstant.SUPERMALL_SEARCH + "/list.html?" + link);
            navs.add(navVo);
            searchResult.setNavs(navs);
        }

        return searchResult;
    }
}
