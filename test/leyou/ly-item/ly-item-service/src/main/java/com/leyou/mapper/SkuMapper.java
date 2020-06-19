package com.leyou.mapper;

import com.leyou.item.pojo.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkuMapper extends tk.mybatis.mapper.common.Mapper<Sku> {
    @Select("select * from tb_sku where spu_id = ${id}")
    List<Sku> selectSkuById(@Param("id") Long id);
}
