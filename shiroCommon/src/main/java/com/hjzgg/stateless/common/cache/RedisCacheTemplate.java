package com.hjzgg.stateless.common.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * redis缓存操作实现类
 */
public class RedisCacheTemplate {
    private static final String DEFAULT_KEY_SPACE = "default_cache";
    private RedisCache redisCache;
    private RedisTemplate redisTemplate;

    private RedisTemplate<String, Object> stringObjectRedisTemplate;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @PostConstruct
    public void init() {
        redisCache = (RedisCache) redisCacheManager.getCache(DEFAULT_KEY_SPACE);
        redisTemplate = (RedisTemplate) redisCache.getNativeCache();
        stringObjectRedisTemplate = (RedisTemplate<String, Object>)redisCache.getNativeCache();
    }

    /**
     * 写入缓存
     *
     * @param key   键
     * @param value 值
     */

    public void put(final String key, final Object value) {
        redisCache.put(addPrefix(key), value);
    }

    /**
     * 写入缓存
     *
     * @param key     键
     * @param value   值
     * @param timeout 有效时间（单位：秒）
     */

    public void put(final String key, final Object value, final long timeout) {
        this.put(key, value);
        redisTemplate.expire(addPrefix(key), timeout, TimeUnit.SECONDS);
    }

    /**
     * 写入缓存
     *
     * @param key        键
     * @param value      值
     * @param expireTime 过期时间
     */

    public void put(final String key, final Object value, final Date expireTime) {
        this.put(key, value);
        redisTemplate.expireAt(addPrefix(key), expireTime);
    }

    /**
     * 写入缓存（仅当不存在时写入）
     *
     * @param key   键
     * @param value 值
     */

    public void putIfAbsent(final String key, final Object value) {
        redisCache.putIfAbsent(key, value);
    }

    /**
     * 写入缓存（仅当不存在时写入）
     *
     * @param key     键
     * @param value   值
     * @param timeout 有效时间（单位：秒）
     */

