package com.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.test.model.ShortUrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 短链接表 服务类
 * </p>
 *
 * @author camp_user
 * @since 2023-03-29
 */
public interface ShortUrlService extends IService<ShortUrl> {

    String createKeyByLongUrl(String longUrl);

    void getLongUrlByKey(String key, HttpServletRequest request, HttpServletResponse response);
}
