package com.leyou.service;

import cn.jiyun.common.pojo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.pojo.*;
import com.leyou.mapper.BrandMapper;
import com.leyou.mapper.CategoryMapper;
import com.leyou.mapper.SpuDetailMapper;
import com.leyou.mapper.SpuMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    public PageResult<SpuVo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        if(!StringUtils.isEmpty(key)){
            criteria.andLike("title", "%"+key+"%");
        }
        //查看是否上架
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //分页条件
        PageHelper.startPage(page, rows);
        // 执行查询
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        List<SpuVo> spuVoList = new ArrayList<>();
        for (Spu spu : spus) {
            SpuVo spuVo = new SpuVo();
            spuVo.setSaleable(spu.getSaleable());
            spuVo.setTitle(spu.getTitle());
            spuVo.setId(spu.getId());
            spuVo.setCid1(spu.getCid1());
            spuVo.setCid2(spu.getCid2());
            spuVo.setCid3(spu.getCid3());
            spuVo.setSubTitle(spu.getSubTitle());
            spuVo.setValid(spu.getValid());
           spuVo.setBrandId(spu.getBrandId());
            //去查询商品的类型表
            Long cid1 = spu.getCid1();
            Long cid2 = spu.getCid2();
            Long cid3 = spu.getCid3();

            Category category1 = categoryMapper.selectByPrimaryKey(cid1);
            Category category2 = categoryMapper.selectByPrimaryKey(cid2);
            Category category3 = categoryMapper.selectByPrimaryKey(cid3);
            String cname = category1.getName()+"/"+category2.getName()+"/"+category3.getName();

            //设置类型名称
            spuVo.setCname(cname);
            //设置类型名称
            Long brandId = spu.getBrandId();

            Brand brand = brandMapper.selectByPrimaryKey(brandId);

            spuVo.setBname(brand.getName());

            //到这里就完全设置成功了然后添加到集合中
            spuVoList.add(spuVo);

        }
        PageResult pageResult = new PageResult();
        pageResult.setItems(spuVoList);
        pageResult.setTotal(pageInfo.getTotal());
        return pageResult;
    }

    public SpuDetail selectById(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        System.out.println(spuDetail);
        return spuDetail;
    }


    public Spu querySpuById(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        return spu;
    }

    public Brand findBrandBySpuId(Long id) {
        Spu spu = this.querySpuById(id);
        Long brandId = spu.getBrandId();
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        return brand;
    }
}
