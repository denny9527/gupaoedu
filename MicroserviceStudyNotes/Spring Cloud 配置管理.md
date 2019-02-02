## Spring Cloud 配置管理

### 分布式配置

#### 国内知名开源项目

百度 Disconf

携程 Apollo

阿里 Nacos



### 客户端

#### 第三方库

`commons-configuration`

* `Configuration`：提供大多数常见类型的Value转换

  * `PropertiesConfiguration` ：将Properties作为配置

  * `MapConfiguration`

    * `EnvironmentConfiguration`：OS环境变量作为配置
    * `SystemConfiguration` ： Java系统变量

  * `CompositeConfiguration`：组合配置，适用于多个配置源

    

核心概念：配置源、以及它们的优先次序、配置转换能力

配置源：文件、HTTP资源、数据源、Git

URL：file://、http://、jdbc://、 git://



#### Spring Enviroment

```sequence
Enviroment -> ConfigurableEnvironment:父子关系
ConfigurableEnvironment ->MutablePropertySources:获取可变多个配置源
MutablePropertySources -> List PropertySource:包含多个配置源
```

`PropertySource` ：配置源

* `MapPropertySource`
  * `PropertiesPropertySource`
* `CompositePropertySource`：组合
* `SystemEnvironmentPropertySource`：系统环境变量

Spring Config 客户端配置定位扩展：`PropertySourceLocator`

默认为：`ConfigServicePropertySourceLocator`

### 服务端

#### 基于Git实现

版本化配置

/应用名/profile/${label}

/应用名/profile=应用名/profile/master

/应用名=/应用名.properties

${label}：分支

HTTP访问资源格式：

```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

请求处理类：

`EnvironmentConverter`

```
@RequestMapping({"/{name}/{profiles:.*[^-].*}"})
@RequestMapping({"/{name}/{profiles}/{label:.*}"})
@RequestMapping({"/{name}-{profiles}.properties"})
@RequestMapping({"/{label}/{name}-{profiles}.properties"})
...
```



Spring Cloud Config实现了一套完整的配置管理API设计。

Git实现缺陷：

* 复杂的版本更新机制
  * 版本
  * 分支
  * 提交
  * 配置
* 憋足的内容更新(实时性不高)
  * 客户端第一次启动拉取
  * 需要整合BUS做更新通知

#### 设计原理

分析`@EnableConfigServer`

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ConfigServerConfiguration.class})
public @interface EnableConfigServer {
}

```

实际配置类`@ConfigServerConfiguration`：

```
@Configuration
public class ConfigServerConfiguration {
    public ConfigServerConfiguration() {
    }

    @Bean
    public ConfigServerConfiguration.Marker enableConfigServerMarker() {
        return new ConfigServerConfiguration.Marker();
    }

    class Marker {
        Marker() {
        }
    }
}
```

```
@Configuration
@ConditionalOnBean({Marker.class})
@EnableConfigurationProperties({ConfigServerProperties.class})
@Import({EnvironmentRepositoryConfiguration.class, CompositeConfiguration.class, ResourceRepositoryConfiguration.class, ConfigServerEncryptionConfiguration.class, ConfigServerMvcConfiguration.class})
public class ConfigServerAutoConfiguration {
    public ConfigServerAutoConfiguration() {
    }
}
```

当配置类标注了

* `@`EnableConfigServer
  * 导入`ConfigServerConfiguration`
    * 注册`Marker`Bean
      * 作为`ConfigServerAutoConfiguration`条件之一

##### 案例分析JDBC实现

* `JdbcTemplate` Bean的来源

  * `JdbcTemplateAutoConfiguration`

* SQL来源

  * `JdbcEnvironmentProperties`

    * 不配做，默认为：`DEFAULT_SQL`

      *  

        ```
        SELECT KEY, VALUE from PROPERTIES where APPLICATION=? and PROFILE=? and LABEL=?
        ```

    | KEY    | VALUE      | APPLICATION | PROFILE | LABEL  |
    | ------ | ---------- | ----------- | ------- | ------ |
    | name   | zhangkui   | config      | default | master |
    | master | wangdandan | config      | test    | master |
    |        |            |             |         |        |

  本质说明：

  JDBC连接技术

  DB存储介质

   `EnvironmentRepository`核心接口

  

  思考是否可以自定义`EnvironmentRepository`实现？

  为什么默认采用Git作为配置仓库？

  ```
  @Configuration
  @ConditionalOnMissingBean(
      value = {EnvironmentRepository.class},
      search = SearchStrategy.CURRENT
  )
  class DefaultRepositoryConfiguration {
      @Autowired
      private ConfigurableEnvironment environment;
      @Autowired
      private ConfigServerProperties server;
      @Autowired(
          required = false
      )
      private TransportConfigCallback customTransportConfigCallback;
  
      DefaultRepositoryConfiguration() {
      }
  
      @Bean
      public MultipleJGitEnvironmentRepository defaultEnvironmentRepository(MultipleJGitEnvironmentRepositoryFactory gitEnvironmentRepositoryFactory, MultipleJGitEnvironmentProperties environmentProperties) throws Exception {
          return gitEnvironmentRepositoryFactory.build(environmentProperties);
      }
  }
  ```

  当Spring 应用上下文没有出现`EnvironmentRepository`Bean的时候，那么，默认激活`DefaultRepositoryConfiguration`。

  可以自定义 `EnvironmentRepository`。 

  自定义实现：

  ```
      @Bean
      public EnvironmentRepository environmentRepository(){
          return (application, profile, label) -> {
              Environment environment = new Environment(profile, "default");
              Map<String, String> map = new HashMap<String, String>();
              map.put("name", "张奎");
              PropertySource propertySource = new PropertySource("map", map);
              environment.add(propertySource);
              return environment;
          };
      }
  ```

  

```

```

