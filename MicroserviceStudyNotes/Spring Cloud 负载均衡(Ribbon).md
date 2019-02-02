# Spring Cloud 负载均衡(Ribbon)

## Spring RestTemplate



## Spring Http 消息转换器(HttpMessageConverter)

自定义

编码问题

切换序列化和反序列化协议

## Http Client 适配工厂(ClientHttpRequestFactory)

Spring 实现

* `SimpleClientHttpRequestFactory`

Http Client

* `HttpComponentsClientHttpRequestFactory`

OkHttp

* `OkHttp3ClientHttpRequestFactory`
* 

Ribbon

* `RibbonClientHttpRequestFactory`

实例如下：

```
RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
```



## Http请求拦截器(ClientHttpRequestInterceptor)

`@LoadBalanced`

`LoadBalancerInterceptor`	

负载均衡拦截器

##  整合Netflix Ribbon

`RestTemplate`增加一个`LoadBalanceInterceptor`，调用Netflix中的`LoandBalance`的实现，根据Eureka 客户端应用获取目标服务提供方应用的IP+PORT的信息，轮询方式调用。

### 实际请求客户端

* `LoadBalanceClient`
  * `RibbonLoadBalanceClient`

###负载均衡上下文

* `LoadBalancerContext`
  * `RibbonLoadBalancerContext`

###负载均衡器

* `ILoadBalancer`

  * `BaseLoadBalancer`

    

  * `DynamicServerListLoadBalancer`

    动态服务列表

  * `NoOpLoadBalancer`

  * `ZoneAwareLoadBalancer`

### 负载均衡规则

#### 核心规则接口

* IRule
  * 随机规则：`RandomRule`
  * 最可用规则：`BestAvailableRule`
  * 轮询规则：`RoundRobinRule`
  * 重试实现：`RetryRule`
  * 客户端配置：`ClientConfigEnableRoundRobinRule`
  * 可用性过滤规则：`AvailabilityFilterRule`
  * RT权重规则：`WeightedResponseTimeRule`
  * 避免区域规则：`ZoneAvoidanceRule`

#### PING策略

核心策略接口：

`IPingStrategy`

##### PING接口

* IPing

  * `NoOpPing`
  * `DummyPing`
  * `PingConstant`
  * `PingUrl`

  

##### Discovery Client实现

* `NIWSDiscoveryPing`