    public void putIfAbsent(final String key, final Object value, final long timeout) {
        redisTemplate.execute(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                connection.set(keySerializer.serialize(addPrefix(key)), valueSerializer.serialize(value),
                        Expiration.seconds(timeout), RedisStringCommands.SetOption.SET_IF_ABSENT);
                return null;
            }
        });
    }

    /**
     * 写入缓存（仅当不存在时写入）
     *
     * @param key        键
     * @param value      值
     * @param expireTime 过期时间
     */

    public void putIfAbsent(final String key, final Object value, final Date expireTime) {
        redisTemplate.execute(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                connection.set(keySerializer.serialize(addPrefix(key)), valueSerializer.serialize(value),
                        Expiration.milliseconds(expireTime.getTime() - new Date().getTime()),
                        RedisStringCommands.SetOption.SET_IF_ABSENT);
                return null;
            }
        });
    }

    /**
     * 批量写入缓存
     *
     * @param keyValues 键值对
     */

    public void batchPut(final Map<String, Object> keyValues) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    connection.set(keySerializer.serialize(addPrefix(entry.getKey())), valueSerializer.serialize(entry.getValue()));
                }
                return null;
            }
        });
    }

    /**
     * 批量写入缓存
     *
     * @param keyValues 键值对
     * @param timeout   有效时间（单位：秒）
     */

    public void batchPut(final Map<String, Object> keyValues, final long timeout) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    connection.set(keySerializer.serialize(addPrefix(entry.getKey())), valueSerializer.serialize(entry.getValue()),
                            Expiration.seconds(timeout), RedisStringCommands.SetOption.UPSERT);
                }
                return null;
            }
        });
    }

    /**
     * 批量写入缓存
     *
     * @param keyValues  键值对
     * @param expireTime 过期时间
     */

    public void batchPut(final Map<String, Object> keyValues, final Date expireTime) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    connection.set(keySerializer.serialize(addPrefix(entry.getKey())), valueSerializer.serialize(entry.getValue()),
                            Expiration.milliseconds(expireTime.getTime() - new Date().getTime()),
                            RedisStringCommands.SetOption.UPSERT);
                }
                return null;
            }
        });
    }

    /**
     * 批量写入缓存（仅当不存在时写入）
     *
     * @param keyValues 键值对
     */

    public void batchPutIfAbsent(final Map<String, Object> keyValues) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    connection.set(keySerializer.serialize(addPrefix(entry.getKey())), valueSerializer.serialize(entry.getValue()),
                            Expiration.persistent(), RedisStringCommands.SetOption.SET_IF_ABSENT);
                }
                return null;
            }
        });
    }

    /**
     * 批量写入缓存（仅当不存在时写入）
     *
     * @param keyValues 键值对
     * @param timeout   有效时间（单位：秒）
     */

    public void batchPutIfAbsent(final Map<String, Object> keyValues, final long timeout) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    connection.set(keySerializer.serialize(addPrefix(entry.getKey())), valueSerializer.serialize(entry.getValue()),
                            Expiration.seconds(timeout), RedisStringCommands.SetOption.SET_IF_ABSENT);
                }
                return null;
            }
        });
    }

    /**
     * 批量写入缓存（仅当不存在时写入）
     *
     * @param keyValues  键值对
     * @param expireTime 过期时间
     */

    public void batchPutIfAbsent(final Map<String, Object> keyValues, final Date expireTime) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                    connection.set(keySerializer.serialize(addPrefix(entry.getKey())), valueSerializer.serialize(entry.getValue()),
                            Expiration.milliseconds((expireTime.getTime() - new Date().getTime())),
                            RedisStringCommands.SetOption.SET_IF_ABSENT);
                }
                return null;
            }
        });
    }

    /**
     * 读取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        Cache.ValueWrapper wrapper = redisCache.get(this.addPrefix(key));
        return wrapper == null ? null : wrapper.get();
    }

    /**
     * @param key 键
     * @param cls class对象
     * @return 值
     */

    public <T> T get(String key, Class<T> cls) {
        return this.get(key) == null ? null : (T) this.get(this.addPrefix(key));
    }

    /**
     * 读取缓存（不存在时调用接口获取，并存入缓存）
     *
     * @param key      键
     * @param callable 缓存中不存在时，获取值的接口
     * @return 值
     */

    public <T> T get(String key, Callable<T> callable) {
        return redisCache.get(this.addPrefix(key), callable);
    }

    /**
     * 批量读取缓存
     *
     * @param keys 键集合
     * @return 值集合
     */

    public List<Object> batchGet(Collection<String> keys) {
        Collection<String> newKeys = new ArrayList<>();
        for (String key : keys) {
            newKeys.add(this.addPrefix(key));
        }
        return redisTemplate.opsForValue().multiGet(newKeys);
    }

    /**
     * 批量读取缓存
     *
     * @param keys 键集合
     * @param cls  class对象
     * @return 值集合
     */

    public <T> List<T> batchGet(Collection<String> keys, Class<T> cls) {
        Collection<String> newKeys = new ArrayList<>();
        for (String key : keys) {
            newKeys.add(this.addPrefix(key));
        }
        return redisTemplate.opsForValue().multiGet(newKeys);
    }

    /**
     * 清除缓存
     *
     * @param key 键
     */

    public void delete(String key) {
        redisCache.evict(key);
    }

    /**
     * 批量清除缓存
     *
     * @param keys 键集合
     */

    public void batchDelete(Collection<String> keys) {
        Collection<String> newKeys = new ArrayList<>();
        for (String key : keys) {
            newKeys.add(this.addPrefix(key));
        }
        redisTemplate.delete(newKeys);
    }

    /**
     * 设置缓存有效时间
     *
     * @param key     键
     * @param timeout 有效时间（单位：秒）
     */

    public void expire(String key, final long timeout) {
        redisTemplate.expire(addPrefix(key), timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置缓存过期时间
     *
     * @param key        键
     * @param expireTime 过期时间
     */

    public void expireAt(String key, final Date expireTime) {
        redisTemplate.expireAt(addPrefix(key), expireTime);
    }

    /**
     * 批量设置缓存有效时间
     *
     * @param keys    键集合
     * @param timeout 有效时间（单位：秒）
     */

    public void batchExpire(final Collection<String> keys, final long timeout) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                for (String key : keys) {
                    connection.expire(keySerializer.serialize(addPrefix(key)), timeout);
                }
                return null;
            }
        });
    }

    /**
     * 批量设置缓存过期时间
     *
     * @param keys       键集合
     * @param expireTime 过期时间
     */

    public void batchExpireAt(final Collection<String> keys, final Date expireTime) {
        redisTemplate.executePipelined(new RedisCallback() {

            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                for (String key : keys) {
                    connection.expireAt(keySerializer.serialize(addPrefix(key)), expireTime.getTime() / 1000);
                }
                return null;
            }
        });
    }

    /**
     * 是否存在缓存
     *
     * @param key 键
     * @return 存在->true;不存在->false
     */

    public boolean exist(String key) {
        return redisTemplate.hasKey(addPrefix(key));
    }

    /**
     * 清除所有缓存
     */

    public void clear() {
        redisCache.clear();
    }

    /**
     * @MethodName:
     * @Description: TODO
     * @author: hujunzheng
     * @Date: 2017/7/19 下午1:04
     *
     * @Return:
     * @Parameter:
     */
    public void hPut(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(addPrefix(key), hashKey, value);
    }

    /**
     * @MethodName: hGet
     * @Description: 根据 hash值 获取value
     * @author: hujunzheng
     * @Date: 2017/7/19 下午1:04
     *
     * @Return:
     * @Parameter:
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(addPrefix(key), hashKey);
    }

    public List<Object> hMultiGet(String key, List<String> hashKeys) {
        return redisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * @MethodName: hExist
     * @Description: 判断 hash值是否存在
     * @author: hujunzheng
     * @Date: 2017/7/19 下午1:04
     *
     * @Return:
     * @Parameter:
     */
    public boolean hExist(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(addPrefix(key), hashKey);
    }


    public void hDel(String key, String hashKey) {
        redisTemplate.opsForHash().delete(addPrefix(key), hashKey);
    }


    public void hMultiDel(String key, List<String> hashKeys) {
        redisTemplate.opsForHash().delete(addPrefix(key), hashKeys);
    }

    /**
     * @MethodName: getTTL
     * @Description: 获取key的过期时间
     * @author: hujunzheng
     * @Date: 2017/7/19 下午1:05
     *
     * @Return:
     * @Parameter:
     */
    public long getTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 给键添加前缀
     *
     * @param key 键
     * @return 添加前缀后的键
     */
    private String addPrefix(String key) {
        return redisCache.getName() + ":" + key;
    }

    public Set<Object> hKeys(String key) {
        return stringObjectRedisTemplate.opsForHash().keys(addPrefix(key));
    }
}