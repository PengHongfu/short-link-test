package com.test.service.impl;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.hash.Hashing;
import com.test.mapper.ShortUrlMapper;
import com.test.model.ShortUrl;
import com.test.service.ShortUrlService;
import com.test.util.NumberHelper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <p>
 * 短链接表 服务实现类
 * </p>
 *
 * @author camp_user
 * @since 2023-03-29
 */
@Slf4j
@Service
public class ShortUrlServiceImpl extends ServiceImpl<ShortUrlMapper, ShortUrl> implements ShortUrlService, ApplicationRunner {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private Executor threadPoolExecutor;
    private RBloomFilter<String> rbloomFilter;

    @Override
    public String createKeyByLongUrl(String longUrl) {
        return this.createKeyByLongUrl(longUrl, "");
    }

    private String createKeyByLongUrl(String longUrl, String randomStr) {
        long number = Hashing.murmur3_32().hashUnencodedChars(longUrl + randomStr).padToLong();
        String key = NumberHelper.baseConver(String.valueOf(number), 10, 62);
        if (rbloomFilter.contains(key)) {
            //冲突之后，随机拼接字符串继续
            return createKeyByLongUrl(longUrl, RandomUtil.randomString(5));
        } else {
            save(new ShortUrl().setShortKey(key).setLongUrl(longUrl));
            rbloomFilter.add(key);
            return key;
        }
    }

    @Override
    public void getLongUrlByKey(String key, HttpServletRequest request, HttpServletResponse response) {
        if (!rbloomFilter.contains(key)) {
            throw new RuntimeException("您输入的短链接不存在！");
        }
        ShortUrl shortUrl = this.getOne(Wrappers.lambdaQuery(ShortUrl.class)
                .eq(ShortUrl::getShortKey, key), false);
        if (Objects.isNull(shortUrl)) {
            throw new RuntimeException("您输入的短链接不存在！");
        }
        try {
            CompletableFuture.runAsync(() -> saveRecord(shortUrl, request), threadPoolExecutor);
            response.sendRedirect(shortUrl.getLongUrl());
        } catch (IOException e) {
            throw new RuntimeException("长链接不合法！");
        }
    }

    /**
     * 记录访问日志
     */
    private void saveRecord(ShortUrl shortUrl, HttpServletRequest request) {
        String ip = ServletUtil.getClientIP(request);
        String header = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgentUtil.parse(header);
        HttpSession session = request.getSession();
        session.getId();
        //保存到数据库...
    }

    @Override
    public void run(ApplicationArguments args) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("short_key_bloom");
        bloomFilter.tryInit(10000_0000, 0.03);
        List<ShortUrl> list = list(Wrappers.lambdaQuery(ShortUrl.class)
                .select(ShortUrl::getShortKey));
        list.forEach(shortUrl -> bloomFilter.add(shortUrl.getShortKey()));
        rbloomFilter = bloomFilter;
    }
}
