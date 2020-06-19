package com.leyou.search.com.leyou.feign;

import com.leyou.item.api.SkuApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface SkuClient extends SkuApi {
}
