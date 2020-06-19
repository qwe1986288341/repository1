package cn.jiyun.service;

import cn.jiyun.client.CartClient;
import cn.jiyun.common.pojo.utils.JsonUtils;
import cn.jiyun.entity.UserInfo;
import cn.jiyun.intercept.UserIntercept;
import cn.jiyun.pojo.Cart;
import com.leyou.item.pojo.Sku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CartClient cartClient;
    static final String KEY_PREFIX="leyou:cart:uid";

    public void addCart(Cart cart) {
        UserInfo u = UserIntercept.getUserInfo();
        //如果 redis中要是有该数据 我们直接修改redis中的对应对象的num就可以
        //redis中的数据结构怎么处理   Map<String,Map<String,String>>
        //hash 大key 小key value
        //先从Redis中查看有没有这个值 有修改 没有就添加
        BoundHashOperations<String, Object, Object> hash = redisTemplate.boundHashOps(KEY_PREFIX + u.getId());
        //获取要添加商品的skuid
        Long skuId = cart.getSkuId();
        Boolean aBoolean = hash.hasKey(skuId.toString());
        if(aBoolean){
            //这里说明有 修改num
            String sku = hash.get(skuId.toString()).toString();
            //通过工具类将JSON数据转换为对应的对象
            Cart c = JsonUtils.parse(sku, Cart.class);
            //将原来的num值加上自己新添加的数量
            c.setNum(c.getNum()+cart.getNum());
            //将已经修改完的对象重新转换为JSON对象并存入到redis
            hash.put(cart.getSkuId().toString(),JsonUtils.serialize(c.toString()));
        }else{
            //只要你登录 对购物车进行一定的操作 到拦截器 拦截器就会根据token解析 从而可以获取到UserInfo
            //现在我们的cart中有skuid和num
            //我们根据方法通过skuid查询到sku
            Sku sku = cartClient.selectSkuById(cart.getSkuId());

            cart.setUserId(u.getId());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setImage(sku.getImages().split(",")[0]);
            cart.setOwnSpec(sku.getOwnSpec());
            //然后将cart对象存放到我们的redis中
            hash.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));

        }
    }

    public List<Cart> queryCart() {
        UserInfo u = UserIntercept.getUserInfo();
        Long userId = u.getId();
        //大Key是KEY_PREFIX+自己的userid
        String key = KEY_PREFIX+userId;
        //判断redis中该用户是否有购物车 如果没有购物车就不用走后边的代码了
        if(!redisTemplate.hasKey(key)){
            return null;
        }
        //有了大Key可以从redis中查询
        BoundHashOperations<String, Object, Object> hash = redisTemplate.boundHashOps(key);
        //获取它的值   stringObjectObjectBoundHashOperations相当于一个map集合  我们通过key获取到他的值 然后将值存入到list中
        //得到hash中的value的集合
        List<Object> values = hash.values();
        //判断集合中是否有值 没有值说明用户购物车中没有商品 可以直接返回null
        if (CollectionUtils.isEmpty(values)){
            return null;
        }
        //因为我们从hash中取出来的hash值是object类型 所以我们要将 object类型转换为Cart类型 carts是用来存储转换后的数据 用于返回
        List<Cart> carts = new ArrayList<>();
//        values.forEach(v->{
//            Cart parse = JsonUtils.parse(v.toString(), Cart.class);
//            carts.add(parse);
//        });
        //将数据转换为流循环  这样可以提高我们的效率
        values.stream().forEach(v->{
            Cart parse = JsonUtils.parse(v.toString(), Cart.class);
             carts.add(parse);
        });

        return carts;
    }

    public void putCarts(Cart cart) {
        UserInfo u = UserIntercept.getUserInfo();
        Long userId = u.getId();
        //大Key是KEY_PREFIX+自己的userid
        String key = KEY_PREFIX+userId;
        //有了大Key可以从redis中查询 查询出该用户下面的购物车都有什么
        BoundHashOperations<String, Object, Object> hash = redisTemplate.boundHashOps(key);
        //查询出来之后 根据要添加商品的id获取出 这个商品在购物车中的信息
        String cart1 = hash.get(cart.getSkuId().toString()).toString();
        //将Json数据转换为对象并且修改
        Cart parse = JsonUtils.parse(cart1, Cart.class);
        parse.setNum(cart.getNum());
        //修改之后再将对象转换为Json格式放入我们的redis中  根据key添加 如果key重复 则覆盖修改
        hash.put(cart.getSkuId().toString(),JsonUtils.serialize(parse));
    }

    public void deleteCart(Long skuId) {
        //获取userInfo 查看是哪个用户要删除数据
        UserInfo u = UserIntercept.getUserInfo();
        Long userId = u.getId();
        //大Key是KEY_PREFIX+自己的userid
        String key = KEY_PREFIX+userId;
        //去redis中查看
        BoundHashOperations<String, Object, Object> hash = redisTemplate.boundHashOps(key);
        //然后直接删除
        hash.delete(skuId.toString());

    }

}
