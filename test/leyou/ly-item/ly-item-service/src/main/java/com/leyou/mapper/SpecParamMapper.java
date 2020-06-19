package com.leyou.mapper;

import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SpecParamMapper extends tk.mybatis.mapper.common.Mapper<SpecParam> {
    @Select("select * from tb_spec_param where group_id = ${id}")
    List<SpecParam> findSpecGroupBySpecGroupId(@Param("id") Long id);
}
