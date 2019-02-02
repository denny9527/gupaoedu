# Spring Cloud 服务发现

## 简单对比Eureka

| 比较点     | Eureka                                           | Zookeeper                  | Consul                   |
| ---------- | :----------------------------------------------- | -------------------------- | ------------------------ |
| 运维熟悉度 | 陌生                                             | 熟悉                       | 更陌生                   |
| 一致性     | AP                                               | CP                         | AP(最终一致性)           |
| 一致性协议 | HTTP定时轮询                                     | ZAB                        | R                        |
| 通讯协议   | HTTP REST                                        | 自定义协议                 | HTTP REST                |
| 更新机制   | Peer2Peer (服务器之间)+Scheduler(服务器和客户端) | ZK Watch                   | Agent监听的方式          |
| 适用规模   | 20K-30K实例（节点）                              | 10K-20K实例（节点）        | <3K实例（节点）          |
| 性能问题   | 简单的更新机制、负责设计、规模设计时GC频繁       | 扩容麻烦、规模较大时GC频繁 | 3K节点以上，更新列表缓慢 |

### 为什么推荐使用ZK作为Spring Cloud的基础设施

Zookeeper：CP(强一致性、非高可用)



### 高可用的计算

可用性比率计算

通过时间来计算（一年或一个月）

比如：一年 99.99% 

可用时间：365 * 24 * 3600 * 99.99%

不可用时间：365 * 24 * 3600 * 0.01% = 3153.6秒  < 1小时

不可用时间：1小时 推算一年 1/24/365 = 0.000114=0.01%

24 * 7



单机不可用比率：1%

两台不可用比率：1% * 1%

N台不可用比率：1%^N

### 可靠性

微服务里面的问题：N台机器，可靠性99%^N

A -> B -> C

0.99 * 0.99 * 0.99 = 97%

服务链路越长可靠性会降低。



结论：增加机器可以提供可用性，增加服务调用会降低可靠性，同时降低了可用性。





## 主要内容

### Eureka 客户端

客户端(消费者、提供者)注解：`@EnableEurekaClient`

单点Eureka Client(消费者、提供者)相关配置(application.properties)：

```
spring.application.name=user-service-provider

#消费者和提供者服务端口号
server.port=8090

eureka.servre.port=9090

#Eureka Server 服务 URL，用户客户服务注册和发现
eureka.client.serviceUrl.defaultZone=http://peer1:${eureka.servre.port}/eureka/

management.endpoints.web.exposure.include=*
```

### Eureka 服务端

Eureka 服务端一般不需要自我注册，也不需要注册其他的服务器。

服务端注解：`@EnableEurekaServer`

单点Eureka Server相关配置(application.properties)：

```java
spring.application.name=spring cloud eureka server

server.port=9090

#取消服务器自我注册
eureka.client.register-with-eureka=false

#单点注册中心服务器，不需要获取注册服务
eureka.client.fetch-registry=false

#Eureka Server 服务URL,用于客户端注册和服务发现
eureka.instance.hostname=peer1
eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

## Eureka  高可用（Replicas）架构

### 客户端高可用

只需要增加Eureka服务注册集群URL配置，如：

```
#Eureka 服务器集群配置，客户端配置
eureka.client.serviceUrl.defaultZone=http://peer1:9090/eureka/,http://peer2:9091/eureka/
```

如果Eureka客户端应用配置了多个Eureka注册服务器，那么默认情况只用第一台可用的服务器存在服务注册信息。如果第一台可用的Eureka服务器宕机，那么Eureka客户端将选择下一台可用的Eureka服务器。



#### 客户端配置源码(EurekaClientConfigBean)

```java
@ConfigurationProperties(EurekaClientConfigBean.PREFIX)
public class EurekaClientConfigBean implements EurekaClientConfig, Ordered {

	public static final String PREFIX = "eureka.client";

	public static final String DEFAULT_URL = "http://localhost:8761" + DEFAULT_PREFIX
			+ "/";

	public static final String DEFAULT_ZONE = "defaultZone";

	private static final int MINUTES = 60;
	...
	private Map<String, String> serviceUrl = new HashMap<>();

	{
		this.serviceUrl.put(DEFAULT_ZONE, DEFAULT_URL);
	}
	...
}
```

配置项`eureka.client.serviceUrl.defaultZone`实际上映射字段：serviceUrl。字段：serviceUrl 为Map类型，KEY为自定义，默认KEY有`defaultZone`，对应的值是需要配置的Eureka注册服务器的URL。Value的值可以配置多个Eureka服务器URL，以逗号分割。如：

```java
	@Override
	public List<String> getEurekaServerServiceUrls(String myZone) {
		String serviceUrls = this.serviceUrl.get(myZone);
		if (serviceUrls == null || serviceUrls.isEmpty()) {
			serviceUrls = this.serviceUrl.get(DEFAULT_ZONE);
		}
		if (!StringUtils.isEmpty(serviceUrls)) {
			final String[] serviceUrlsSplit = StringUtils.commaDelimitedListToStringArray(serviceUrls);
			List<String> eurekaServiceUrls = new ArrayList<>(serviceUrlsSplit.length);
			for (String eurekaServiceUrl : serviceUrlsSplit) {
				if (!endsWithSlash(eurekaServiceUrl)) {
					eurekaServiceUrl += "/";
				}
				eurekaServiceUrls.add(eurekaServiceUrl.trim());
			}
			return eurekaServiceUrls;
		}

		return new ArrayList<>();
	}
