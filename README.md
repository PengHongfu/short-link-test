# 唯一短链生成
使用非加密哈希算法：MurmurHash
```java
//32bit位的哈希值
long number = Hashing.murmur3_32().hashUnencodedChars(longUrl + randomStr).padToLong();
```
转换为62进制缩短长度
> 1346822846 -> 1t9898
> 
## 如何判断哈希冲突
通过Redisson布隆过滤器判断是否冲突

每次程序启动加载生成过的hash Key
```java
    ... implements ApplicationRunner{
    @Override
    public void run(ApplicationArguments args){
        RBloomFilter<String> bloomFilter=redissonClient.getBloomFilter("short_key_bloom");
        bloomFilter.tryInit(10000_0000,0.03);
        List<ShortUrl> list=list(Wrappers.lambdaQuery(ShortUrl.class)
        .select(ShortUrl::getShortKey));
        list.forEach(shortUrl->bloomFilter.add(shortUrl.getShortKey()));
        rbloomFilter=bloomFilter;
    }
}
```
## 如何解决哈希冲突
通过随机字符串凭借后继续生成
```java
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

```
## 短链存储
```sql
CREATE TABLE `short_url` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key` varchar(256) NOT NULL COMMENT '短链接key',
  `long_url` varchar(2048) NOT NULL COMMENT '长链接',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_key` (`key`) USING BTREE
) ENGINE=InnoDB  COMMENT='短链接表';
```
也可以使用redis存储
# 关键依赖
```xml
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.18.0</version>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>${guava.version}</version>
        </dependency>
```