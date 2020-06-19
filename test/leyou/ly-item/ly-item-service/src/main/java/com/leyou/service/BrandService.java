package com.leyou.service;

import cn.jiyun.common.pojo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.item.pojo.Brand;
import com.leyou.mapper.BrandMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.beans.Transient;
import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper bm;

    public PageResult<Brand> queryByManycase(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        PageHelper.startPage(page, rows);
        //有时候条件逻辑很复杂 可以自己去做一个xml 用来书写sql语句
        Example example = new Example(Brand.class);
        //这一段相当于是在创建where语句
        Example.Criteria criteria = example.createCriteria();
        //1.key 根据姓名模糊查询或者根据首字母查询
        //2.相当于sql语句中的 select * from *** where  这里相当于后边拼接的  还有 or letter=key
        if(!StringUtils.isEmpty(key)){
            criteria.andLike("name", "%"+key+"%").orEqualTo("letter",key);
        }
        //3.排序  排序是属于example的
        example.setOrderByClause(sortBy+" "+ (desc?"Desc":"ASC"));

        //3.分页的信息
        List<Brand> list = bm.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getPageSize(),pageInfo.getList() );
    }

    @Transient
    public void addBread(Brand brand,List<Integer> cids) {
        //添加品牌 用另一种方法 因为有空值 这个方法就只添加不为null的值 并返回添加后的id
        //并且返回添加后生成的主键
        bm.insertSelective(brand);
        // 商品类型和品牌是多对多的类型
        /**
         * 比如华为公司 有手机也有电脑   手机和电脑也有许多品牌可以做
         * 所以他们之间是多 比如苹果公司也生产手机电脑 所以他们之间是多对多 所以就有一个中间表
         * */
        //添加中间表的信息
        for (Integer cid : cids) {
            bm.categoryandBrand(cid,brand.getId());
        }
    }

    public void deleteById(Integer id) {
        //根据id查找出来中间表中 该商品所对应的分类
        //删除商品类中的根据id
        bm.deleteByPrimaryKey(id);
        //删除中间表中的该id
        bm.deleteByPrimaryKey1(id);
    }


    public void updateBrand(Brand brand, List<Integer> cids) {
        //修改商品表中数据的信息
        bm.updateByPrimaryKey(brand);
        //然后删除中间表中的信息
        Integer id = Math.toIntExact(brand.getId());
        bm.deleteByPrimaryKey1(id);
        //然后向中间表中添加信息
        for (Integer cid : cids) {
            bm.addBrandAndCategory(cid,id);
        }

    }

    public List<Brand> selectById(Integer id) {
        List<Brand> brand = bm.selectBrandByCid(id);

        return brand;
    }

    public Brand getBrandNameByBrandId(Long id) {
        return bm.selectByPrimaryKey(id);
    }
}
