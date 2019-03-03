# MyBatis 使用及源码分析

## MySQL的安装

###下载yum源进行安装

wget http://repo.mysql.com/mysql57-community-release-el7-11.noarch.rpm

```shell
rpm -ivh mysql57-community-release-el7-11.noarch.rpm 

yum repolist enabled | grep "mysql.*-community.*"

rpm -ivh mysql-community-server
```

安装组件包括：

```shell
mysql-community-libs 

mysql-community-server

mysql-community-client

mysql-community-common
```

启动MySQL:

```
systemctl start mysqld
```

查看root默认密码设置密码级别：

```shell
grep "temporary password" /var/log/mysqld.log
set global validate_password_policy=0;
```

validate_password_policy有以下取值：

| Policy          | Tests Performed                                              |
| --------------- | ------------------------------------------------------------ |
| `0` or `LOW`    | Length                                                       |
| `1` or `MEDIUM` | Length; numeric, lowercase/uppercase, and special characters |
| `2` or `STRONG` | Length; numeric, lowercase/uppercase, and special characters; dictionary file |

修改root默认密码：

```shell
mysql> set password for root@localhost = password('19830203');
```



查看密码长度：

```
select @@validate_password_length;
```

开放root用户远程连接访问：

```
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '19830203' 
```

## 认识MyBatis

```properties
MyBatis is a first class persistence framework with support for customSQL,stored
procedures and advanced mappings.是什么
MyBatis eliminates almost all of the JDBC code and manual setting of parameters and
retrieval of results.优势
MyBatis can use simple XML or Annotations for configuration and map primitives, Map
interfaces and Java POJOs (Plain Old Java Objects) to database records. 怎么做到的
```

## MyBatis的基本构成

* SqlSessionFactoryBuilder：根据配置信息或代码生成SqlSessionFactory。
* SqlSessionFactory：用于生成SqlSession
* SqlSession：既可以发送SQL执行并返回结果，也可以获取Mapper的接口。用途：
  * 获取映射器，让映射器通过命名空间和方法名去找到指定的SQL语句，发送执行。
  * 直接通过命名信息去执行SQL返回信息，这是保留了iBatis的方式。
  * 支持事务管理
* SQLMapper：由Java接口和XML配置文件(或注解)构成，需要给出对应的SQL和映射规则。它负责发送SQL执行并返回结果。

##SqlSessionFactory生成

