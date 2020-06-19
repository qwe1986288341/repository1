package com.leyou.search.com.leyou.service;

import cn.jiyun.common.pojo.PageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.search.com.leyou.feign.*;
import com.leyou.search.com.leyou.mapper.GoodsRepository;
import com.leyou.search.com.leyou.pojo.Goods;
import com.leyou.search.com.leyou.pojo.SearchRequest;
import com.leyou.search.com.leyou.pojo.SearchResult;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Goods的字段
 *  @Id
 *     private Long id; // spuId
 *     @Field(type = FieldType.Text, analyzer = "ik_max_word")
 *     private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌
 *     @Field(type = FieldType.Keyword, index = false)
 *     private String subTitle;// 卖点
 *     private Long brandId;// 品牌id
 *     private Long cid1;// 1级分类id
 *     private Long cid2;// 2级分类id
 *     private Long cid3;// 3级分类id
 *     private Date createTime;// 创建时间
 *     private List<Long> price;// 价格
 *     @Field(type = FieldType.Keyword, index = false)
 *     private String skus;// List<sku>信息的json结构
 *     private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值
 *
 *     spu的字段
 *       private Long id;
 *     private Long brandId;
 *     private Long cid1;// 1级类目
 *     private Long cid2;// 2级类目
 *     private Long cid3;// 3级类目
 *     private String title;// 标题
 *     private String subTitle;// 子标题
 *     private Boolean saleable;// 是否上架
 *     private Boolean valid;// 是否有效，逻辑删除用
 *     private Date createTime;// 创建时间
 *     private Date lastUpdateTime;// 最后修改时间
 * */

@Service
public class GoodsSeravice {

    @Autowired
    SpuClient spuClient;

    @Autowired
    SpeClient speClient;

    @Autowired
    BrandClient brandClient;

    @Autowired
    CategoryClient categoryClient;

    @Autowired
    SkuClient skuClient;

    @Autowired
    GoodsRepository goodsRepository;

