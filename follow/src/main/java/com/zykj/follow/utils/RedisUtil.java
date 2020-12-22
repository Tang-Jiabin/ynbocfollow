package com.zykj.follow.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 如果key存在的话返回fasle 不存在的话返回true
    public Boolean setNx(String key, String value, Long timeout) {
        Boolean setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent(key, value);
        if (timeout != null) {
            stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
        return setIfAbsent;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }

    public void setList(String key, List<String> listToken) {
        stringRedisTemplate.opsForList().leftPushAll(key, listToken);
    }

    public List<String> getRandomValue(String key, int size) {
        return stringRedisTemplate.opsForSet().randomMembers(key, size);
    }

    public Set<String> getSet(String prefix, String key) {
        Set<String> members = stringRedisTemplate.opsForSet().members(prefix + key);
        return members;
    }

    public Long setSet(String prefix, String key, String value) {
        return stringRedisTemplate.opsForSet().add(prefix + key, value);
    }

    public boolean examineSet(String prefix, String key, String value) {
        return stringRedisTemplate.opsForSet().isMember(prefix + key, value);
    }

    public Integer getSetSize(String prefix, String key) {
        Set<String> set = getSet(prefix, key);

        return set.size();
    }

    public Integer getSetSizeTwo(String prefix, String key) {

        Long size = stringRedisTemplate.opsForSet().size(prefix + key);

        return Integer.valueOf(size.toString());
    }

    public String getZSetPageSort(String prefix, String key, long currentPage, long pageNum) {

        Set<String> reverseRangeWithScores = stringRedisTemplate.opsForZSet().reverseRangeByScore(prefix + key, 0,
                System.currentTimeMillis(), (currentPage - 1) * pageNum, pageNum);

        return JSONObject.toJSONString(reverseRangeWithScores);
    }

//	public void addZSet(String prefix, String key, String v, long t) {
//		stringRedisTemplate.opsForZSet().add(prefix + key, v, t);
//	}

    public void incr(String key) {
        stringRedisTemplate.opsForValue().increment(key, 1);
    }

    public void decrease(String key) {
        stringRedisTemplate.opsForValue().increment(key, -1);
    }

    public void removeUnread(String key) {
        setString(key, "0");
    }

    public void removeSet(String prefix, String key, String value) {
        stringRedisTemplate.opsForSet().remove(prefix + key, value);
    }

    public void incrementScore(String prefix, String key, double value) {
        Double score = stringRedisTemplate.opsForZSet().incrementScore(prefix, key, value);

    }

    public String getTop(String key) {

        Set<ZSetOperations.TypedTuple<String>> rangeWithScores = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, 10);
        return JSONObject.toJSONString(rangeWithScores);

    }

    public Long getListLength(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    /**
     * 存放string类型
     *
     * @param key     key
     * @param data    数据
     * @param timeout 超时间
     */
    public void setString(String key, String data, Long timeout) {
        try {

            stringRedisTemplate.opsForValue().set(key, data);
            if (timeout != null) {
                stringRedisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
            }

        } catch (Exception e) {

        }

    }

    public void setStringSECONDS(String key, String data, Long timeout) {
        try {

            stringRedisTemplate.opsForValue().set(key, data);
            if (timeout != null) {
                stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }

        } catch (Exception e) {

        }

    }

    /**
     * 开启Redis 事务
     *
     * @param
     */
    public void begin() {
        // 开启Redis 事务权限
        stringRedisTemplate.setEnableTransactionSupport(true);
        // 开启事务
        stringRedisTemplate.multi();

    }

    /**
     * 提交事务
     *
     * @param
     */
    public void exec() {
        // 成功提交事务
        stringRedisTemplate.exec();
    }

    /**
     * 回滚Redis 事务
     */
    public void discard() {
        stringRedisTemplate.discard();
    }

    /**
     * 存放string类型
     *
     * @param key  key
     * @param data 数据
     */
    public void setString(String key, String data) {
        setString(key, data, null);
    }

    /**
     * 根据key查询string类型
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        return value;
    }

    /**
     * 根据对应的key删除key
     *
     * @param key
     */
    public Boolean delKey(String key) {
        return stringRedisTemplate.delete(key);

    }

    public void addZset(String key, String value, long score) {
        stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    public Double incrZsetScore(String key, String value, long score) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    public Double scoreZet(String key, String value) {
        return stringRedisTemplate.opsForZSet().score(key, value);
    }

    public Long rankZet(String key, String value) {

        return stringRedisTemplate.opsForZSet().rank(key, value);
    }

    public Long getZsetSize(String key) {

        return stringRedisTemplate.opsForZSet().zCard(key);

    }
}
