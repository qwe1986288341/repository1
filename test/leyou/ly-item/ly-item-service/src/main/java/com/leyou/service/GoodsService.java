package com.leyou.service;

import cn.jiyun.common.pojo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.pojo.*;
import com.leyou.mapper.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    AmqpTemplate amqpTemplate;


    @Transactional
    public void add(SpuVo spuVo) {
        //添加spu商品时候先向表中添加没有的数据
        //设置id为null
        spuVo.setId(null);
        //设置创建的时间
        spuVo.setCreateTime(new Date());
        //设置最后修改的时间
        spuVo.setLastUpdateTime(spuVo.getCreateTime());
        //设置是否上架 默认上架
        spuVo.setSaleable(true);
        //设置是否有效
        spuVo.setValid(true);
        //添加到spu表中
        spuMapper.insertSelective(spuVo);

        //上边使用这个方法之后会返回添加的spu商品的id
        //我们添加spuDetail的时候可以根据spuid添加
        SpuDetail spuDetail = spuVo.getSpuDetail();
        //给这个spuDeail设置主键id并插入
        spuDetail.setSpuId(spuVo.getId());
        //设置完之后添加到spuDetail中
        spuDetailMapper.insertSelective(spuDetail);

        //然后向sku表中添加信息
        List<Sku> skus = spuVo.getSkus();
        //在这里遍历skus并且给她们的spuid赋值
        for (Sku sku : skus) {
            sku.setSpuId(spuVo.getId());
            //并给她们设置创建时间和更改时间
            sku.setCreateTime(new Date());
            //最后更待的时间
            sku.setLastUpdateTime(sku.getCreateTime());
            //完成之后并插入到数据库中
            skuMapper.insertSelective(sku);

            //添加库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insert(stock);
        }
        sentMsg(spuVo.getId(),"insert");
    }

    private void sentMsg(Long id,String key) {
        try {
            amqpTemplate.convertAndSend("item."+key,id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Transactional
    public void updateGoods(SpuVo spuVo) {
        //修改最后更改时间
        spuVo.setLastUpdateTime(new Date());
        //修改
        spuMapper.updateByPrimaryKeySelective(spuVo);

        //获取SpuDetail
        SpuDetail spuDetail = spuVo.getSpuDetail();
        //直接去修改
        spuDetailMapper.updateByPrimaryKeySelective(spuDetail);


        List<Sku> skus = spuVo.getSkus();
        Long id = spuVo.getId();
        //给先设置的sku添加spuid属性
        for (Sku sku : skus) {
            sku.setSpuId(id);
        }
        //删除所有之前sku的属性
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        skuMapper.deleteByExample(example);

        //通过spuid获取到所有sku对象
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> select = skuMapper.select(sku);
        //删除所有的库存
        for (Sku sku1 : select) {
            stockMapper.deleteByPrimaryKey(sku1.getId());
        }



        //然后再将新的库存和sku属性添加
        for (Sku sku2 : skus) {
            sku2.setCreateTime(new Date());
            sku2.setLastUpdateTime(sku2.getCreateTime());
            //添加sku
            skuMapper.insertSelective(sku2);
            //添加库存
            Stock stock = new Stock();
            stock.setSkuId(sku2.getId());
            stock.setStock(sku2.getStock());
            //添加库存
            stockMapper.insertSelective(stock);
        }
        sentMsg(spuVo.getId(),"update");
    }

    @Transactional
    public void deleteById(Long id) {
       //直接去数据库中删除spu的数据
        spuMapper.deleteByPrimaryKey(id);
      //然后根据spu的id删除spuDetail的数据
        spuDetailMapper.deleteByPrimaryKey(id);
      //创建sku对象 并将spuid赋值
        Sku sku = new Sku();
        sku.setSpuId(id);
        //通过这个id查找到所有该spu对象所对应的sku对象
        List<Sku> skus = skuMapper.select(sku);
        //遍历skus集合 并根据id删除
        for (Sku sku1 : skus) {
            stockMapper.deleteByPrimaryKey(sku1.getId());
        }
        //然后最后删除sku表中的数据
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        skuMapper.deleteByExample(example);

        sentMsg(id,"delete");
    }

    @Transactional
    public void saleable(Long id) {
        //通过Id去spu表中查询出spu 的信息
        Spu spu = spuMapper.selectByPrimaryKey(id);
        //将saleable更改为相反的信息
        Boolean saleable = spu.getSaleable();
        spu.setSaleable(!saleable);
        spuMapper.updateByPrimaryKeySelective(spu);

    }
}
