package com.leyou.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

@Mapper
public interface BrandMapper extends tk.mybatis.mapper.common.Mapper<Brand> {
    @Insert("insert into tb_category_brand value (${cid},${bid})")
    void categoryandBrand(@Param("cid") Integer cid,@Param("bid") Long bid);

    @Delete("delete from tb_category_brand where brand_id=${id}")
    void deleteByPrimaryKey1(Integer id);
    @Insert("insert into tb_category_brand values(${cid},${id})")
    void addBrandAndCategory(@Param("cid") Integer cid,@Param("id") Integer id);
    @Select("SELECT b.* from tb_brand b INNER JOIN tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> selectBrandByCid(Integer id);
}
