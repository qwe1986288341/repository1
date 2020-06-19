package com.leyou.search.com.leyou.pojo;

import cn.jiyun.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;
import java.util.Map;

public class SearchResult extends PageResult<Goods> {


    public SearchResult() {
    }

    //商品分类的结果集 用来存储
    private List<Map<String,Object>> categories;

    //用来存储搜索条件下品牌的结果集
    private List<Brand> brands;

    //分类对应的规格参数
    private List<Map<String,Object>> specs;


    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, Object>> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "categories=" + categories +
                ", brands=" + brands +
                ", specs=" + specs +
                '}';
    }
}
