package com.test.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短链接表
 * </p>
 *
 * @author camp_user
 * @since 2023-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("short_url")
public class ShortUrl implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 短链接key
     */
    private String shortKey;

    /**
     * 长链接
     */
    private String longUrl;

    /**
     * 创建时间
     */
    private Date createTime;


}
