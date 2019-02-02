# Sprig Cloud 服务调用(Feign)

##	申明式Web服务客户端：Feign

申明式：接口声明，Annotation注解驱动。

Web服务：HTTP的方式作为通讯协议。

客户端：用于服务调用的存根。

Feign：原生并不是Spring Web MVC的实现，基于JAX-RE(Java REST规范)实现。Spring Cloud封装了Feign，使其支持Spring Web MVC。`RestTemplate`、 `HttpMessageConverter`

`RestTemplate`以及Spring Web MVC可以显示地自定义 `HttpMessageConverter`实现。

`RequestResponseBodyMethodProcessor`

`HttpEntityMethodProcessor`

假设，有一个Java接口 `PersonService`，Feigh可以将其声明以HTTP方式调用。



需要服务：

* Eureka Server（注册中心）   	

  启动Eureka 服务集群：Eureka Server1、Eureka Server2，域名为：peer1、peer2。

  配置如下：

  `application-peer1.properties`

  ```java
  spring.application.name=spring cloud eureka server
  
  server.port=9090
  
  #Eureka Server 服务端的HOST
  eureka.instance.hostname=peer1
  
  #取消服务器自我注册
  eureka.client.register-with-eureka=true
  
  #注册中心服务器，不需要检索服务
  eureka.client.fetch-registry=true
  
  peer2.hostname=peer2
  peer2.port=9091
  
  #Eureka Server 服务URL,用于客户端注册和服务发现
  eureka.client.serviceUrl.defaultZone=http://${peer2.hostname}:${peer2.port}/eureka/
  ```

  application-peer2.properties

  ```java
  spring.application.name=spring cloud eureka server
  
  server.port=9091
  
  #Eureka Server 服务端的HOST
  eureka.instance.hostname=peer2
  
  #取消服务器自我注册
  eureka.client.register-with-eureka=true
  
  #注册中心服务器，不需要检索服务
  eureka.client.fetch-registry=true
  
  peer1.hostname=peer1
  peer1.port=9090
  
  #Eureka Server 服务URL,用于客户端注册和服务发现
  eureka.client.serviceUrl.defaultZone=http://${peer1.hostname}:${peer1.port}/eureka/
  ```

  启动类：

  ```java
  @SpringBootApplication
  @EnableEurekaServer
  public class SpringCloudEurekaServer {
  
      public static void main(String[] args) {
          SpringApplication.run(SpringCloudEurekaServer.class, args);
      }
  }
  
  ```

* Feign客户端 (服务消费)：调用Feign申明接口

  * 应用名称：person-client

    * 启动类：`SpringCloudFeignClientApplication`

      ```java
      @SpringBootApplication
      @EnableFeignClients(clients = {PersonService.class})
      @EnableEurekaClient
      public class SpringCloudFeignClientApplication {
      
          public static void main(String[] args) {
              new SpringApplicationBuilder(SpringCloudFeignClientApplication.class)
                      .run(args);
          }
      
      }
      
      ```

    * 客户端调用Controller：`

      ```java
      @RestController
      public class PersonClientController implements PersonService {
      
          @Autowired
          private PersonService personService;
      
          @Override
          public Person save(@RequestBody Person person){
              return personService.save(person);
          }
      
          @Override
          public Collection<Person> findAll(){
              return personService.findAll();
          }
      
      }
      ```

    * 客户端调用测试服务类及测试类：

      ```
      @Service
      public class TestService {
      
          @Autowired
          private PersonService personService;
      
          public Person save(Person person){
              return personService.save(person);
          }
      
          public Collection<Person> findAll(){
              return personService.findAll();
          }
      
      }
      ```

      ```java
      @RunWith(SpringRunner.class)
      @SpringBootTest(classes = {SpringCloudFeignClientApplication.class})
      public class SpingCloudFeignClientApplicationTests {
      
          @Autowired
          private TestService testService;
      
          @Test
          public void contextLoads() {
          }
      
          @Test
          public void testSavePerson(){
              Person person = new Person();
              person.setId(22222l);
              person.setName("张姓名");
              System.out.println(testService.save(person));
          }
      
          @Test
          public void testPersonList(){
              System.out.println(testService.findAll());
          }
      }
      ```

      

    * 应用配置：

      ```java
      spring.application.name=person-client
      
      server.port=6060
      
      eureka.client.serviceUrl.defaultZone=http://peer1:9090/eureka/,http://peer2:9091/eureka/
      ```

* Feign服务端(服务提供)：不一定强制实现Feign申明接口
  * 应用名称：person-service

    * 服务端REST服务：

      ```java
      @RestController
      public class PersonServiceProviderController {
      
          private Map<Long, Person> persons = new ConcurrentHashMap<Long, Person>();
      
          @PostMapping("/person/save")
          public Person save(@RequestBody Person person){
              persons.put(person.getId(), person);
              return person;
          }
      
          @GetMapping("/person/list")
          public Collection<Person> findAll(){
              return persons.values();
          }
      
      }
      ```

    * 启动类：

      ```java
      @SpringBootApplication
      @EnableEurekaClient
      public class SpringCloudFeignServiceProviderApplication {
      
          public static void main(String[] args) {
              new SpringApplicationBuilder(SpringCloudFeignServiceProviderApplication.class)
                      .run(args);
          }
      
      }
      ```

    * 应用配置：

      ```java
      spring.application.name=person-service
      
      server.port=6061
      
      eureka.client.serviceUrl.defaultZone=http://peer1:9090/eureka/,http://peer2:9091/eureka/
      
      #eureka.instance.appname=person-service
      ```

* Feign声明接口(契约) ：定义一种Java强制类型接口。

  * 应用名称：person-api

    * 实体类

      ```java
      @Data
      public class Person {
      
          private Long id;
      
          private String name;
      }
      
      ```

    * 服务契约接口

      ```java
      @FeignClient(value = "person-service") //服务提供方应用的名称
      public interface PersonService {
      
          @PostMapping("/person/save")
          Person save(@RequestBody Person person);
      
          @GetMapping("/person/list")
          Collection<Person> findAll();
      
      }
      ```

### 调用顺序

Postman->person-client->person-service

person-api定义了@FeignClient(value = "person-service")，person-service实际是一个服务提供方的应用名称。

person-client和person-service两个应用注册到了Eureka Server。

person-client可以感知person-service应用存在的，并且Spring Cloud帮助解析`PersonService`中声明的应用名称："person-service"，因此person-client调用`PersonService时，实际路由到person-service的URL。