* xml配置

  `mybatis-config.xml`

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
      <settings>
          <setting name="logImpl" value="SLF4J"/>
          <!--<setting name="cacheEnabled" value="true" />-->
      </settings>
      <environments default="development">
          <environment id="development">
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                  <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                  <property name="url" value="jdbc:mysql://192.168.3.37:3306/denny-test?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false&amp;useJDBCCompliantTimezoneShift=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=UTC"/>
                  <property name="username" value="denny"/>
                  <property name="password" value="19830203"/>
              </dataSource>
          </environment>
      </environments>
      <mappers>
          <mapper resource="xml/TestMapper.xml"/>
          <mapper resource="xml/PostsMapper.xml"/>
      </mappers>
  </configuration>
  ```

  演示代码：

  ```java
  public class MyBatisDemo {
  
      public static void main(String[] args) throws FileNotFoundException {
          String userDir = System.getProperty("user.dir");
          FileInputStream inputStream = new FileInputStream(userDir + "/src/main/resources/mybatis-config.xml");
          SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
          SqlSession sqlSession = sessionFactory.openSession();
          System.out.println(sqlSession.getMapper(TestMapper.class).selectByPrimaryKey(1));
      }
  }
  ```

  

* 编程式

  ```java
  public class MyBatisProgrammaticDemo {
  
      public static void main(String[] args) {
  
          TransactionFactory transactionFactory = new JdbcTransactionFactory();
  
          PooledDataSource pooledDataSource = new PooledDataSource();
  
          pooledDataSource.setDriver("com.mysql.cj.jdbc.Driver");
          pooledDataSource.setUrl("jdbc:mysql://192.168.3.37:3306/denny-test");
          pooledDataSource.setUsername("denny");
          pooledDataSource.setPassword("19830203");
  
          Environment environment = new Environment("development", transactionFactory, pooledDataSource);
  
          Configuration configuration = new Configuration(environment);
  
          //configuration.addMappers("com.denny.mybatis.mapper");
          configuration.addMapper(TestMapper.class);
  
          SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
          SqlSession sqlSession = sessionFactory.openSession();
          try {
              System.out.println(sqlSession.getMapper(TestMapper.class).selectByPrimaryKey(1));
          }finally {
              sqlSession.close();
          }
      }
  }
  ```

  用SqlSessionFactoryBuilder通过Configuration对象创建SqlSessionFactory。

  注意：将Mapper配置文件放入到与Mapper类文件同目录下。如：`mybatis-demo/src/main/resources/com/denny/mybatis/mapper/TestMapper.xml`

## SQL Mapper

映射器由Java接口和XML文件（或注解）共同组成。作用：

* 定义命名空间

  ```xml
  <mapper namespace="com.denny.mybatis.mapper.PostsMapper">
  ```

* 定义查询结果和POJO的映射关系。

  ```xml
    <resultMap id="BaseResultMap" type="com.denny.mybatis.beans.Posts">
      <id column="pid" jdbcType="INTEGER" property="pid" />
      <result column="post_name" jdbcType="VARCHAR" property="postName" />
      <result column="blog_id" jdbcType="INTEGER" property="blogId" />
    </resultMap>
  ```

* 描述缓存

* 描述SQL语句

  ```xml
    <select id="selectByPrimaryKey" parameterType="java.lang.Integer" resultMap="BaseResultMap">
      select 
      <include refid="Base_Column_List" />
      from posts
      where pid = #{pid,jdbcType=INTEGER}
    </select>
  ```

  ```xml
    <update id="updateByPrimaryKey" parameterType="com.denny.mybatis.beans.Posts">
      update posts
      set post_name = #{postName,jdbcType=VARCHAR},
        blog_id = #{blogId,jdbcType=INTEGER}
      where pid = #{pid,jdbcType=INTEGER}
    </update>
  ```

  ```xml
    <insert id="insert" parameterType="com.denny.mybatis.beans.Posts">
      insert into posts (pid, post_name, blog_id
        )
      values (#{pid,jdbcType=INTEGER}, #{postName,jdbcType=VARCHAR}, #{blogId,jdbcType=INTEGER}
        )
    </insert>
  ```

  ```xml
    <delete id="deleteByPrimaryKey" parameterType="java.lang.Integer">
      delete from posts
      where pid = #{pid,jdbcType=INTEGER}
    </delete>
  ```

  ```java
    <sql id="Base_Column_List">
      pid, post_name, blog_id
    </sql>
  ```

  ```xml
  <sql id="Example_Where_Clause">
      <where>
        <foreach collection="oredCriteria" item="criteria" separator="or">
          <if test="criteria.valid">
            <trim prefix="(" prefixOverrides="and" suffix=")">
              <foreach collection="criteria.criteria" item="criterion">
                <choose>
                  <when test="criterion.noValue">
                    and ${criterion.condition}
                  </when>
                  <when test="criterion.singleValue">
                    and ${criterion.condition} #{criterion.value}
                  </when>
                  <when test="criterion.betweenValue">
                    and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}
                  </when>
                  <when test="criterion.listValue">
                    and ${criterion.condition}
                    <foreach close=")" collection="criterion.value" item="listItem" open="(" separator=",">
                      #{listItem}
                    </foreach>
                  </when>
                </choose>
              </foreach>
            </trim>
          </if>
        </foreach>
      </where>
    </sql>
  ```

  注解方式和XML方式的比较：

|            | 优点                                                     | 缺点                                   |
| ---------- | -------------------------------------------------------- | -------------------------------------- |
| Mapper.xml | 根据接口隔离、统一管理；复杂的语句可以不影响接口的可读性 | 过多的XML文件                          |
| Annotation | 接口就能看到SQL语句、可读性高，不需要查看XML文件         | 复杂的联合查询不好维护、代码可读性差。 |

## 生命周期

|                          |                                                              |      |
| ------------------------ | ------------------------------------------------------------ | ---- |
| SqlSessionFactoryBuilder | 负责构建SqlSessionFactory，构建完后即废弃回收                |      |
| SqlSessionFactory        | 负责创建SqlSession，每次应用访问数据库时都需要通过它创建SqlSession。故在应用的整个生命周期中。 |      |
| SqlSession               | 相当于数据库连接，生命周期是一个请求数据库事务的过程。线程不安全，单线程中。 |      |
| Mapper                   | 在SqlSession的事务方法内，方法级别，最大范围与SqlSession相同。在一个SqlSession事务方法使用时候后，就可以废弃。 |      |

## 配置文件解析

mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties></properties>
    <typeAliases></typeAliases>
    <typeHandlers></typeHandlers>
    <objectFactory></objectFactory>
    <settings>
        <setting name="logImpl" value="SLF4J"/>
        <!--<setting name="cacheEnabled" value="true" />-->
    </settings>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://192.168.3.37:3306/denny-test?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=false&amp;useJDBCCompliantTimezoneShift=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=UTC"/>
                <property name="username" value="denny"/>
                <property name="password" value="19830203"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="xml/TestMapper.xml"/>
        <mapper resource="xml/PostsMapper.xml"/>
    </mappers>
    <plugins></plugins>
</configuration>
```

