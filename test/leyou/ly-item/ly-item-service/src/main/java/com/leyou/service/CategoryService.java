package com.leyou.service;

import com.leyou.item.pojo.BrandAndCategory;
import com.leyou.item.pojo.Category;
import com.leyou.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryMapper cm;

    public List<Category> findList(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> select = cm.select(category);
        return select;
    }

    public List<Category> selectAll(Integer id) {
       List<BrandAndCategory> bcList =  cm.selectByPrimaryKey1(id);
       List<Integer> ids = new ArrayList<>();
        for (BrandAndCategory brandAndCategory : bcList) {
            ids.add(brandAndCategory.getCid());
        }
        List<Category> categories = new ArrayList<>();
        for (Integer integer : ids) {
            Category category = cm.selectByPrimaryKey(integer);
            categories.add(category);
        }
        return categories;
    }

    public List<String> findCategorysByCid(List<Long> ids) {
        List<Category> list = cm.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category o : list) {
            names.add(o.getName());
        }
        return names;
    }
}
