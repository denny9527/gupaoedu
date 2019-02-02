#Spring Cloud 云原生应用

##主要内容

###Bootstrap 应用上下文

####元原生

[官方文档](https://cloud.spring.io/spring-cloud-static/Finchley.SR2/single/spring-cloud.html#_cloud_native_applications)

Spring 应用上下文的层次性？

`Component`”派生性“

#####Spring 事件

事件类：`ApplicationEvent`

​		`PayloadApplicationEvent`自定义事件

事件监听：`ApplicationListener`

事件广播：`ApplicationEventMutilcaste`r->`SimpleApplicationEventMutilcaster`

事件发送器：

##### Spring 应用上下文-`ApplicationContext`

#####理解上下文层次

`ApplicationContext`

`BeanFacotory`和`ApplicationContext`

`ApplicationContext`扩展`ListableBeanFactory`以及`HierarchicalBeanFactory`

装饰者模式：继承并扩展，底层实现基于被扩展实例。

`BeanFacoty`管理上下文。

`ApplicatonContext`包含了`BeanFactory`职责，并且扩展了功能。



 `BeanFactory`才是真正的Bean容器，管理Bean的生命周期。

从结构而言，`ApplicationContext`关联了`BeanFactory`实现：

* `AbstractRefreshableApplicationContext`
  * 属性：beanFactory->`DefaultListableBeanFactory`

`ApplicatonContext`继承了`HierarchicalBeanFactory`。

`ApplicatonContext`Bean生命周期的管理能力，来自于`BeanFactory`，并且它又是`HierarchicalBeanFactory`的接口，说明它具备`BeanFactory`的层次关系。

* Parent`ApplicatonContext`
  * Child `ApplicatonContext`

* Parent`ClassLoader`
  * Child `ClassLoader`

Spring Boot 1.x 默认情况ApplicationContext

如果独立管理上下文，有两个ApplicationContext



Spring Boot 2.x 合并为一个ApplicationContext



Spring Cloud会增加Bootstrap ApplicationContext

##### 理解Bootstrap 应用上下文

###### 关键调用实现

`org.springframework.boot.builder.ParentContextApplicationContextInitializer`

`org.springframework.cloud.bootstrap.BootstrapApplicationListener`

- Spring Cloud 实现
  - 监听事件 `ApplicationEnvironmentPreparedEvent`

#####理解Spring boot Actuator Endpoints

```
#开发所有Web管理EndPoint
management.endpoints.web.exposure.include=*
```

###### 服务上下文和管理上下文独立：

```
server.port=8090

#管理服务端口
management.server.port=9001

```

Spring Boot 和 Spring Cloud的上下文层次：

```
http://127.0.0.1:9001/actuator/beans

{
	contexts: {
		application-1: {
		beans: {},
		parentId: "bootstrap"
	},
	bootstrap: {}
	}
}
```

子上下文必须晚于双亲上下文启动。bootstrap->application-1

是不是意味着bootstrap要提早加载什么资源？

######获取环境端口(/env)



```
http://127.0.0.1:9001/actuator/env
{
activeProfiles: [ ],
propertySources: [
{},
{
name: "commandLineArgs",
properties: { }
},
{
name: "servletContextInitParams",
properties: { }
},
{
name: "systemProperties",
properties: {}
},
{
name: "systemEnvironment",
properties: {}
},
{
name: "applicationConfig: [classpath:/application.properties]",
properties: {}
},
{
name: "springCloudClientHostInfo",
properties: {}
},
{
name: "defaultProperties",
properties: {}
}
]
}
```





#####理解Environment

######Spring boot 外部化配置官方文档

[Spring 外部化配置官方文文档](https://docs.spring.io/spring-boot/docs/2.1.2.RELEASE/reference/htmlsingle/#boot-features-external-config)

配置加载顺序：

```java
1、Devtools global settings properties on your home directory (~/.spring-boot-devtools.properties when devtools is active).
2、@TestPropertySource annotations on your tests.
3、properties attribute on your tests. Available on @SpringBootTest and the test annotations for testing a particular slice of your application.
4、Command line arguments.
5、Properties from SPRING_APPLICATION_JSON (inline JSON embedded in an environment variable or system property).
6、ServletConfig init parameters.
7、ServletContext init parameters.
8、JNDI attributes from java:comp/env.
9、Java System properties (System.getProperties()).
10、OS environment variables.
11、A RandomValuePropertySource that has properties only in random.*.
12Profile-specific application properties outside of your packaged jar (application-{profile}.properties and YAML variants).
13、Profile-specific application properties packaged inside your jar (application-{profile}.properties and YAML variants).
14、Application properties outside of your packaged jar (application.properties and YAML variants).
15、Application properties packaged inside your jar (application.properties and YAML variants).
16、@PropertySource annotations on your @Configuration classes.
17、Default properties (specified by setting SpringApplication.setDefaultProperties).
```



#### 端点介绍

Sprng Cloud 在Spring Boot的基础上引入的端点(EndPoint)，比如：上下文重启(/restart)、生命周期控制 (如：/pause、/resume)等。

`RestartEndpoint`