* 

* properties

  参数配置：

  ```xml
  <properties>
      <property name="driver" value="com.mysql.cj.jdbc.Driver"></property>
  	<property name="url" value="jdbc:mysql://192.168.3.37:3306/denny-test"></property>
      <property name="userName" value="denny"></property>
      <property name="password" value="19830203"></property>
  </properties>
  
      <environments default="development">
          <environment id="development">
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                  <property name="driver" value="${driver}"/>
                  <property name="url" value="${url}/>
                  <property name="username" value="${userName}"/>
                  <property name="password" value="${password}"/>
              </dataSource>
          </environment>
      </environments>
                                                               
  
  ```

  ```xml
  <properties resouce="jdbc.properties"></properties>
  ```

  

* typeAliases

  类型别名设置

  ```xml
  <!-- mybatis-config.xml增加别名处理 -->
  <!--typeAliases为某个Java类型起别名,别名不区分大小写
      type：指定起别名类的全类名，默认别名就是类名小写，user
      alias：起新的别名
  -->
  <typeAliases>
  	<typeAlias type="com.queen.mybatis.bean.User"/>
  </typeAliases>
  
  <!-- 将com.queen.mybatis.bean.User全类名用user替代 -->
  <select id="findUserById" resultType="user">
  	select id, loginId, userName, role, note from t_user where id = #{id}
  </select>
  ```

  或者在实体类中使用注解：

  ```java
  package com.queen.mybatis.bean;
  
  @Alias("user")
  public class User(){
      ...
  }
  ```

  ```xml
  <typeAliases>
  	<!-- <typeAlias type="com.queen.mybatis.bean.User"/> -->
  	<!-- package：为某个包下所有类批量起别名
  	     name：指定包名(为当前包以及下面所有的后代包的每一个类都起一个默认别名,默认小写)
  	 -->
  	<package name="com.queen.mybatis.bean"/>
  </typeAliases>
  ```

  使用package标签批量起别名的情况下，使用@Alias注解为某个类型指定新的别名，避免出现别名冲突报错。

* typeHandlers

  类型处理器。MyBatis在预处理语句中设置一个参数或从结果集中取出一个值时，都会用注册的`typeHandler`进行处理。typeHandler作用是将参数从javaType转换为jdbcType，或从数据库返回结果时把jdbcType转换为javaType。

  系统定义的TypeHandler可参考`TypeHandlerRegistry`类。

  自定义TypeHandler：

  ```java
  @Slf4j
  @MappedTypes({String.class})
  @MappedJdbcTypes({JdbcType.VARCHAR})
  public class CustomStringTypeHandler implements TypeHandler<String> {
      @Override
      public void setParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
          preparedStatement.setString(i, s);
      }
  
      @Override
      public String getResult(ResultSet resultSet, String s) throws SQLException {
          log.info("使用自定义的TypeHandler");
          return resultSet.getString(s);
      }
  
      @Override
      public String getResult(ResultSet resultSet, int i) throws SQLException {
          return resultSet.getString(i);
      }
  
      @Override
      public String getResult(CallableStatement callableStatement, int i) throws SQLException {
          return callableStatement.getString(i);
      }
  }
  ```

  配置自定义TypeHandler：

  ```xml
      <typeHandlers>
          <typeHandler handler="com.denny.mybatis.custom.CustomStringTypeHandler">
          <!-- 或者配置扫描包 -->
           <!--<package name="com.denny.mybatis.custom"></package>-->
          </typeHandler>
      </typeHandlers>
  ```

  或

  ```xml
    <resultMap id="BaseResultMap" type="com.denny.mybatis.beans.Test">
      <id column="id" jdbcType="INTEGER" property="id" />
      <result column="nums" jdbcType="INTEGER" property="nums" />
      <result column="name" jdbcType="VARCHAR" property="name" typeHandler="com.denny.mybatis.custom.CustomStringTypeHandler"/>
    </resultMap>
  ```

  

