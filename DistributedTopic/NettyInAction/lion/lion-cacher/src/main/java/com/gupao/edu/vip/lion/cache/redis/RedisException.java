package com.gupao.edu.vip.lion.cache.redis;

/**
 *
 *
 */
public class RedisException extends RuntimeException {

    public RedisException() {
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }
}