```



#### 获取注册信息间隔时间

Eureka客户端需要获取Eureka服务端的服务注册信息，以方便服务调用。

Eureka客户端：`com.netflix.discovery.EurekaClient`

关联的应用集合：`Applications`

单个应用信息：`Application`关联多个应用实例

单个应用实例：`InstanceInfo`

当Eureka客户端需要调用具体某个服务时，比如：`user-serice-client`调用`user-service-provider`，那么`user-service-provider`实际对应对象是`Application`，关联了多个应用实例(`InstanceInfo`) 。如果`user-service-provider`的应用实例发生变化时，那么`user-serice-client`时需要感知的。比如：`user-service-provider`服务器从10台降到了5台，作为服务调用方`user-serice-client`需要知晓这个情况，可是这个变化过程，可能存在一定的延迟，可以通过调整获取注册信息间隔时间来减少错误。

具体的配置项：

```java
#调整客户端从Eeruka服务器获取服务注册信息的间隔时间
eureka.client.registry-fetch-interval-seconds=30
```

#### 实例信息复制时间间隔

具体是客户端信息的上报到Eureka服务器时间。当Eureka客户端应用上报的频率越高，那么Eureka服务器的应用状态管理一致性就越高。

具体的配置项：

```java
#调整客户端应用状态上报至Eureka服务器的间隔时间
eureka.client.instance-info-replication-interval-seconds=5
```

Eureka的应用信息同步的方式：拉模式。

Eureka的应用信息上报同步的方式：推模式。

#### 实例ID

从Eureka Server Dashboard里面可以看到某个应用中的实例信息，比如：

```java
USER-SERVICE-PROVIDER	n/a (2)	(2)	UP (2) - znode1:user-service-provider:7008 , znode1:user-service-provider:7005
```

其中，它们的命名模式为：`{hostname}:${spring.application.name}:​${server.port}`

实例类：`EurekaInstanceConfigBean`

配置项：`eureka.instance.instance-id`(修改客户端应用实例名称)

#### 实例端点映射

配置类：`EurekaInstanceConfigBean`

配置项：

```java
#Eureka 应用实例状态URL
eureka.instance.status-page-url-path=/health
```



### 服务端高可用

构建Eureka服务去相互注册，配置如下：

* Eureka Server1->profile=peer1

  `application-peer1.properties`

  ```java
  spring.application.name=spring cloud eureka server
  
  server.port=9090
  
  #Eureka Server 服务URL,用于客户端注册和服务发现
  eureka.instance.hostname=peer1
  
  #启动服务器自我注册
  eureka.client.register-with-eureka=true
  
  #注册中心服务器同时作为客户端，需要检索服务
  eureka.client.fetch-registry=true
  
  peer2.hostname=peer2
  peer2.port=9091
  #建议使用域名，或非127.0.0.1的本地IP。
  eureka.client.serviceUrl.defaultZone=http://${peer2.hostname}:${peer2.port}/eureka/
  ```

  

* Eureka Server2->profile=peer2

  `application-peer2.properties`

  ```java
  spring.application.name=spring cloud eureka server
  
  server.port=9091
  
  #Eureka Server 服务URL,用于客户端注册和服务发现
  eureka.instance.hostname=peer2
  
  #启动服务器自我注册
  eureka.client.register-with-eureka=true
  
  #注册中心服务器同时作为客户端，需要检索服务
  eureka.client.fetch-registry=true
  
  peer1.hostname=peer1
  peer1.port=9090
  #建议使用域名，或非127.0.0.1的本地IP。
  eureka.client.serviceUrl.defaultZone=http://${peer1.hostname}:${peer1.port}/eureka/
  ```

  注意点：`eureka.client.serviceUrl.defaultZone`配置中请使用域名；Eureka Server1和Eureka Server2中的`eureka.instance.hostname`请采用不同的域名，不用使用IP地址。

  在application.properties中配置`spring.profiles.active`参数或在启动命令行增加`spring.profiles.active=peer1`参数来启动多个Eureka服务器。

  集群部署示意图：

  ![image-20190131192325650](/Users/denny/Library/Application Support/typora-user-images/image-20190131192325650.png)

  

  ![image-20190131190855433](/Users/denny/Library/Application Support/typora-user-images/image-20190131190855433.png)

​       

###Spring Cloud Discovery 客户端



###Spring Cloud Discovery ZK服务端

