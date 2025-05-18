package com.example.demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;
import java.util.Map;

@FeignClient(name="dataClient", url="https://jsonplaceholder.typicode.com")
public interface DataFeignInterface {
    @GetMapping("/posts")
    List<EntityDto> getData();
}
