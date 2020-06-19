import cn.jiyun.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.SpuVo;
import com.leyou.search.SearchApplication;
import com.leyou.search.com.leyou.feign.BrandClient;
import com.leyou.search.com.leyou.feign.SpuClient;
import com.leyou.search.com.leyou.mapper.GoodsRepository;
import com.leyou.search.com.leyou.pojo.Goods;
import com.leyou.search.com.leyou.service.GoodsSeravice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class GoodsTest {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    BrandClient brandClient;

    @Autowired
    SpuClient spuClient;

    @Autowired
    GoodsSeravice goodsSeravice;
    @Test
    public void buildGoods(){

        Integer page = 1;
        Integer rows = 100;
        do{



        /**
         * 步骤
         * 1.从mysql中查询数据
         * 2.将mysql数据模型转化为Goods数据模型
         * 一次从mysql中查询出数据模型 spu sku cotyray brand 等等
         * 3.创建elasticsearch的索引库 并将数据批量插入其中
         * */
        //1.从mysql中查询数据
        PageResult<SpuVo> pageResult = spuClient.querySpuBoByPage(null, null, page, rows);
       //获取分页中的当前页信息
        List<SpuVo> spus = pageResult.getItems();
        List<Goods> goodsList = new ArrayList<>();
        spus.forEach(s->{
            try {
                System.out.println(s.getId());
                Goods goods =  goodsSeravice.buildGoods(s);
                goodsList.add(goods);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        /**
         * 2.将mysql数据模型转化为Goods数据模型
         * 一次从mysql中查询出数据模型 spu sku cotyray brand 等等
         * 在Service中完成
         * */


        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
        goodsRepository.saveAll(goodsList);

            rows = pageResult.getItems().size();
            page ++;
        }while (rows == 100);
    }

    @Test
    public void queryBrandById(){
        Brand brandByBrandId = brandClient.getBrandByBrandId(1528L);
        System.out.println(brandByBrandId);
    }


}