* objectFactory

  当MyBatis在构建一个结果返回时，都会使用ObjectFactory去构建POJO，在MyBatis这个可以自定义自己的ObjectFactory。默认的ObjectFactory仅会按照配置结果类型的默认构造方法或者指定构造方法来创建对象实例。如果想重写默认的 ObjectFactory,你可以创建你自己的。

  自定义的对象工厂需要继承`DefaultObjectFactory`。

* plugins

  配置插件。

* environments

  环境参数配置。如：事务管理(如：JdbcTransactionFactory)、数据源配置(如：JdbcTransactionFactory)。

## 缓存

* 一级缓存

  * 默认开启一级缓存，即对同一SqlSession而言。当参数和SQL完全一样时，我们使用同一SqlSession对象多次调用同一个Mapper接口方法时，只执行一次SQL， 因为第一次查询时，会将结果放入缓存，后面再次查询时，如没有声明需要刷新，并且缓存未失效时，SqlSession都只会取出缓存的数据，而不会发送SQL执行。

  * 全局一级缓存配置

    配置文件(mybatis-config.xml)中配置：

    ```xml
    settings->localCacheScope:MyBatis 利用本地缓存机制（Local Cache）防止循环引用（circular references）和加速重复嵌套查询。 默认值为 SESSION，这种情况下会缓存一个会话中执行的所有查询,共享缓存。 若设置值为 STATEMENT，本地会话仅用在语句执行上，对相同 SqlSession 的调用(相同SQL)将不会共享数据。
    
    ```

    当`localCacheScope = STATEMENT`时执行查询语句时将会重新清空一级缓存，相当于一级缓存无效。

    查看`BaseExecutor`的query方法中有判断当`localCacheScope = STATEMENT`为时会清空本地缓存。

    如：

    ```java
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
            ErrorContext.instance().resource(ms.getResource()).activity("executing a query").object(ms.getId());
            if (this.closed) {
                throw new ExecutorException("Executor was closed.");
            } else {
                if (this.queryStack == 0 && ms.isFlushCacheRequired()) {
                    this.clearLocalCache();
                }
    
                List list;
                try {
                    ++this.queryStack;
                    list = resultHandler == null ? (List)this.localCache.getObject(key) : null;
                    if (list != null) {
                        this.handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
                    } else {
                        list = this.queryFromDatabase(ms, parameter, rowBounds, resultHandler, key, boundSql);
                    }
                } finally {
                    --this.queryStack;
                }
    
                if (this.queryStack == 0) {
                    Iterator var8 = this.deferredLoads.iterator();
    
                    while(var8.hasNext()) {
                        BaseExecutor.DeferredLoad deferredLoad = (BaseExecutor.DeferredLoad)var8.next();
                        deferredLoad.load();
                    }
    
                    this.deferredLoads.clear();
                    if (this.configuration.getLocalCacheScope() == LocalCacheScope.STATEMENT) {
                        this.clearLocalCache();
                    }
                }
    
                return list;
            }
        }
    ```

    

  * 总结

    * Mybatis一级缓存的生命周期和SqlSession一致。

    * Mybatis的缓存是一个粗粒度的缓存，没有更新缓存和缓存过期的概念，同时只是使用了默认的hashmap，也没有做容量上的限定。

    * Mybatis的一级缓存最大范围是SqlSession内部，有多个SqlSession或者分布式的环境下，有操作数据库写的话，会引起脏数据，建议是把一级缓存的默认级别设定为Statement，即不使用一级缓存。

