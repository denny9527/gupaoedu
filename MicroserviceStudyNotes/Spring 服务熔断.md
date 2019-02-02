# Spring 服务熔断 

## 服务熔断(CircuitBreaker)

QPS：Query Per Second

TPS：Transaction Per Second

QPS：经过全链路压测，计算单机极限QPS，集群QPS = 单机QPS * 集群机器数 * 可靠性比率

全链路压测，除了压极限QPS，还有错误数量。

全链路：一个完整的业务流程操作。

##Spring Cloud Hystrix Client

###[官网](https://github.com/Netflix/Hystrix)

Reactive Java框架：

* Java 9 Flow API

* Reactor
* Rxjava（Reactive X）

JDK 中的CompletableFuture。

### 激活Hystrix

通过 `@EnableHystrix`注解激活。

配置信息wiki文档：https://github.com/Netflix/Hystrix/wiki/Configuration



####执行隔离策略：

线程隔离(`THREAD`)：业务执行在一个单独的线程中，并发请求数受线程池中的线程数限制。

信号量隔离(`SEMAPHORE`)：业务执行调用线程，并发请求数受信号量数限制。



#### Hystrix 使用示例

```java
@RestController
public class HystrixDemoController {

    private Random random = new Random();

    /**
     * 当方法调用失败时，
     * fallback 方法{@link #errorContent()}作为替代返回
     *
     * @return
     */
    @GetMapping("/hello_world")
    //设置超时时间
    @HystrixCommand(fallbackMethod = "errorContent",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")}
    )
    public String helloWorld() throws InterruptedException {

        int value = random.nextInt(2000);

        System.out.println("等待时间：" + value + "ms");

        Thread.sleep(Long.valueOf(value));

        return "Hello World!";
    }

    public String errorContent() {
        return "error";
    }
}
```



####对比其他Java执行方式：

Future

```java
public class FutureDemo {

    public static void main(String[] args) {

        ExecutorService service = Executors.newFixedThreadPool(20);

        Future<String> future = service.submit(()->{
            int value = new Random().nextInt(2400);
            Thread.sleep(value);
            return "Hello world!";
        });

        try {
            System.out.println(future.get(2000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            System.out.println("超时保护！");
        }
        service.shutdown();//等待已提交任务都完成后才会shutdown线程池。

    }
}
```

#### Health Endpoint

开启health详细信息：

```java
management.endpoints.web.exposure.include=*

management.endpoint.health.show-details=always

management.endpoints.web.base-path=/actuator
```

详细信息：

```json
{
	status: "UP",
	details: {
		diskSpace: {
			status: "UP",
			details: {
				total: 249000001536,
				free: 46976380928,
				threshold: 10485760
			}
		},
		refreshScope: {
			status: "UP"
		},
		hystrix: {
			status: "UP"
		}
	}
}
```

#### 激活熔断保护

`@EnableCircuitBreaker`激活：`@EnableHystrix`+Spring Cloud功能。

`@EnableHystrix`激活，没有一些Spring Cloud功能。如：/hystrix.stream。

#### Hystrix Endpoint(/hystrix.stream)

启动hystrix.stream 端点：

```java
management.endpoint.hystrix.stream.enabled=true
```





##Spring Cloud Hystrix Dashboard

### Hystrix 监控面板激活

`@EnableHystrixDashboard`

```java
@SpringBootApplication(scanBasePackages = {"com.denny.microservice.spring.hystrix"})
@EnableHystrixDashboard
public class SpringCloudHystrixDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudHystrixDashboardApplication.class, args);
    }
}
```



##整合Netflix Turbine