package com.denny.microservice.person.feign.client.ribbon;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;

public class CustomRule extends AbstractLoadBalancerRule {


    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

    @Override
    public Server choose(Object key) {
        Server server = null;
        ILoadBalancer loadBalancer = this.getLoadBalancer();
        List<Server> serverList = loadBalancer.getAllServers();
        return serverList.get(0);
    }
}
