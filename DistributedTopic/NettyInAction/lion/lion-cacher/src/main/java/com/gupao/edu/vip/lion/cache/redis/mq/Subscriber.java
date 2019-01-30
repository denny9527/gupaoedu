
package com.gupao.edu.vip.lion.cache.redis.mq;

import com.gupao.edu.vip.lion.tools.Jsons;
import com.gupao.edu.vip.lion.tools.log.Logs;
import redis.clients.jedis.JedisPubSub;

public final class Subscriber extends JedisPubSub {
    private final ListenerDispatcher listenerDispatcher;

    public Subscriber(ListenerDispatcher listenerDispatcher) {
        this.listenerDispatcher = listenerDispatcher;
    }

    @Override
    public void onMessage(String channel, String message) {
        Logs.CACHE.info("onMessage:{},{}", channel, message);
        listenerDispatcher.onMessage(channel, message);
        super.onMessage(channel, message);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        Logs.CACHE.info("onPMessage:{},{},{}", pattern, channel, message);
        super.onPMessage(pattern, channel, message);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        Logs.CACHE.info("onPSubscribe:{},{}", pattern, subscribedChannels);
        super.onPSubscribe(pattern, subscribedChannels);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        Logs.CACHE.info("onPUnsubscribe:{},{}", pattern, subscribedChannels);
        super.onPUnsubscribe(pattern, subscribedChannels);
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        Logs.CACHE.info("onSubscribe:{},{}", channel, subscribedChannels);
        super.onSubscribe(channel, subscribedChannels);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        Logs.CACHE.info("onUnsubscribe:{},{}", channel, subscribedChannels);
        super.onUnsubscribe(channel, subscribedChannels);
    }


    @Override
    public void unsubscribe() {
        Logs.CACHE.info("unsubscribe");
        super.unsubscribe();
    }

    @Override
    public void unsubscribe(String... channels) {
        Logs.CACHE.info("unsubscribe:{}", Jsons.toJson(channels));
        super.unsubscribe(channels);
    }

}
