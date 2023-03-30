package com.test;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.test.api.Result;
import com.test.model.ShortUrl;
import com.test.service.ShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@EnableTransactionManagement
@MapperScan({"com.test.mapper"})
@Slf4j
@RestController
@SpringBootApplication
public class ShortLinkTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkTestApplication.class, args);
    }

    @Resource
    private ShortUrlService shortUrlService;

    /**
     * 生成成短链接
     */
    @PostMapping
    public Result create(@RequestParam String url) {
        return Result.success("localhost:8080/a/" + shortUrlService.createKeyByLongUrl(url));
    }

    /**
     * 跳转到长链接
     *
     * @param key 短链接key
     */
    @GetMapping("/a/{key}")
    public void query(@PathVariable String key, HttpServletRequest request, HttpServletResponse response) {
        shortUrlService.getLongUrlByKey(key,request, response);
    }
}