* 二级缓存

  * 二级缓存是SqlSessionFactory层面的。需要配置开启，在Mapper.xml中配置 `<cache/>`。

  * 二级缓存全局配置

    配置文件(mybatis-config.xml)中配置：

    ```xml
    settings->cacheEnabled:
    全局地开启或关闭配置文件中的所有映射器已经配置的任何缓存(<cache/>)。默认为：true。
    ```

  * <cache/>意味着如下规则：

    * 所有的select语句都将缓存
    * insert、update和delete语句都会刷新缓存
    * 缓存会存储列表集合或对象的1024个引用
    * 缓存默认使用LRU（最近最少使用）算法来回收
    * 缓存会被视为是读写缓存，意味着对象检索不是共享的，而且可以安全地被调用者修改，不干扰其他调用者或线程所做的潜在修改。

  * Cache配置参数

    ```xml
    <cache eviction="LRU" flushInterval="10000" size="2014" readOnly="true" />
    ```

    * eviction：指定缓存回收策略
      * LRU
      * FIFO
      * SOFT：软引用
      * WEAK：弱引用
    * flushInterval：刷新间隔时间
    * size：引用数量
    * readOnly：只读，意味着缓存数据只能读取不能修改，好处是可以快速读取缓存，但不能修改缓存。默认为：false。

## 插件

### 插件开发

插件拦截的四大对象。需要注册签名才能运行插件。

* Executor：执行SQL的全过程，包括：参数组装、SQL执行、结果集返回都可以拦截。

  ```java
  public interface Executor {
  
    ResultHandler NO_RESULT_HANDLER = null;
  
    int update(MappedStatement ms, Object parameter) throws SQLException;
  
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;
  
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;
  
    <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;
  
    List<BatchResult> flushStatements() throws SQLException;
  
    void commit(boolean required) throws SQLException;
  
    void rollback(boolean required) throws SQLException;
  
    CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);
  
    boolean isCached(MappedStatement ms, CacheKey key);
  
    void clearLocalCache();
  
    void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);
  
    Transaction getTransaction();
  
    void close(boolean forceRollback);
  
    boolean isClosed();
  
    void setExecutorWrapper(Executor executor);
  
  }
  ```

  

* StatementHandler：执行SQL，可以重写执行SQL的过程。

  ```java
  public interface StatementHandler {
  
    Statement prepare(Connection connection, Integer transactionTimeout)
        throws SQLException;
  
    void parameterize(Statement statement)
        throws SQLException;
  
    void batch(Statement statement)
        throws SQLException;
  
    int update(Statement statement)
        throws SQLException;
  
    <E> List<E> query(Statement statement, ResultHandler resultHandler)
        throws SQLException;
  
    <E> Cursor<E> queryCursor(Statement statement)
        throws SQLException;
  
    BoundSql getBoundSql();
  
    ParameterHandler getParameterHandler();
  
  }
  ```

  

* ParameterHandler：组装执行SQL的参数，可以重写组装参数的规则。

  ```java
  public interface ParameterHandler {
  
    Object getParameterObject();
  
    void setParameters(PreparedStatement ps)
        throws SQLException;
  
  }
  ```

* ResultSetHandler：组装结果集返回，可以重写组装结果集规则。

  ```java
  public interface ResultSetHandler {
  
    <E> List<E> handleResultSets(Statement stmt) throws SQLException;
  
    <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;
  
    void handleOutputParameters(CallableStatement cs) throws SQLException;
  
  }
  ```

  自定义插件如下：

  ```java
  //配置多个签名
  //type：指定拦截接口 method：指定拦截的方法 args：拦截方法的参数列表
  @Intercepts(value = {
          @Signature(type = StatementHandler.class, method = "prepare", args={Connection.class, Integer.class})
  })
  @Slf4j
  public class LogPlugin implements Interceptor {
      @Override
      public Object intercept(Invocation invocation) throws Throwable {
          System.out.println("----------- intercept query start.... ---------");
  
          // 调用方法，实际上就是拦截的方法
          Object result = invocation.proceed();
  
          log.info("----------- intercept query end.... ---------");
  
          return result;
      }
  
      @Override
      public Object plugin(Object target) {
          return Plugin.wrap(target, this);//嵌套包装
      }
  
      @Override
      public void setProperties(Properties properties) {
  
      }
  }
  ```

  定义签名：

  ```java
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface Intercepts {
    Signature[] value();
  }
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({})
  public @interface Signature {
    Class<?> type();
  
    String method();
  
    Class<?>[] args();
  }
  ```

  配置插件：

  ```java
      <plugins>
          <!-- 嵌套递归包装，最后配置的拦截器先执行 -->
          <plugin interceptor="com.denny.mybatis.custom.LogPlugin"></plugin>
      </plugins>
  ```

  

