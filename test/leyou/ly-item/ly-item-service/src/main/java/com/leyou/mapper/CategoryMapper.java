package com.leyou.mapper;


import com.leyou.item.pojo.BrandAndCategory;
import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category>, SelectByIdListMapper<Category,Long> {
    @Select("SELECT category_id AS cid,brand_id AS bid FROM tb_category_brand WHERE brand_id = ${id}")
    List<BrandAndCategory> selectByPrimaryKey1(@Param("id") Integer id);
}