## 整合Netflix Ribbon

[官方参考文档](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/single/spring-cloud.html#spring-cloud-ribbon)

### 关闭Eureka注册

Ribbon不会使用Eureka。

* 调整客户端的Eureka的配置

  ```properties
  #Ribbon 不使用Eureka
  ribbon.eureka.enabled=false
  ```

* 定义Ribbon的服务列表(服务名称：person-service)

  ```properties
  #配置Ribbon负载均衡的服务列表，如：person-service为服务应用名称
  person-service.ribbon.listOfServers=http://localhost:6061
  ```

###完全关闭Eureka注册：

`不启用Eureka注册，直接使用Feign 和 Ribbon进行客户端服务请求负载均衡`

```java
//@EnableEurekaClient 不启用Eureka注册，直接使用Feign 和 Ribbon进行客户端服务请求负载均衡
```

###自定义Ribbon的规则：

自定义负载均衡规则：

```java
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

```

定义Ribbon自定义配置：

```java
@Configuration
public class CustomConfig {

    @Bean
    public CustomRule custRule() {
       return new CustomRule();
    }
}

```

启动类中使用`@RibbonClient`注解配置自定义规则：

```java
@SpringBootApplication
@EnableFeignClients(clients = {PersonService.class})
@EnableEurekaClient //不启用Eureka注册，直接使用Feign 和 Ribbon进行客户端服务请求负载均衡
//说明CustomConfig配置的Ribbon相关配置将覆盖RibbonClientConfiguration中的配置。如：自定义负载均衡规则将覆盖默认的.
@RibbonClient(value = "person-service", configuration = CustomConfig.class)
public class SpringCloudFeignClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudFeignClientApplication.class)
                .run(args);
    }

}
```

# 

## 整合Netflix  Hystrix

#### 激活Hystrix

客户端激活Hystrix，如下：

```Java
@SpringBootApplication(scanBasePackages = {"com.denny.microservice"})
@EnableFeignClients(clients = {PersonService.class})
@EnableEurekaClient //不启用Eureka注册，直接使用Feign 和 Ribbon进行客户端服务请求负载均衡
//说明CustomConfig配置的Ribbon相关配置将覆盖RibbonClientConfiguration中的配置。如：自定义负载均衡规则将覆盖默认的.
@RibbonClient(value = "person-service", configuration = CustomConfig.class)
@EnableHystrix//激活Hystrix
public class SpringCloudFeignClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudFeignClientApplication.class)
                .run(args);
    }

}
```

#### 自定义回退方法类

在`@FeignClient`配置回退方法类fallback，如：

```Java
@FeignClient(value = "person-service", fallback = PersonServiceFallback.class) //服务提供方应用的名称
public interface PersonService {

    @PostMapping("/person/save")
    Person save(@RequestBody Person person);

    @GetMapping("/person/list")
    Collection<Person> findAll();

}
```

定义回退方法类，如：

```java
@Service
public class PersonServiceFallback implements PersonService {

    @Override
    public Person save(Person person) {
        Person personTmp = new Person();
        return personTmp;
    }

    @Override
    public Collection<Person> findAll() {
        return Collections.emptyList();
    }
}
```

#### 自定义回退工厂类

也可以事项`FallbackFactory`接口定义Fallback Factory，如下：

```java
@Component
public class PersonServiceFallbackFactory implements FallbackFactory<PersonService> {
    @Override
    public PersonService create(Throwable cause) {
        return new PersonService(){
            @Override
            public Person save(Person person) {
                Person personTmp = new Person();
                return personTmp;
            }

            @Override
            public Collection<Person> findAll() {
                return Collections.emptyList();
            }
        };
    }
}
```

在`@FeignClient`配置回退工厂类，如：

```java
@FeignClient(value = "person-service", fallbackFactory = PersonServiceFallbackFactory.class)
public interface PersonService {

    @PostMapping("/person/save")
    Person save(@RequestBody Person person);

    @GetMapping("/person/list")
    Collection<Person> findAll();

}
```



#### 关于配置Hystrix参数

如在application.properties文件配置如下参数：

```properties
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds = 1000
```

在实际应用启动时，应用会自动根据服务提供方应用名称和服务接口方法名生成对应的commandGroup 和 commandKey。如：

commandKey=PersonService#save(Person)

生成的配置项KEY为：

```properties
hystrix.command.PersonService#findAll().execution.isolation.thread.timeoutInMilliseconds = 10000
```

等于默认配置如下：

```properties
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds = 10000
```

相关Hystrix配置参数参考`HystrixCommandProperties` 类，其他配置项如：

```properties
hystrix.command.default.execution.isolation.strategy=THREAD
```



如图：

![image-20190202153856239](/Users/denny/Library/Application Support/typora-user-images/image-20190202153856239.png)

![image-20190202154947942](/Users/denny/Library/Application Support/typora-user-images/image-20190202154947942.png)