### 插件实现原理

配置plugins添加拦截器

`Configuration`

```java
  public void addInterceptor(Interceptor interceptor) {
    interceptorChain.addInterceptor(interceptor);
  }
```

`InterceptorChain`拦截器链

```java
public class InterceptorChain {

  private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

  //Object target为四大接口：Executor、ParameterHandler、ParameterHandler、ParameterHandler
  //的实现类对象实例
  //循环拦截器链，嵌套递归生成代理对象,比如：Proxy(target)为第一个代理，最终代理对象如：
  //Proxy(Proxy(Proxy(target)))，这就类似栈(LIFO),最后添加的拦截器最先执行。
  public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptors) {
      target = interceptor.plugin(target);
    }
    return target;
  }

  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }
  
  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }

}
```

`Plugin`插件类实现了`InvocationHandler`接口。

```java
public class Plugin implements InvocationHandler {

  private Object target;
  private Interceptor interceptor;
  private Map<Class<?>, Set<Method>> signatureMap;

  private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
    this.target = target;
    this.interceptor = interceptor;
    this.signatureMap = signatureMap;
  }

  //根据目标对象和拦截器，使用Java动态代理方式生成代理对象。如：$Proxy0(h=Plugin对象)
  public static Object wrap(Object target, Interceptor interceptor) {
    Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
    Class<?> type = target.getClass();
    Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
    if (interfaces.length > 0) {
      return Proxy.newProxyInstance(
          type.getClassLoader(),
          interfaces,
          new Plugin(target, interceptor, signatureMap));
    }
    return target;
  }

  //调用代理对象中对应的目标对象方法时调用该方法。
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
      //如果当前方法为签名中(@Intercepts(value={@Signature(...),...}))配置的指定拦截方法，执行拦截器的
      //定制业务逻辑。
      if (methods != null && methods.contains(method)) {
        return interceptor.intercept(new Invocation(target, method, args));
      }
      //如当前方法不是签名中(@Intercepts(value={@Signature(...),...}))配置的指定拦截方法，则直接调用目
      //标对象的该方法。
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }
   
  //解析签名注解@Intercepts(value={@Signature(...),...})
  private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
    Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
    // issue #251
    if (interceptsAnnotation == null) {
      throw new PluginException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());      
    }
    Signature[] sigs = interceptsAnnotation.value();
    Map<Class<?>, Set<Method>> signatureMap = new HashMap<Class<?>, Set<Method>>();
    for (Signature sig : sigs) {
      Set<Method> methods = signatureMap.get(sig.type());
      if (methods == null) {
        methods = new HashSet<Method>();
        signatureMap.put(sig.type(), methods);
      }
      try {
        Method method = sig.type().getMethod(sig.method(), sig.args());
        methods.add(method);
      } catch (NoSuchMethodException e) {
        throw new PluginException("Could not find method on " + sig.type() + " named " + sig.method() + ". Cause: " + e, e);
      }
    }
    return signatureMap;
  }
  
  //获取目标对象需要实现的所有接口。
  private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
    Set<Class<?>> interfaces = new HashSet<Class<?>>();
    while (type != null) {
      for (Class<?> c : type.getInterfaces()) {
        if (signatureMap.containsKey(c)) {
          interfaces.add(c);
        }
      }
      type = type.getSuperclass();
    }
    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

}
```

## SpringBoot 整合MyBatis



`MapperProxyFactory`

`MapperProxy`

`MapperRegistry`

`MapperMethod`

`MappedStatement`

`DefaultSqlSession`