    //该工具可以将集合转换为Json类型
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu s) throws Exception {
        //创建商品的对象
        Goods goods = new Goods();
        //1.设置goods的id goods的id就是spu的id设置完成
        goods.setId(s.getId());

        //2.用于查询搜索 包含标题，分类，甚至品牌
        //标题 title + 分类 + 品牌 brandName
        //品牌有 品牌id  可以根据品牌id查找到品牌名称
        //品牌名称
        Brand brandName = brandClient.getBrandByBrandId(s.getBrandId());
        //商品分类
        //创建一个集合用来存储id
        List<Long> list = new ArrayList<>();
        //添加三个id
        list.add(s.getCid1());
        list.add(s.getCid2());
        list.add(s.getCid3());
        List<String> cname = categoryClient.getCategorys(list);
        //All字段
        goods.setAll(s.getTitle()+" "+brandName.getName()+" "+ StringUtils.join(cname,""));

        //3.卖点 subTitle
        goods.setSubTitle(s.getSubTitle());

        //4.品牌的id用于设置品牌的id
        goods.setBrandId(s.getBrandId());

        //5.商品的三个分类id
        //设置商品的所属分类
        goods.setCid1(s.getCid1());
        goods.setCid2(s.getCid2());
        goods.setCid3(s.getCid3());

        //6.设置创建时间
        goods.setCreateTime(s.getCreateTime());

        //7.价格和sku的结构信息可以一起查询  sku可以通过spuid查询
        List<Sku> skus = skuClient.list(s.getId());
        //为了节省空间所以我们才创建这个map集合用来节省孔家你自愿
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        List<Long> prices = new ArrayList<>();

        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            //创建Map集合用来存储展示所需要的信息
            Map<String,Object> map = new HashMap<>();
            //添加所需要的信息
            map.put("id", sku.getId());

            map.put("title", sku.getTitle());

            String image = "";

            String images = sku.getImages();

            if(StringUtils.isNotBlank(images)){
                image = images.split(",")[0];
            }

            map.put("image", image);

            map.put("price", sku.getPrice());

            skuMapList.add(map);
        });
        //把价格添加到goods中
        goods.setPrice(prices);

        //将skus转换为json格式
        try {
            goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //最后 specs;// 可搜索的规格参数，key是参数名，值是参数值
        Map<String ,Object> specs = new HashMap<>();
        //首先查询出所有可搜索的规格参数
        List<SpecParam> params = speClient.params(null, s.getCid3(), true, null);

        //获取所有的属性值
        SpuDetail list1 = spuClient.list(s.getId());
        //这个是Json格式的spu属性
        String genericSpec = list1.getGenericSpec();
        //这个是Json格式的sku属性
        String specialSpec = list1.getSpecialSpec();
        //Spring字符串你不方便操作 所以我们将json转换为javaBean的形式
        //将Json字符串你转换为JavaBean对象

        //用于存放spu属性
        Map<Long,Object> genericSpecMap = MAPPER.readValue(genericSpec, new TypeReference<Map<Long,Object>>(){

        });
        //用于存放sku属性
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(list1.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });

        //从所有的规格参数中进行分类  通用的规格参数和sku的规格参数
        //params就是所有课查询的参数名称对象
        params.forEach(param->{
            //如果为true则说明该参数是通用的规格参数
            if (param.getGeneric()){
               //如果是true则说明是sku通用属性
               //去集合中查询出和参数名对应的名称
                String value = genericSpecMap.get(param.getId()).toString();
               //判断如果当前值是数字类型需要进行额外的判断 进行区间查询
                if(param.getNumeric()){
                   value = this.chooseSegment(value, param);
                }
                specs.put(param.getName(), value);
            }else{
                //否则就是sku的规格参数
                //如果是sku对象的话
                List<Object> value = specialSpecMap.get(param.getId());
                specs.put(param.getName(), value);

            }
        });

        goods.setSpecs(specs);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchResult SearchGoods(SearchRequest searchRequest) {
        //这里跟我们的elasticserch关联起来

        //有什么需要处理

        //对key进行处理

        //还要进行分页

        // 根据cid3进行 聚合查询 查看要查询的商品是属于什么分类下的

        //根据当前查询出来的商品goods对象中的品牌id聚合查询出来都有什么品牌
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //通过all字段分词查询出来的商品
        QueryBuilder all = QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND);
        //创建布尔查询
        BoolQueryBuilder myQuery = BuildBooleanQueryBuilder(all,searchRequest);
        queryBuilder.withQuery(myQuery);
        if(searchRequest.getKey()==null){
           return null;
        }
        queryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle","price","image"}, null));
        int page = searchRequest.getPage();
        int size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1, size));


        queryBuilder.addAggregation(AggregationBuilders.terms("categorys").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brands").field("brandId"));

        AggregatedPage<Goods> search = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());

        //对聚合条件进行解析
        List<Map<String,Object>> categorys =  getCategoryAggResult(search.getAggregation("categorys"));
        List<Brand> brands = getBrandAggResult(search.getAggregation("brands"));

        List<Map<String,Object>> specResult =  getSpecsAggResult(all, (Long) categorys.get(0).get("id"));

        SearchResult pageResult = new SearchResult();
        pageResult.setSpecs(specResult);
        pageResult.setTotal(search.getTotalElements());
        pageResult.setItems(search.getContent());
        pageResult.setTotalPage(search.getTotalPages());
        pageResult.setCategories(categorys);
        pageResult.setBrands(brands);
        return pageResult;
    }

    private BoolQueryBuilder BuildBooleanQueryBuilder(QueryBuilder all, SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(all);
        Map<String, String> filter = searchRequest.getFilter();
        Set<Map.Entry<String, String>> entries = filter.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value1 = entry.getValue();
            System.out.println(key+"1111111111111"+value1);
            if(key.equals("品牌")){
                key = "brandId";
            }else if(key.equals("分类")){
                key = "cid3";
            }else{
                key = "specs."+key+".keyword";
            }
            String value = entry.getValue();
            //添加过滤条件
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,value));
        }
        return boolQueryBuilder;
    }

    private List<Map<String, Object>> getSpecsAggResult(QueryBuilder all, Long id) {
        //全新的聚合查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(all);
        //得到当前分类下的所有用于搜索的规格参数对象
        List<SpecParam> params = speClient.params(null, id, true, null);

        //通过规格参数进行聚合
        params.forEach(p->{
            nativeSearchQueryBuilder.addAggregation
                    (AggregationBuilders.terms(p.getName()).field("specs."+p.getName()+".keyword"));
        });
        //纯粹的聚合不包含任何字段
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行搜索
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) goodsRepository.search(nativeSearchQueryBuilder.build());
        List<Map<String,Object>> paramMapList = new ArrayList<>();
        //获取所有的聚合结果 如 cpu频率 cpu核数
        //String表示当时我们给聚合起的名字 后边表示该聚合名字所对应的值
        Map<String, Aggregation> stringAggregationMap = goodsPage.getAggregations().asMap();
        //map集合的遍历
        Set<Map.Entry<String, Aggregation>> entries = stringAggregationMap.entrySet();
        entries.forEach(entry->{
            Map<String,Object> map = new HashMap<>();
            map.put("key", entry.getKey());
            List<Object> options = new ArrayList<>();
            StringTerms value = (StringTerms) entry.getValue();
            List<StringTerms.Bucket> buckets = value.getBuckets();
            buckets.forEach(bucket -> {
                options.add(bucket.getKeyAsString());
                map.put("options",options);
            });
            paramMapList.add(map);
        });

        return paramMapList;
    }

    private List<Brand> getBrandAggResult(Aggregation key) {
        LongTerms brandLongTerms = (LongTerms) key;
        List<LongTerms.Bucket> buckets = brandLongTerms.getBuckets();
        List<Brand> brandId = new ArrayList<>();
        buckets.forEach(bucket -> {
            Brand brandByBrandId = brandClient.getBrandByBrandId(bucket.getKeyAsNumber().longValue());
            brandId.add(brandByBrandId);
        });
        //根据品牌集合去后台查询

        return brandId;
    }

    private List<Map<String, Object>> getCategoryAggResult(Aggregation key) {
        LongTerms longTerms = (LongTerms) key;
        List<LongTerms.Bucket> buckets = longTerms.getBuckets();
        List<Long> longs = new ArrayList<>();
        buckets.forEach(b->{
            long l = b.getKeyAsNumber().longValue();
            longs.add(l);
        });
        List<String> categorys = categoryClient.getCategorys(longs);
        List<Map<String,Object>> maps = new ArrayList<>();

        for (int i = 0;i<categorys.size();i++){
            Map<String,Object> map = new HashMap<>();
            map.put("id", longs.get(i));
            map.put("name", categorys.get(i));
            maps.add(map);
        }

        return maps;
    }
}
