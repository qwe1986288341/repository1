package com.leyou.start.com.leyou.feign;

import com.leyou.item.api.SpecApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
public interface SpeClient extends SpecApi {
}
