# Spring核心源码分析

## 搭建Spring源码环境

### Git 远程克隆Spring源码仓库

```shell
git clone https://github.com/spring-projects/spring-framework.git
```

### 切换到5.1.x分支

```shell
git checkout -b 5.1.x origin/5.1.x
```

### 预编译

```
./gradlew 
```

预编译`spring-oxm`

```
./gradlew :spring-oxm:compileTestJava
```

### 导入源码

源码导入到IDEA，步骤：

* 选择File -> New -> Project from Existing Sources -> Spring源码主目录-> 选择 build.gradle。

## Spring 系统概述

### Spring 核心思想

核心技术：IOC、DI、AOP

Spring 的设计初衷：

Spring 是为解决企业级应用开发的复杂性而设计。

Spring的关键策略：

* 基于POJO的轻量级和最小侵入式编程
* 通过依赖注入和面向接口松耦合
* 基于切面和惯性进行声明式编程
* 通过切面和模板减少样板式代码

### IOC容器及依赖注入

Spring 设计的核心 org.springframework.beans 包。 BeanFactory 接口，采用工厂模式实现。负责对象实例额创建和检索，管理Bean之间的关系(依赖)。

` BeanFactory `的对象范围(scope)：

* 单例(Singleton)
* 原型(Prototype)

Bean 工厂的概念是 Spring 作为 IOC 容器的基础。

### AOP编程

面向切面编程，即 AOP，是一种编程思想，它允许程序员对横切关注点或横切典型的职责分界线的
行为(例如日志和事务管理)进行模块化。AOP 的核心构造是方面(切面)，它将那些影响多个类的行
为封装到可重用的模块中。

AOP 编程的常用场景有:Authentication 权限认证、Logging 日志、Transctions Manager 事务、
Lazy Loading 懒加载、Context Process 上下文处理、Error Handler 错误跟踪(异常捕获机制)
、Cache 缓存。

### Spring5系统概况



![20180505214030958](/Users/denny/Downloads/20180505214030958.png)

模块组成和功能：

* 核心容器：由 spring-beans、spring-core、spring-context 和 spring-expression(Spring
  Expression Language, SpEL) 4 个模块组成。
  * spring-beans 和 spring-core 模块是 Spring 框架的核心模块，包含了控制反转(Inversion of Control, IOC)和依赖注入(Dependency Injection, DI)。
  * spring-context 模块构架于核心模块之上，他扩展了 BeanFactory，为它添加了 Bean 生命周期控制、框架事件体系以及资源加载透明化等功能。ApplicationContext 是该模块的核心接口。
  * spring-expression 模块是统一表达式语言(EL)的扩展模块，可以查询、管理运行中的对象，同时也方便的可以调用对象方法、操作数组、集合等。
* AOP 支持：由 spring-aop、spring-aspects 和 spring-instrument 3 个模块组成。
  * spring-aop 是 Spring 的另一个核心模块，是 AOP 主要的实现模块。
  * spring-aop 是 Spring 的另一个核心模块，是 AOP 主要的实现模块。
  * spring-instrument 模块是基于 JAVA SE 中的"java.lang.instrument"进行设计的，应该算是AOP 的一个支援模块，主要作用是在 JVM 启用时，生成一个代理类，程序员通过代理类在运行时修改类的字节，从而改变一个类的功能，实现 AOP 的功能。
* 数据访问及集成：由 spring-jdbc、spring-tx、spring-orm、spring-jms 和 spring-oxm 5 个
  模块组成。
  * spring-jdbc 模块是 Spring 提供的 JDBC 抽象框架的主要实现模块，用于简化 Spring JDBC。主要实现类有：JdbcTemplate、SimpleJdbcTemplate 以及 NamedParameterJdbcTemplate。
  * spring-tx 模块是 Spring JDBC 事务控制实现模块。
  * spring-orm 模块是 ORM 框架支持模块。
  * spring-oxm模块主要提供一个抽象层以支撑OXM(OXM是Object-to-XML-Mapping的缩写，它是一个O/M-mapper，将 java 对象映射成 XML 数据，或者将 XML 数据映射成 java 对象)，例如:JAXB,
    Castor, XMLBeans, JiBX 和 XStream 等。
* Web MVC：由 spring-web、spring-webmvc、spring-websocket 和 spring-webflux 4 个模块组
  成。
  * spring-web 模块为 Spring 提供了最基础 Web 支持，主要建立于核心容器之上，通过 Servlet 或
    者 Listeners 来初始化 IOC 容器，也包含一些与 Web 相关的支持。
  * spring-webmvc 模块众所周知是一个的 Web-Servlet 模块，实现了 Spring MVC的 Web 应用。
  * spring-websocket 模块主要是与 Web 前端的全双工通讯的协议。
  * spring-webflux 是一个新的非堵塞函数式 Reactive Web 框架，可以用来建立异步的，非阻塞，
    事件驱动的服务，并且扩展性非常好。
* 报文发送：即 spring-messaging 模块。
* 测试：即 spring-test 模块。

主要学习的模块有：spring-core、spring-context、spring-beans、spring-tx、spring-sop、spiring-web、spring-webmvc。

## Spring IOC容器实现原理

Spring IOC 容器完成对 Bean 定义资源文件的定位，载入、解析和依赖注入的全部功能，现在
Spring IOC 容器中管理了一系列靠依赖关系联系起来的 Bean，程序不需要应用自己手动创建所需的对
象，Spring IOC 容器会在我们使用的时候自动为我们创建，并且为我们注入好相关的依赖，这就是
Spring 核心功能的控制反转和依赖注入的相关功能。

### Spring IOC容器体系结构

![Spring IOC容器体系结构](/Users/denny/Documents/Spring IOC容器体系结构.jpg)

`BeanFactory`作为顶层抽象接口。

* `ListableBeanFactory`具备Bean列表查询。
* `HierarchicalBeanFactory`表示Bean的继承关系，具备双亲容器。
* `AutowireCapableBeanFactory`表示Bean具备自动注入功能。

`DefaultListableBeanFactory具备了以上接口提供的所有功能，被ApplicationContext`所使用。

`BeanFactory`接口中的方法：

```java
public interface BeanFactory {

	String FACTORY_BEAN_PREFIX = "&";

	Object getBean(String name) throws BeansException;

	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	Object getBean(String name, Object... args) throws BeansException;

	<T> T getBean(Class<T> requiredType) throws BeansException;

	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	boolean containsBean(String name);

	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	String[] getAliases(String name);

}
```

```java
public interface ListableBeanFactory extends BeanFactory {

	boolean containsBeanDefinition(String beanName);

	int getBeanDefinitionCount();

	String[] getBeanDefinitionNames();

	String[] getBeanNamesForType(ResolvableType type);
	
	String[] getBeanNamesForType(@Nullable Class<?> type);

	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}
```

```java
public interface AutowireCapableBeanFactory extends BeanFactory {
	
    //注入策略
	int AUTOWIRE_NO = 0;

	int AUTOWIRE_BY_NAME = 1;

	int AUTOWIRE_BY_TYPE = 2;

	int AUTOWIRE_CONSTRUCTOR = 3;

	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;

	/**
	 * Suffix for the "original instance" convention when initializing an existing
	 * bean instance: to be appended to the fully-qualified bean class name,
	 * e.g. "com.mypackage.MyClass.ORIGINAL", in order to enforce the given instance
	 * to be returned, i.e. no proxies etc.
	 * @since 5.1
	 * @see #initializeBean(Object, String)
	 * @see #applyBeanPostProcessorsBeforeInitialization(Object, String)
	 * @see #applyBeanPostProcessorsAfterInitialization(Object, String)
	 */
	String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";
	//根据Class类型创建Bean
	<T> T createBean(Class<T> beanClass) throws BeansException;
    //Bean自动注入
	void autowireBean(Object existingBean) throws BeansException;

	Object configureBean(Object existingBean, String beanName) throws BeansException;

	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

	Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;

}
```

`AbstractAutowireCapableBeanFactory`负责Bean创建、属性封装、注入（包括自动注入）和Bean初始化。处理运行时Bean引用、调用初始化方法等。提供构造函数、属性类型、属性名称进行注入。

### BeanDefinition体系结构

`BeanDefinition`对Bean对象进行描述。类图如下：

![BeanDefinition体系结构](/Users/denny/Documents/BeanDefinition体系结构.jpg)

### Bean解析体系结构

`BeanDefinitionReader`负责对Bea配置进行解析。类图如下：

![Bean解析体系机构](/Users/denny/Documents/Bean解析体系机构.jpg)

### IOC容器的初始化

![ApplicationContext体系结构](/Users/denny/Documents/ApplicationContext体系结构.jpg)

ApplicationContext初始化整体流程：

![ClasspathXmlApplicationContext初始化整体流程](/Users/denny/Documents/ClasspathXmlApplicationContext初始化整体流程.jpg)

* refresh()

  * `prepareRefresh()`

    刷新前预处理。初始化属性配置信息(properties)。

  * `obtainFreshBeanFactory()`

    刷新获取DefaultListableBeanFactory。包括：

  * `prepareBeanFactory(beanFactory)`

    配置BeanFactory标准的上下文特征。

    * 配置BeanFactory使用Context的ClassLoader；配置表达式语言处理器。如：可以用#{bean.xxx}的方式来调用相关属性值；配置资源编辑注册器。

      ```java
      		// Tell the internal bean factory to use the context's class loader etc.
      		beanFactory.setBeanClassLoader(getClassLoader());
      		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
      		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
      ```

      

    * 配置上下文的回调

      ```java
      		// Configure the bean factory with context callbacks.
      		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
      		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
      		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
      		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
      		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
      		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
      		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
      ```

      ApplicationContextAwareProcessor对实现了`Aware`接口的Bean自动setter注入当前上下文。如：

      ```java
      class ApplicationContextAwareProcessor implements BeanPostProcessor {
      
      	private final ConfigurableApplicationContext applicationContext;
      
      	private final StringValueResolver embeddedValueResolver;
      
      
      	/**
      	 * Create a new ApplicationContextAwareProcessor for the given context.
      	 */
      	public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
      		this.applicationContext = applicationContext;
      		this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());
      	}
      
      
      	@Override
      	@Nullable
      	public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
      		AccessControlContext acc = null;
      
      		if (System.getSecurityManager() != null &&
      				(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
      						bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
      						bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)) {
      			acc = this.applicationContext.getBeanFactory().getAccessControlContext();
      		}
      
      		if (acc != null) {
      			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
      				invokeAwareInterfaces(bean);
      				return null;
      			}, acc);
      		}
      		else {
      			invokeAwareInterfaces(bean);
      		}
      
      		return bean;
      	}
      
      	private void invokeAwareInterfaces(Object bean) {
      		if (bean instanceof Aware) {
      			if (bean instanceof EnvironmentAware) {
      				((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
      			}
      			if (bean instanceof EmbeddedValueResolverAware) {
      				((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
      			}
      			if (bean instanceof ResourceLoaderAware) {
      				((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
      			}
      			if (bean instanceof ApplicationEventPublisherAware) {
      				((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
      			}
      			if (bean instanceof MessageSourceAware) {
      				((MessageSourceAware) bean).setMessageSource(this.applicationContext);
      			}
      			if (bean instanceof ApplicationContextAware) {
      				((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
      			}
      		}
      	}
      
      	@Override
      	public Object postProcessAfterInitialization(Object bean, String beanName) {
      		return bean;
      	}
      
      }
      ```

    * 

    

  * `postProcessBeanFactory(beanFactory)`

    

  * `invokeBeanFactoryPostProcessors(beanFactory)`

    调用BeanFactory后置处理器。

    `BeanFactory`后置处理器：`BeanFactoryPostProcessor`和`BeanDefinitionRegistryPostProcessor`

    在Bean定义都加载后，可以读取Bean的元数据自定义修改。PropertyPlaceholderConfigurer就是典型的`BeanFactoryPostProcessor`实现。

    ```java
    /**
     * Allows for custom modification of an application context's bean definitions,
     * adapting the bean property values of the context's underlying bean factory.
     *
     * <p>Application contexts can auto-detect BeanFactoryPostProcessor beans in
     * their bean definitions and apply them before any other beans get created.
     *
     * <p>Useful for custom config files targeted at system administrators that
     * override bean properties configured in the application context.
     *
     * <p>See PropertyResourceConfigurer and its concrete implementations
     * for out-of-the-box solutions that address such configuration needs.
     *
     * <p>A BeanFactoryPostProcessor may interact with and modify bean
     * definitions, but never bean instances. Doing so may cause premature bean
     * instantiation, violating the container and causing unintended side-effects.
     * If bean instance interaction is required, consider implementing
     * {@link BeanPostProcessor} instead.
     *
     * @author Juergen Hoeller
     * @since 06.07.2003
     * @see BeanPostProcessor
     * @see PropertyResourceConfigurer
     */
    @FunctionalInterface
    public interface BeanFactoryPostProcessor {
    
    	/**
    	 * Modify the application context's internal bean factory after its standard
    	 * initialization. All bean definitions will have been loaded, but no beans
    	 * will have been instantiated yet. This allows for overriding or adding
    	 * properties even to eager-initializing beans.
    	 * @param beanFactory the bean factory used by the application context
    	 * @throws org.springframework.beans.BeansException in case of errors
    	 */
    	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
    
    }
    ```

    ```java
    /**
     * Extension to the standard {@link BeanFactoryPostProcessor} SPI, allowing for
     * the registration of further bean definitions <i>before</i> regular
     * BeanFactoryPostProcessor detection kicks in. In particular,
     * BeanDefinitionRegistryPostProcessor may register further bean definitions
     * which in turn define BeanFactoryPostProcessor instances.
     *
     * @author Juergen Hoeller
     * @since 3.0.1
     * @see org.springframework.context.annotation.ConfigurationClassPostProcessor
     */
    public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    
    	/**
    	 * Modify the application context's internal bean definition registry after its
    	 * standard initialization. All regular bean definitions will have been loaded,
    	 * but no beans will have been instantiated yet. This allows for adding further
    	 * bean definitions before the next post-processing phase kicks in.
    	 * @param registry the bean definition registry used by the application context
    	 * @throws org.springframework.beans.BeansException in case of errors
    	 */
    	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
    
    }
    ```

    

  * `registerBeanPostProcessors(beanFactory)`

    注册Bean后置处理器。

    Bean后置处理器，在Bean的初始化前、初始化后自定义客户化逻辑处理。如下：

    ```java
    /**
     * Factory hook that allows for custom modification of new bean instances,
     * e.g. checking for marker interfaces or wrapping them with proxies.
     *
     * <p>ApplicationContexts can autodetect BeanPostProcessor beans in their
     * bean definitions and apply them to any beans subsequently created.
     * Plain bean factories allow for programmatic registration of post-processors,
     * applying to all beans created through this factory.
     *
     * <p>Typically, post-processors that populate beans via marker interfaces
     * or the like will implement {@link #postProcessBeforeInitialization},
     * while post-processors that wrap beans with proxies will normally
     * implement {@link #postProcessAfterInitialization}.
     *
     * @author Juergen Hoeller
     * @since 10.10.2003
     * @see InstantiationAwareBeanPostProcessor
     * @see DestructionAwareBeanPostProcessor
     * @see ConfigurableBeanFactory#addBeanPostProcessor
     * @see BeanFactoryPostProcessor
     */
    public interface BeanPostProcessor {
    
    	/**
    	 * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean
    	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
    	 * or a custom init-method). The bean will already be populated with property values.
    	 * The returned bean instance may be a wrapper around the original.
    	 * <p>The default implementation returns the given {@code bean} as-is.
    	 * @param bean the new bean instance
    	 * @param beanName the name of the bean
    	 * @return the bean instance to use, either the original or a wrapped one;
    	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
    	 * @throws org.springframework.beans.BeansException in case of errors
    	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
    	 */
    	@Nullable
    	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    		return bean;
    	}
    
    	/**
    	 * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
    	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
    	 * or a custom init-method). The bean will already be populated with property values.
    	 * The returned bean instance may be a wrapper around the original.
    	 * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
    	 * instance and the objects created by the FactoryBean (as of Spring 2.0). The
    	 * post-processor can decide whether to apply to either the FactoryBean or created
    	 * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
    	 * <p>This callback will also be invoked after a short-circuiting triggered by a
    	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
    	 * in contrast to all other BeanPostProcessor callbacks.
    	 * <p>The default implementation returns the given {@code bean} as-is.
    	 * @param bean the new bean instance
    	 * @param beanName the name of the bean
    	 * @return the bean instance to use, either the original or a wrapped one;
    	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
    	 * @throws org.springframework.beans.BeansException in case of errors
    	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
    	 * @see org.springframework.beans.factory.FactoryBean
    	 */
    	@Nullable
    	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    		return bean;
    	}
    
    }
    ```

  * `initMessageSource()`

    初始化MessageSource。MessageSource是Spring中的国际化配置信息访问接口。

  * `initApplicationEventMulticaster()`

    初始化应用事件广播器。默认为：`SimpleApplicationEventMulticaster`。

  * `onRefresh()`

    子类自定义的刷新处理逻辑。

  * `registerListeners()`

    注册应用监听器(`SpringListener`)。在`ApplicationEventMulticaster`事件广播器中添加监听器。

  * `finishBeanFactoryInitialization(beanFactory)`

    完成当前上下文中的BeanFactory的初始化。初始化所有单例(Singleton)Bean(`beanFactory.preInstantiateSingletons()`)。

    ```java
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    		// Initialize conversion service for this context.
    		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
    				beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
    			beanFactory.setConversionService(
    					beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
    		}
    
    		// Register a default embedded value resolver if no bean post-processor
    		// (such as a PropertyPlaceholderConfigurer bean) registered any before:
    		// at this point, primarily for resolution in annotation attribute values.
    		if (!beanFactory.hasEmbeddedValueResolver()) {
    			beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
    		}
    
    		// Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
    		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
    		for (String weaverAwareName : weaverAwareNames) {
    			getBean(weaverAwareName);
    		}
    
    		// Stop using the temporary ClassLoader for type matching.
    		beanFactory.setTempClassLoader(null);
    
    		// Allow for caching all bean definition metadata, not expecting further changes.
    		beanFactory.freezeConfiguration();
    
    		// Instantiate all remaining (non-lazy-init) singletons.
    		beanFactory.preInstantiateSingletons();
    	}
    ```

    

  * `finishRefresh()`

    完成上下文的刷新。

    * 清除上下文资源缓存
    * 初始化生命周期处理器
    * 调用LifecycleProcessor的onRefresh()方法。
    * 发布上下文刷新完成事件
    * 如果配置了MBeanServer，就完成在MBeanServer上的注册

    ```java
    	protected void finishRefresh() {
    		// Clear context-level resource caches (such as ASM metadata from scanning).
    		clearResourceCaches();
    
    		// Initialize lifecycle processor for this context.
    		initLifecycleProcessor();
    
    		// Propagate refresh to lifecycle processor first.
    		getLifecycleProcessor().onRefresh();
    
    		// Publish the final event.
    		publishEvent(new ContextRefreshedEvent(this));
    
    		// Participate in LiveBeansView MBean, if active.
    		LiveBeansView.registerApplicationContext(this);
    	}
    ```

    

### BeanFactory初始化流程

![BeanFactory初始化](/Users/denny/Documents/BeanFactory初始化.jpg)

```java
	/** Map of bean definition objects, keyed by bean name. */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
```

### Bean的初始化流程

DefaultListableBeanFactory中getBean(...)方法调用AbstractBeanFactory中的getBean(...)方法完成指定Bean的创建和初始化。

* AbstractBeanFactory 的 getBean 相关方法的源码如下:

  ```java
  //获取 IOC 容器中指定名称的 Bean
  @Override
  public Object getBean(String name) throws BeansException {
  //doGetBean 才是真正向 IOC 容器获取被管理 Bean 的过程
  return doGetBean(name, null, null, false); }
  
  //获取 IOC 容器中指定名称和类型的 Bean
  @Override
  public <T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException {
  //doGetBean 才是真正向 IOC 容器获取被管理 Bean 的过程
     return doGetBean(name, requiredType, null, false);
  }
  
  //获取 IOC 容器中指定名称和类型的 Bean
  @Override
  public <T> T getBean(String name, @Nullable Class<T> requiredType) throws BeansException {
  //doGetBean 才是真正向 IOC 容器获取被管理 Bean 的过程
     return doGetBean(name, requiredType, null, false);
  }
  
  //获取 IOC 容器中指定名称、类型和参数的 Bean
  public <T> T getBean(String name, @Nullable Class<T> requiredType, @Nullable Object... args)
  throws BeansException {
  //doGetBean 才是真正向 IOC 容器获取被管理 Bean 的过程 return doGetBean(name, requiredType, args, false);
  }
  
  @SuppressWarnings("unchecked")
  //真正实现向 IOC 容器获取 Bean 的功能，也是触发依赖注入功能的地方
  protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,
  @Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {
  	//根据指定的名称获取被管理 Bean 的名称，剥离指定名称中对容器的相关依赖 //如果指定的是别名，将别 名转换为规范的 Bean 名称
  	final String beanName = transformedBeanName(name);
  	Object bean;
  	//先从缓存中取是否已经有被创建过的单态类型的 Bean //对于单例模式的 Bean 整个 IOC 容器中只创建	一次，不需要重复创建 
      Object sharedInstance = getSingleton(beanName);
  	//IOC 容器创建单例模式 Bean 实例对象
  	if (sharedInstance != null && args == null) {
  		if (logger.isDebugEnabled()) {
  		    //如果指定名称的 Bean 在容器中已有单例模式的 Bean 被创建 
              //直接返回已经创建的 Bean
  			if (isSingletonCurrentlyInCreation(beanName)) {
  				logger.debug("Returning eagerly cached instance of singleton bean '" + 				beanName + "' that is not fully initialized yet - a consequence of a 					circular reference");
  			}
  			else {
  				logger.debug("Returning cached instance of singleton bean '" + 						beanName + "'"); 
      		}
  		}
  		//获取给定 Bean 的实例对象，主要是完成 FactoryBean 的相关处理 
          //注意:BeanFactory 是管理容器中 Bean 的工厂，而 FactoryBean 是 
          //创建创建对象的工厂 Bean，两者之间有区别
  		bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
  	}
  	else {
  		//缓存没有正在创建的单例模式 Bean 
          //缓存中已经有已经创建的原型模式 Bean 
          //但是由于循环引用的问题导致实例化对象失败
  		if (isPrototypeCurrentlyInCreation(beanName)) {
       		throw new BeanCurrentlyInCreationException(beanName);
     		}
  		//对 IOC 容器中是否存在指定名称的 BeanDefinition 进行检查，首先检查是否 
          //能在当前的BeanFactory 中获取的所需要的 Bean，如果不能则委托当前容器 
          //的父级容器去查找，如果还是找不到则沿着容器的继承体系向父级容器查找 
          BeanFactory parentBeanFactory = getParentBeanFactory(); //当前容器的父级容器存在，		  且当前容器中不存在指定名称的 Bean
  		if (parentBeanFactory != null && !containsBeanDefinition(beanName)) { //解析指			定 Bean 名称的原始名称
  			String nameToLookup = originalBeanName(name);
  			if (parentBeanFactory instanceof AbstractBeanFactory) {
          		return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
                nameToLookup, requiredType, args, typeCheckOnly);
  			}
  			else if (args != null) {
  				//委派父级容器根据指定名称和显式的参数查找
       			return (T) parentBeanFactory.getBean(nameToLookup, args);
     			}
  			else {
  				//委派父级容器根据指定名称和类型查找
  				return parentBeanFactory.getBean(nameToLookup, requiredType);
  			} 
  		}
  		//创建的 Bean 是否需要进行类型验证，一般不需要 if (!typeCheckOnly) {
  		//向容器标记指定的 Bean 已经被创建
     		markBeanAsCreated(beanName);
  	}
  	try {
  		//根据指定 Bean 名称获取其父级的 Bean 定义
  		//主要解决 Bean 继承时子类合并父类公共属性问题
  		final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName); 				checkMergedBeanDefinition(mbd, beanName, args);
  		//获取当前 Bean 所有依赖 Bean 的名称 String[] dependsOn = mbd.getDependsOn(); 
          //如果当前 Bean 有依赖 Bean
  		if (dependsOn != null) {
       		for (String dep : dependsOn) {
          		if (isDependent(beanName, dep)) {
  						throw new BeanCreationException(mbd.getResourceDescription(), 						beanName,
  						"Circular depends-on relationship between '" + beanName + "' 						and '" + dep + "'");
  				}
  				//递归调用 getBean 方法，获取当前 Bean 的依赖 Bean 					                
                  registerDependentBean(dep, beanName); 
                  //把被依赖 Bean 注册给当前依赖的 Bean 
                  getBean(dep);
  				} 
          	}
  			//创建单例模式 Bean 的实例对象 if (mbd.isSingleton()) {
  			//这里使用了一个匿名内部类，创建 Bean 实例对象，并且注册给所依赖的对象 
          	sharedInstance = getSingleton(beanName, () -> {
  				try {
  					//创建一个指定 Bean 实例对象，如果有父级继承，则合并子类和父类的定义 
                  	return createBean(beanName, mbd, args);    
   				}
       			catch (BeansException ex) {
  					//显式地从容器单例模式 Bean 缓存中清除实例对象 
              		destroySingleton(beanName);
  					throw ex;
  				} 
              });
  			//获取给定 Bean 的实例对象
     			bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
  		}
  		//IOC 容器创建原型模式 Bean 实例对象 
      	else if (mbd.isPrototype()) {
  		//原型模式(Prototype)是每次都会创建一个新的对象 Object prototypeInstance = null;
  			try {
  				//回调 beforePrototypeCreation 方法，默认的功能是注册当前创建的原型对象 
                  beforePrototypeCreation(beanName);
  				//创建指定 Bean 对象实例
  				prototypeInstance = createBean(beanName, mbd, args);
  			}
  			finally {
  				//回调 afterPrototypeCreation 方法，默认的功能告诉 IOC 容器指定 Bean 的原型对				象不再创建
       			afterPrototypeCreation(beanName);
     			}
  			//获取给定 Bean 的实例对象
     			bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
  		}
  		//要创建的 Bean 既不是单例模式，也不是原型模式，则根据 Bean 定义资源中 //配置的生命周期范		围，选择实例化 Bean 的合适方法，这种在 Web 应用程序中 //比较常用，如:request、session、			application 等生命周期
  		else {
  			String scopeName = mbd.getScope();
  			final Scope scope = this.scopes.get(scopeName); //Bean 定义资源中没有配置生命					周期范围，则 Bean 定义不合法 
              if (scope == null) {
  				throw new IllegalStateException("No Scope registered for scope name '+ 						scopeName + "'"); 
          	}
  			try { //这里又使用了一个匿名内部类，获取一个指定生命周期范围的实例 Object 
          		scopedInstance = scope.get(beanName, () -> {
          			beforePrototypeCreation(beanName);
          			try {
     						return createBean(beanName, mbd, args);
                  	}
                  	finally {
                     		afterPrototypeCreation(beanName);
  					} 
               	});
  			 	//获取给定 Bean 的实例对象
               	bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
  			}
  			catch (IllegalStateException ex) {
  				throw new BeanCreationException(beanName,
  					"Scope '" + scopeName + "' is not active for the current thread; 					consider " +"defining a scoped proxy for this bean if you intend to 					refer to it from a singleton", ex);
  			} 
  		}
       }
       catch (BeansException ex) {
          cleanupAfterBeanCreationFailure(beanName);
  		throw ex; 
       }
  	}
  	//对创建的 Bean 实例对象进行类型检查
  	if (requiredType != null && !requiredType.isInstance(bean)) {
       	try {
          	T convertedBean = getTypeConverter().convertIfNecessary(bean, 						                  requiredType);
          	if (convertedBean == null) {
             		throw new BeanNotOfRequiredTypeException(name, requiredType, 						bean.getClass());
          	}
          	return convertedBean;
       	}
  		catch (TypeMismatchException ex) { 
          	if (logger.isDebugEnabled()) {
  				logger.debug("Failed to convert bean '" + name + "' to required type 							'" + ClassUtils.getQualifiedName(requiredType) + "'", ex);
  			}
          	throw new BeanNotOfRequiredTypeException(name, requiredType, 
                                                  bean.getClass());
       	}
  	}
     return (T) bean;
  }
  ```

  通过上面对向 IOC 容器获取 Bean 方法的分析，我们可以看到在 Spring 中，如果 Bean 定义的单例模式 (Singleton)，则容器在创建之前先从缓存中查找，以确保整个容器中只存在一个实例对象。如果 Bean 

  定义的是原型模式(Prototype)，则容器每次都会创建一个新的实例对象。除此之外，Bean 定义还可 以扩展为指定其生命周期范围。

  上面的源码只是定义了根据 Bean 定义的模式，采取的不同创建 Bean 实例对象的策略，具体的 Bean 实 例对象的创建过程由实现了 ObejctFactory 接口的匿名内部类的 createBean 方法完成， `ObejctFactory` 使用委派模式，具体的 Bean 实例创建过程交由其实现类 `AbstractAutowireCapableBeanFactory` 完 成 ， 我 们 继 续 分 析 `AbstractAutowireCapableBeanFactor`y 的 `createBean` 方法的源码，理解其创建 Bean 实例的具体 实现过程。 

  

  解决循环依赖引用。单例三级缓存：

  `Map<String, Object> singletonObjects`：单例对象缓存

  `Map<String, Object> earlySingletonObjects`：提前暴露单例对象缓存

  `Map<String, ObjectFactory> singletonFactories`：单例工厂缓存。`ObjectFactory.getObject()` 单例实例创建。

  

  ```java
  	@Nullable
  	protected Object getSingleton(String beanName, boolean allowEarlyReference) {
  		Object singletonObject = this.singletonObjects.get(beanName);
  		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
  			synchronized (this.singletonObjects) {
  				singletonObject = this.earlySingletonObjects.get(beanName);
  				if (singletonObject == null && allowEarlyReference) {
  					ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
  					if (singletonFactory != null) {
  						singletonObject = singletonFactory.getObject();
  						this.earlySingletonObjects.put(beanName, singletonObject);
  						this.singletonFactories.remove(beanName);
  					}
  				}
  			}
  		}
  		return singletonObject;
  	}
  ```

  

  createBeanInstance`

  

* AbstractAutowireCapableBeanFactory 创建 Bean 实例对象

  ```java
  @Override
  protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
  	if (logger.isDebugEnabled()) {
  		logger.debug("Creating instance of bean '" + beanName + "'");
     	}
     	RootBeanDefinition mbdToUse = mbd;
  	//判断需要创建的 Bean 是否可以实例化，即是否可以通过当前的类加载器加载
  	Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
  	if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != 		null) {
       	mbdToUse = new RootBeanDefinition(mbd);
       	mbdToUse.setBeanClass(resolvedClass);
     	}
  	//校验和准备 Bean 中的方法覆盖 
      try {
      	mbdToUse.prepareMethodOverrides();
      }
      catch (BeanDefinitionValidationException ex) {
       	throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
             beanName, "Validation of Method overrides failed", ex);
  	}
      
      try {
  		//如果 Bean 配置了初始化前和初始化后的处理器，则试图返回一个需要创建 Bean 的代理对象 
          Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
  		if (bean != null) {
     			return bean;
       	}
  	}
  	catch (Throwable ex) {
  			throw new BeanCreationException(mbdToUse.getResourceDescription(), 	
               beanName, "BeanPostProcessor before instantiation of bean failed", ex);
  	}
  	try {
  		//创建 Bean 的入口
  		Object beanInstance = doCreateBean(beanName, mbdToUse, args); 
          if (logger.isDebugEnabled()) {
  			logger.debug("Finished creating instance of bean '" + beanName + "'"); 
          }
       	return beanInstance;
     	}
     	catch (BeanCreationException ex) {
       	throw ex;
     	}
     	catch (ImplicitlyAppearedSingletonException ex) {
  		throw ex; 
      }
  	catch (Throwable ex) {
  		throw new BeanCreationException(
  		mbdToUse.getResourceDescription(), beanName, "Unexpected exception during 			  bean creation", ex);
  	} 
  }
  
  //真正创建 Bean 的方法
  protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args) throws BeanCreationException {
  	//封装被创建的 Bean 对象
  	BeanWrapper instanceWrapper = null; 
      if (mbd.isSingleton()) {
      	instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
      }
      if (instanceWrapper == null) {
      	instanceWrapper = createBeanInstance(beanName, mbd, args);
  	}
  	final Object bean = instanceWrapper.getWrappedInstance(); //获取实例化对象的类型
  	Class<?> beanType = instanceWrapper.getWrappedClass();
  	if (beanType != NullBean.class) {
  		mbd.resolvedTargetType = beanType;
  	}
  	//调用 PostProcessor 后置处理器 synchronized (mbd.postProcessingLock) {
      if (!mbd.postProcessed) {
      	try {
          	applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
       	}
  		catch (Throwable ex) {
  			throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                "Post-processing of merged bean definition failed", ex);
       		mbd.postProcessed = true;
  		} 
      }
  	//向容器中缓存单例模式的 Bean 对象，以防循环引用
  	boolean earlySingletonExposure = (mbd.isSingleton() &&
                                        this.allowCircularReferences &&
                                        isSingletonCurrentlyInCreation(beanName));
  	if (earlySingletonExposure) {
  		if (logger.isDebugEnabled()) {
  			logger.debug("Eagerly caching bean '" + beanName +
  				"' to allow for resolving potential circular references");
  		}
  		//这里是一个匿名内部类，为了防止循环引用，尽早持有对象的引用
     		addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd,
                                                                    bean));
  	}
  	//Bean 对象的初始化，依赖注入在此触发
  	//这个 exposedObject 在初始化完成之后返回作为依赖注入完成后的 
      Bean Object exposedObject = bean;
  	try {
  		//将 Bean 实例对象封装，并且 Bean 定义中配置的属性值赋值给实例对象 
      	populateBean(beanName, mbd, instanceWrapper);
  		//初始化 Bean 对象
  		exposedObject = initializeBean(beanName, exposedObject, mbd);
  	}
  	catch (Throwable ex) {
  		if (ex instanceof BeanCreationException && 
              beanName.equals(((BeanCreationException) ex).getBeanName())) {
              throw (BeanCreationException) ex;
  		}
  		else {
       		throw new BeanCreationException(
  				mbd.getResourceDescription(), beanName, "Initialization of bean
                  failed", ex);
  			} 
          }
  		if (earlySingletonExposure) {
  			//获取指定名称的已注册的单例模式 Bean 对象
  			Object earlySingletonReference = getSingleton(beanName, false); 
              if (earlySingletonReference != null) {
  				//根据名称获取的已注册的 Bean 和正在实例化的 Bean 是同一个 
              	if (exposedObject == bean) {
  					//当前实例化的 Bean 初始化完成
          			exposedObject = earlySingletonReference;
       			}
  				//当前 Bean 依赖其他 Bean，并且当发生循环引用时不允许新创建实例对象
  				else if (!this.allowRawInjectionDespiteWrapping &&
                  	hasDependentBean(beanName)) {
  			    	String[] dependentBeans = getDependentBeans(beanName);
  					Set<String> actualDependentBeans = new LinkedHashSet<>
                              (dependentBeans.length); //获取当前 Bean 所依赖的其他 Bean
  					for (String dependentBean : dependentBeans) {
  						//对依赖 Bean 进行类型检查
  						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) 
                          {
                				actualDependentBeans.add(dependentBean);
             				}
          		}
          		if (!actualDependentBeans.isEmpty()) {
  					throw new BeanCurrentlyInCreationException(beanName,
  					"Bean with name '" + beanName + "' has been injected into other 					beans [" +StringUtils.collectionToCommaDelimitedString
                                                                 (actualDependentBeans) 
                      +"] in its raw version as part of a circular reference, but has    						eventually been " + "wrapped. This means that said other beans do not use the final version of the " + "bean. This is often the result of over-eager type matching - consider using " + "'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
                   } 
  			}
  		} 
  	}
  	//注册完成依赖注入的 Bean 
      try {
     		registerDisposableBeanIfNecessary(beanName, bean, mbd);
  	}
  	catch (BeanDefinitionValidationException ex) {
     		throw new BeanCreationException(
          mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
  	}
     return exposedObject;
  }
  ```

  通过对方法源码的分析，我们看到具体的依赖注入实现在以下两个方法中:
  (1).createBeanInstance:生成 Bean 所包含的 java 对象实例。
  (2).populateBean :对 Bean 属性的依赖注入进行处理。
  下面继续分析这两个方法的代码实现。

* createBeanInstance 方法创建 Bean 的 java 实例对象

  ```java
  //创建 Bean 的实例对象
  protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
  	//检查确认 Bean 是可实例化的
  	Class<?> beanClass = resolveBeanClass(mbd, beanName);
  	//使用工厂方法对 Bean 进行实例化
  	if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && 				!mbd.isNonPublicAccessAllowed()) {
  		throw new BeanCreationException(mbd.getResourceDescription(), beanName,
  		"Bean class isn't public, and non-public access not allowed: " + 					beanClass.getName());
  	}
      Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
      if (instanceSupplier != null) {
      	return obtainFromSupplier(instanceSupplier, beanName);
      }
      if (mbd.getFactoryMethodName() != null) { //调用工厂方法实例化
  		return instantiateUsingFactoryMethod(beanName, mbd, args);
  	}
  	//使用容器的自动装配方法进行实例化 boolean resolved = false;
  	boolean autowireNecessary = false; 
      if (args == null) {
      	synchronized (mbd.constructorArgumentLock) {
          	if (mbd.resolvedConstructorOrFactoryMethod != null) {
             		resolved = true;
             		autowireNecessary = mbd.constructorArgumentsResolved;
          	}
  		}
  	}
      if (resolved) {
  		if (autowireNecessary) { //配置了自动装配属性，使用容器的自动装配实例化 //容器的自动装配			是根据参数类型匹配 Bean 的构造方法
  			return autowireConstructor(beanName, mbd, null, null);
  		}
  		else {
  			//使用默认的无参构造方法实例化
          	return instantiateBean(beanName, mbd);
       	}
  	}
  	//使用 Bean 的构造方法进行实例化
  	Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, 								beanName); 
      if (ctors != null ||
  		mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR ||
  		mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) { //使用容器		  的自动装配特性，调用匹配的构造方法实例化
  		return autowireConstructor(beanName, mbd, ctors, args);
  	}
  	//使用默认的无参构造方法实例化
     	return instantiateBean(beanName, mbd);
  }
  
  //使用默认的无参构造方法实例化 Bean 对象
  protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
  	try {
  		Object beanInstance;
  		final BeanFactory parent = this; //获取系统的安全管理接口，JDK 标准的安全管理 API 
          if (System.getSecurityManager() != null) {
  			//这里是一个匿名内置类，根据实例化策略创建实例对象
  			beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>) () 				-> getInstantiationStrategy().instantiate(mbd, beanName, parent),
                getAccessControlContext());
  		}
  		else {
  			//将实例化的对象封装起来
          	beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, 								parent);
       	}
       	BeanWrapper bw = new BeanWrapperImpl(beanInstance);
       	initBeanWrapper(bw);
  		return bw; 
      }
  	catch (Throwable ex) {
  		throw new BeanCreationException(
      	mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
      } 
  }
  ```

  经过对上面的代码分析，我们可以看出，对使用工厂方法和自动装配特性的 Bean 的实例化相当比较清 楚，调用相应的工厂方法或者参数匹配的构造方法即可完成实例化对象的工作，但是对于我们最常使用 的默认无参构造方法就需要使用相应的实例化策略(JDK 的反射机制或者 CGLIB)来进行初始化了，在方 法 getInstantiationStrategy().instantiate()中就具体实现类使用实例化策略实例化对象。 初始化策略包括：

  `SimpleInstantiationStrategy`和`CglibSubclassingInstantiationStrategy`。

* populateBean 方法对 Bean 属性的依赖注入

  在以上分析中我们已经了解到 Bean 的依赖注入分为以下两个过程:
  (1).createBeanInstance:生成 Bean 所包含的 Java 对象实例。
  (2).populateBean:对 Bean 属性的依赖注入进行处理。
  上面我们已经分析了容器初始化生成 Bean 所包含的 Java 实例对象的过程，现在我们继续分析生成对
  象后，Spring IOC 容器是如何将 Bean 的属性依赖关系注入 Bean 实例对象中并设置好的，属性依赖注
  入的代码如下:

  ```java
  //将 Bean 属性设置到生成的实例对象上
  protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
  		if (bw == null) {
  			if (mbd.hasPropertyValues()) {
  				throw new BeanCreationException(
  						mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
  			}
  			else {
  				// Skip property population phase for null instance.
  				return;
  			}
  		}
  
  		// Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
  		// state of the bean before properties are set. This can be used, for example,
  		// to support styles of field injection.
  		boolean continueWithPropertyPopulation = true;
  
  		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
  			for (BeanPostProcessor bp : getBeanPostProcessors()) {
  				if (bp instanceof InstantiationAwareBeanPostProcessor) {
  					InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
  					if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
  						continueWithPropertyPopulation = false;
  						break;
  					}
  				}
  			}
  		}
  
  		if (!continueWithPropertyPopulation) {
  			return;
  		}
  		//将 Bean 属性设置到生成的实例对象上
  		PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);
  
  		if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME || mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
  			MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
  			// Add property values based on autowire by name if applicable.
  			if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME) {
  				autowireByName(beanName, mbd, bw, newPvs);
  			}
  			// Add property values based on autowire by type if applicable.
  			if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
  				autowireByType(beanName, mbd, bw, newPvs);
  			}
  			pvs = newPvs;
  		}
  
  		boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
  		boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);
  
  		PropertyDescriptor[] filteredPds = null;
  		if (hasInstAwareBpps) {
  			if (pvs == null) {
  				pvs = mbd.getPropertyValues();
  			}
  			for (BeanPostProcessor bp : getBeanPostProcessors()) {
  				if (bp instanceof InstantiationAwareBeanPostProcessor) {
  					InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
  					PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
  					if (pvsToUse == null) {
  						if (filteredPds == null) {
  							filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
  						}
  						pvsToUse = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
  						if (pvsToUse == null) {
  							return;
  						}
  					}
  					pvs = pvsToUse;
  				}
  			}
  		}
  		if (needsDepCheck) {
  			if (filteredPds == null) {
  				filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
  			}
  			checkDependencies(beanName, mbd, filteredPds, pvs);
  		}
  		//对属性进行注入
  		if (pvs != null) {
  			applyPropertyValues(beanName, mbd, bw, pvs);
  		}
  }
  
  //解析并注入依赖属性的过程
  protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
  		if (pvs.isEmpty()) {
  			return;
  		}
  		//设置安全上下文，JDK 安全机制
  		if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
  			((BeanWrapperImpl) bw).setSecurityContext(getAccessControlContext());
  		}
  		//封装属性值
  		MutablePropertyValues mpvs = null;
  		List<PropertyValue> original;
  
  		if (pvs instanceof MutablePropertyValues) {
  			mpvs = (MutablePropertyValues) pvs;
              //属性值已经转换
  			if (mpvs.isConverted()) {
  				// Shortcut: use the pre-converted values as-is.
  				try {
  					bw.setPropertyValues(mpvs);
  					return;
  				}
  				catch (BeansException ex) {
  					throw new BeanCreationException(
  							mbd.getResourceDescription(), beanName, "Error setting property values", ex);
  				}
  			}
              //获取属性值对象的原始类型值
  			original = mpvs.getPropertyValueList();
  		}
  		else {
  			original = Arrays.asList(pvs.getPropertyValues());
  		}
      	
  		//获取用户自定义的类型转换
  		TypeConverter converter = getCustomTypeConverter();
  		if (converter == null) {
  			converter = bw;
  		}
      	//创建一个 Bean 定义属性值解析器，将 Bean 定义中的属性值解析为 Bean 实例对象的实际值
  		BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);
  
  		// Create a deep copy, resolving any references for values.
      	//创建一个 Bean 定义属性值解析器，将 Bean 定义中的属性值解析为 Bean 实例对象的实际值
  		List<PropertyValue> deepCopy = new ArrayList<>(original.size());
  		boolean resolveNecessary = false;
  		for (PropertyValue pv : original) {
              //属性值不需要转换
  			if (pv.isConverted()) {
  				deepCopy.add(pv);
  			}
              //属性值需要转换
  			else {
  				String propertyName = pv.getName();
                  //原始的属性值，即转换之前的属性值
  				Object originalValue = pv.getValue();
                  //转换属性值，例如将引用转换为 IOC 容器中实例化对象引用
  				Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
                  //转换之后的属性值
  				Object convertedValue = resolvedValue;
                  //属性值是否可以转换
  				boolean convertible = bw.isWritableProperty(propertyName) &&
  !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
  				if (convertible) {
                      //使用用户自定义的类型转换器转换属性值
  					convertedValue = convertForProperty(resolvedValue, propertyName, bw, converter);
  				}
  				// Possibly store converted value in merged bean definition,
  				// in order to avoid re-conversion for every created bean instance.
                  //存储转换后的属性值，避免每次属性注入时的转换工作
  				if (resolvedValue == originalValue) {
  					if (convertible) {
                          //设置属性转换之后的值
  						pv.setConvertedValue(convertedValue);
  					}
  					deepCopy.add(pv);
  				}
                  //属性是可转换的，且属性原始值是字符串类型，且属性的原始类型值不是 
                  //动态生成的字符串，且属性的原始值不是集合或者数组类型
  				else if (convertible && originalValue instanceof TypedStringValue &&
  						!((TypedStringValue) originalValue).isDynamic() &&
  						!(convertedValue instanceof Collection || ObjectUtils.isArray(convertedValue))) {
  					pv.setConvertedValue(convertedValue);
                      //重新封装属性的值
  					deepCopy.add(pv);
  				}
  				else {
  					resolveNecessary = true;
  					deepCopy.add(new PropertyValue(pv, convertedValue));
  				}
  			}
  		}
  		if (mpvs != null && !resolveNecessary) {
              //标记属性值已经转换过
  			mpvs.setConverted();
  		}
  
  		// Set our (possibly massaged) deep copy.
      	//进行属性依赖注入
  		try {
  			bw.setPropertyValues(new MutablePropertyValues(deepCopy));
  		}
  		catch (BeansException ex) {
  			throw new BeanCreationException(
  					mbd.getResourceDescription(), beanName, "Error setting property values", ex);
  		}
  	}
  ```

  分析上述代码，我们可以看出，对属性的注入过程分以下两种情况: (1).属性值类型不需要转换时，不需要解析属性值，直接准备进行依赖注入。 (2).属性值需要进行类型转换时，如对其他对象的引用等，首先需要解析属性值，然后对解析后的属性 值进行依赖注入。 

  对属性值的解析是在 BeanDefinitionValueResolver 类中的 resolveValueIfNecessary 方法中进 行的，对属性值的依赖注入是通过 bw.setPropertyValues 方法实现的，在分析属性值的依赖注入之 前，我们先分析一下对属性值的解析过程。

* BeanDefinitionValueResolver` 解析属性值:

  当容器在对属性进行依赖注入时，如果发现属性值需要进行类型转换，如属性值是容器中另一个 Bean
  实例对象的引用，则容器首先需要根据属性值解析出所引用的对象，然后才能将该引用对象注入到目标
  实例对象的属性上去，对属性进行解析的由` resolveValueIfNecessary `方法实现，其源码如下:

* ```java
  //解析属性值，对注入类型进行转换
  public Object resolveValueIfNecessary(Object argName, @Nullable Object value) {
  		// We must check each value to see whether it requires a runtime reference
  		// to another bean to be resolved.
      	//对引用类型的属性进行解析
  		if (value instanceof RuntimeBeanReference) {
  			RuntimeBeanReference ref = (RuntimeBeanReference) value;
              //对引用类型的属性进行解析
  			return resolveReference(argName, ref);
  		}
      	//对属性值是引用容器中另一个 Bean 名称的解析
  		else if (value instanceof RuntimeBeanNameReference) {
  			String refName = ((RuntimeBeanNameReference) value).getBeanName();
  			refName = String.valueOf(doEvaluate(refName));
              //从容器中获取指定名称的 Bean
  			if (!this.beanFactory.containsBean(refName)) {
  				throw new BeanDefinitionStoreException(
  						"Invalid bean name '" + refName + "' in bean reference for " + argName);
  			}
  			return refName;
  		}
      	//对 Bean 类型属性的解析，主要是 Bean 中的内部类
  		else if (value instanceof BeanDefinitionHolder) {
  			// Resolve BeanDefinitionHolder: contains BeanDefinition with name and aliases.
  			BeanDefinitionHolder bdHolder = (BeanDefinitionHolder) value;
  			return resolveInnerBean(argName, bdHolder.getBeanName(), bdHolder.getBeanDefinition());
  		}
  		else if (value instanceof BeanDefinition) {
  			// Resolve plain BeanDefinition, without contained name: use dummy name.
  			BeanDefinition bd = (BeanDefinition) value;
  			String innerBeanName = "(inner bean)" + BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR +
  					ObjectUtils.getIdentityHexString(bd);
  			return resolveInnerBean(argName, innerBeanName, bd);
  		}
      	//对集合数组类型的属性解析
  		else if (value instanceof ManagedArray) {
  			// May need to resolve contained runtime references.
  			ManagedArray array = (ManagedArray) value;
  			Class<?> elementType = array.resolvedElementType;
  			if (elementType == null) {
                  //获取数组元素的类型
  				String elementTypeName = array.getElementTypeName();
  				if (StringUtils.hasText(elementTypeName)) {
  					try {
                          //使用反射机制创建指定类型的对象
  						elementType = ClassUtils.forName(elementTypeName, this.beanFactory.getBeanClassLoader());
  						array.resolvedElementType = elementType;
  					}
  					catch (Throwable ex) {
  						// Improve the message by showing the context.
  						throw new BeanCreationException(
  								this.beanDefinition.getResourceDescription(), this.beanName,
  								"Error resolving array type for " + argName, ex);
  					}
  				}
                  //没有获取到数组的类型，也没有获取到数组元素的类型 
                  //则直接设置数组的类型为 Object
  				else {
  					elementType = Object.class;
  				}
  			}
              //创建指定类型的数组
  			return resolveManagedArray(argName, (List<?>) value, elementType);
  		}
      	//解析 list 类型的属性值
  		else if (value instanceof ManagedList) {
  			// May need to resolve contained runtime references.
  			return resolveManagedList(argName, (List<?>) value);
  		}
      	//解析 set 类型的属性值
  		else if (value instanceof ManagedSet) {
  			// May need to resolve contained runtime references.
  			return resolveManagedSet(argName, (Set<?>) value);
  		}
      	//解析 map 类型的属性值
  		else if (value instanceof ManagedMap) {
  			// May need to resolve contained runtime references.
  			return resolveManagedMap(argName, (Map<?, ?>) value);
  		}
      	//解析 props 类型的属性值，props 其实就是 key 和 value 均为字符串的 map
  		else if (value instanceof ManagedProperties) {
  			Properties original = (Properties) value;
              //创建一个拷贝，用于作为解析后的返回值
  			Properties copy = new Properties();
  			original.forEach((propKey, propValue) -> {
  				if (propKey instanceof TypedStringValue) {
  					propKey = evaluate((TypedStringValue) propKey);
  				}
  				if (propValue instanceof TypedStringValue) {
  					propValue = evaluate((TypedStringValue) propValue);
  				}
  				if (propKey == null || propValue == null) {
  					throw new BeanCreationException(
  							this.beanDefinition.getResourceDescription(), this.beanName,
  							"Error converting Properties key/value pair for " + argName + ": resolved to null");
  				}
  				copy.put(propKey, propValue);
  			});
  			return copy;
  		}
      	//解析字符串类型的属性值
  		else if (value instanceof TypedStringValue) {
  			// Convert value to target type here.
  			TypedStringValue typedStringValue = (TypedStringValue) value;
  			Object valueObject = evaluate(typedStringValue);
  			try {
                  //获取属性的目标类型
  				Class<?> resolvedTargetType = resolveTargetType(typedStringValue);
  				if (resolvedTargetType != null) {
                      //对目标类型的属性进行解析，递归调用
  					return this.typeConverter.convertIfNecessary(valueObject, resolvedTargetType);
  				}
                  //没有获取到属性的目标对象，则按 Object 类型返回
  				else {
  					return valueObject;
  				}
  			}
  			catch (Throwable ex) {
  				// Improve the message by showing the context.
  				throw new BeanCreationException(
  						this.beanDefinition.getResourceDescription(), this.beanName,
  						"Error converting typed String value for " + argName, ex);
  			}
  		}
  		else if (value instanceof NullBean) {
  			return null;
  		}
  		else {
  			return evaluate(value);
  		}
  	}
  
  //解析引用类型的属性值
  @Nullable
  private Object resolveReference(Object argName, RuntimeBeanReference ref) {
  		try {
  			Object bean;
              //解析引用类型的属性值
  			String refName = ref.getBeanName();
  			refName = String.valueOf(doEvaluate(refName));
              //如果引用的对象在父类容器中，则从父类容器中获取指定的引用对象
  			if (ref.isToParent()) {
  				if (this.beanFactory.getParentBeanFactory() == null) {
  					throw new BeanCreationException(
  							this.beanDefinition.getResourceDescription(), this.beanName,
  							"Can't resolve reference to bean '" + refName +
  							"' in parent factory: no parent factory available");
  				}
  				bean = this.beanFactory.getParentBeanFactory().getBean(refName);
  			}
              //从当前的容器中获取指定的引用 Bean 对象，如果指定的 Bean 没有被实例化 
              //则会递归触发引用 Bean 的初始化和依赖注入
  			else {
  				bean = this.beanFactory.getBean(refName);
                  //将当前实例化对象的依赖引用对象
  				this.beanFactory.registerDependentBean(refName, this.beanName);
  			}
  			if (bean instanceof NullBean) {
  				bean = null;
  			}
  			return bean;
  		}
  		catch (BeansException ex) {
  			throw new BeanCreationException(
  					this.beanDefinition.getResourceDescription(), this.beanName,
  					"Cannot resolve reference to bean '" + ref.getBeanName() + "' while setting " + argName, ex);
  		}
  }
  
  /**
   * For each element in the managed array, resolve reference if necessary.
   */
  //将当前实例化对象的依赖引用对象
  private Object resolveManagedArray(Object argName, List<?> ml, Class<?> elementType) {
      	//创建一个指定类型的数组，用于存放和返回解析后的数组
  		Object resolved = Array.newInstance(elementType, ml.size());
  		for (int i = 0; i < ml.size(); i++) {
              //递归解析 array 的每一个元素，并将解析后的值设置到 resolved 数组中，索引为 i
  			Array.set(resolved, i,
  					resolveValueIfNecessary(new KeyedArgName(argName, i), ml.get(i)));
  		}
  		return resolved;
  }
  ```

  通过上面的代码分析，我们明白了Spring是如何将引用类型，内部类以及集合类型等属性进行解析的，
  属性值解析完成后就可以进行依赖注入了，依赖注入的过程就是 Bean 对象实例设置到它所依赖的 Bean
  对象属性上去，在之前我们已经说过，依赖注入是通过 `bw.setPropertyValues` 方法实现的，该
  方法也使用了委托模式，在 `BeanWrapper` 接口中至少定义了方法声明，依赖注入的具体实现交由其实
  现类 `BeanWrapperImpl` 来完成，下面我们就分析依 BeanWrapperImpl 中赖注入相关的源码。

* `BeanWrapperImpl` 对 `Bean `属性的依赖注入

  BeanWrapperImpl 类主要是对容器中完成初始化的 Bean 实例对象进行属性的依赖注入，即把 Bean 对
  象设置到它所依赖的另一个 Bean 的属性中去。然而，BeanWrapperImpl 中的注入方法实际上由
  AbstractNestablePropertyAccessor 来实现的，其相关源码如下:

  ```java
  //实现属性依赖注入功能
  protected void setPropertyValue(PropertyTokenHolder tokens, PropertyValue pv) throws BeansException {
  		if (tokens.keys != null) {
  			processKeyedProperty(tokens, pv);
  		}
  		else {
  			processLocalProperty(tokens, pv);
  		}
  }
  
  //实现属性依赖注入功能
  @SuppressWarnings("unchecked")
  private void processKeyedProperty(PropertyTokenHolder tokens, PropertyValue pv) {
      	//调用属性的 getter(readerMethod)方法，获取属性的值
  		Object propValue = getPropertyHoldingValue(tokens);
  		PropertyHandler ph = getLocalPropertyHandler(tokens.actualName);
  		if (ph == null) {
  			throw new InvalidPropertyException(
  					getRootClass(), this.nestedPath + tokens.actualName, "No property handler found");
  		}
  		Assert.state(tokens.keys != null, "No token keys");
  		String lastKey = tokens.keys[tokens.keys.length - 1];
  		
      	//注入 array 类型的属性值
  		if (propValue.getClass().isArray()) {
  			Class<?> requiredType = propValue.getClass().getComponentType();
  			int arrayIndex = Integer.parseInt(lastKey);
  			Object oldValue = null;
  			try {
  				if (isExtractOldValueForEditor() && arrayIndex < Array.getLength(propValue)) {
  					oldValue = Array.get(propValue, arrayIndex);
  				}
  				Object convertedValue = convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(),
  						requiredType, ph.nested(tokens.keys.length));
                  //注入 array 类型的属性值
  				int length = Array.getLength(propValue);
  				if (arrayIndex >= length && arrayIndex < this.autoGrowCollectionLimit) {
  					Class<?> componentType = propValue.getClass().getComponentType();
  					Object newArray = Array.newInstance(componentType, arrayIndex + 1);
  					System.arraycopy(propValue, 0, newArray, 0, length);
  					setPropertyValue(tokens.actualName, newArray);
                      //注入 array 类型的属性值
  					propValue = getPropertyValue(tokens.actualName);
  				}
                  //将属性的值赋值给数组中的元素
  				Array.set(propValue, arrayIndex, convertedValue);
  			}
  			catch (IndexOutOfBoundsException ex) {
  				throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName,
  						"Invalid array index in property path '" + tokens.canonicalName + "'", ex);
  			}
  		}
  		//注入 list 类型的属性值
  		else if (propValue instanceof List) {
              //获取 list 集合的类型
  			Class<?> requiredType = ph.getCollectionType(tokens.keys.length);
  			List<Object> list = (List<Object>) propValue;
              //获取 list 集合的 size
  			int index = Integer.parseInt(lastKey);
  			Object oldValue = null;
  			if (isExtractOldValueForEditor() && index < list.size()) {
  				oldValue = list.get(index);
  			}
              //获取 list 解析后的属性值
  			Object convertedValue = convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(),
  					requiredType, ph.nested(tokens.keys.length));
  			int size = list.size();
              //如果 list 的长度大于属性值的长度，则多余的元素赋值为 null
  			if (index >= size && index < this.autoGrowCollectionLimit) {
  				for (int i = size; i < index; i++) {
  					try {
  						list.add(null);
  					}
  					catch (NullPointerException ex) {
  						throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName,
  								"Cannot set element with index " + index + " in List of size " +
  								size + ", accessed using property path '" + tokens.canonicalName +
  								"': List does not support filling up gaps with null elements");
  					}
  				}
  				list.add(convertedValue);
  			}
  			else {
  				try {
                      //将值添加到 list 中
  					list.set(index, convertedValue);
  				}
  				catch (IndexOutOfBoundsException ex) {
  					throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName,
  							"Invalid list index in property path '" + tokens.canonicalName + "'", ex);
  				}
  			}
  		}
  		//注入 map 类型的属性值
  		else if (propValue instanceof Map) {
              //获取 map 集合 key 的类型
  			Class<?> mapKeyType = ph.getMapKeyType(tokens.keys.length);
              //获取 map 集合 key 的类型
  			Class<?> mapValueType = ph.getMapValueType(tokens.keys.length);
  			Map<Object, Object> map = (Map<Object, Object>) propValue;
  			// IMPORTANT: Do not pass full property name in here - property editors
  			// must not kick in for map keys but rather only for map values.
  			TypeDescriptor typeDescriptor = TypeDescriptor.valueOf(mapKeyType);
              //获取 map 集合 key 的类型
  			Object convertedMapKey = convertIfNecessary(null, null, lastKey, mapKeyType, typeDescriptor);
  			Object oldValue = null;
  			if (isExtractOldValueForEditor()) {
  				oldValue = map.get(convertedMapKey);
  			}
              //获取 map 集合 key 的类型
  			// Pass full property name and old value in here, since we want full
  			// conversion ability for map values.
  			Object convertedMapValue = convertIfNecessary(tokens.canonicalName, oldValue, pv.getValue(),
  					mapValueType, ph.nested(tokens.keys.length));
              //获取 map 集合 key 的类型
  			map.put(convertedMapKey, convertedMapValue);
  		}
  
  		else {
  			throw new InvalidPropertyException(getRootClass(), this.nestedPath + tokens.canonicalName,
  					"Property referenced in indexed property path '" + tokens.canonicalName +
  					"' is neither an array nor a List nor a Map; returned value was [" + propValue + "]");
  		}
  	}
  
  	private Object getPropertyHoldingValue(PropertyTokenHolder tokens) {
  		// Apply indexes and map keys: fetch value for all keys but the last one.
  		Assert.state(tokens.keys != null, "No token keys");
  		PropertyTokenHolder getterTokens = new PropertyTokenHolder(tokens.actualName);
  		getterTokens.canonicalName = tokens.canonicalName;
  		getterTokens.keys = new String[tokens.keys.length - 1];
  		System.arraycopy(tokens.keys, 0, getterTokens.keys, 0, tokens.keys.length - 1);
  
  		Object propValue;
  		try {
              //获取属性值
  			propValue = getPropertyValue(getterTokens);
  		}
  		catch (NotReadablePropertyException ex) {
  			throw new NotWritablePropertyException(getRootClass(), this.nestedPath + tokens.canonicalName,
  					"Cannot access indexed value in property referenced " +
  					"in indexed property path '" + tokens.canonicalName + "'", ex);
  		}
  
  		if (propValue == null) {
  			// null map value case
  			if (isAutoGrowNestedPaths()) {
  				int lastKeyIndex = tokens.canonicalName.lastIndexOf('[');
  				getterTokens.canonicalName = tokens.canonicalName.substring(0, lastKeyIndex);
  				propValue = setDefaultValue(getterTokens);
  			}
  			else {
  				throw new NullValueInNestedPathException(getRootClass(), this.nestedPath + tokens.canonicalName,
  						"Cannot access indexed value in property referenced " +
  						"in indexed property path '" + tokens.canonicalName + "': returned null");
  			}
  		}
  		return propValue;
  	}
  ```

  通过对上面注入依赖代码的分析，我们已经明白了 Spring IOC 容器是如何将属性的值注入到 Bean 实
  例对象中去的:
  (1).对于集合类型的属性，将其属性值解析为目标类型的集合后直接赋值给属性。

  (2).对于非集合类型的属性，大量使用了 JDK 的反射和内省机制，通过属性的 getter 方法(reader
  Method)获取指定属性注入以前的值，同时调用属性的 setter 方法(writer Method)为属性设置注入
  后的值。看到这里相信很多人都明白了 Spring 的 setter 注入原理。

### 整体流程图

![03.Tom_VIP_20180415_Spring核心IOC容器及依赖注入原理_课堂笔记](/Volumes/Seagate Backup Plus Drive/咕泡学院/2018期录播视频及资料/02.源码分析专题/03.Spring5源码分析/课件及笔记/03.Tom_VIP_20180415_Spring核心IOC容器及依赖注入原理_课堂笔记.png)

### `Bean`初始化中的扩展点



![Bean初始化过程中的扩展点](/Users/denny/Documents/Bean初始化过程中的扩展点.jpg)

### Spring IOC的一些技术细节

* `AbstractBeanFactory `的 getBean 方法调用 `FactoryBean`

  `FactoryBeanRegistrySupport` 类的 `getObjectFromFactoryBean` 方法，该方法实现了 Bean 工厂生
  产 Bean 实例对象。`FactoryBean`的名称以`&`开头。

* 工厂 Bean 的实现类 getObject 方法创建 Bean 实例对象

  `FactoryBean` 的实现类有非常多，比如:Proxy、RMI、JNDI、ServletContextFactoryBean 等等，
  `FactoryBean` 接口为 Spring 容器提供了一个很好的封装机制，具体的 getObject 有不同的实现类根
  据不同的实现策略来具体提供。

* `BeanPostProcessor` 后置处理器

  `BeanPostProcessor` 后置处理器是 Spring IOC 容器经常使用到的一个特性。

  ```java
  public interface BeanPostProcessor {
  
      //为在 Bean 的初始化前提供回调入口
  	@Nullable
  	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
  		return bean;
  	}
  
      //为在 Bean 的初始化之后提供回调入口
  	@Nullable
  	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
  		return bean;
  	}
  
  }
  ```

* `initializeBean `方法为容器产生的 Bean 实例对象添加 `BeanPostProcessor `后置处理器

  在 AbstractAutowireCapableBeanFactory 类中，initializeBean 方法实现为容器创建的 Bean
  实例对象添加 BeanPostProcessor 后置处理器。

  BeanPostProcessor 是一个接口，其初始化前的操作方法和初始化后的操作方法均委托其实现子类来
  实现，在 Spring 中，BeanPostProcessor 的实现子类非常的多，分别完成不同的操作，如:AOP 面向
  切面编程的注册通知适配器、Bean 对象的数据校验、Bean 继承属性/方法的合并等等。

* AdvisorAdapterRegistrationManager 在 Bean 对象初始化后注册通知适配器

  AdvisorAdapterRegistrationManager 是 BeanPostProcessor 的一个实现类，其主要的作用为容器
  中管理的 Bean 注册一个面向切面编程的通知适配器，以便在 Spring 容器为所管理的 Bean 进行面向切
  面编程时提供方便。

* Spring IOC 容器 autowiring 实现原理:

  配置 autowiring 属性实现依赖自动装配特性。`AbstractAutoWireCapableBeanFactory`中的

  `autowireByType`和`autowireByType`方法。

  ```java
  protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
  ...
  	PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);
  
  		if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME || mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
  			MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
              //autowiring为根据名称注入
  			// Add property values based on autowire by name if applicable.
  			if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME) {
  				autowireByName(beanName, mbd, bw, newPvs);
  			}
              //autowiring为根据类型注入
  			// Add property values based on autowire by type if applicable.
  			if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
  				autowireByType(beanName, mbd, bw, newPvs);
  			}
  			pvs = newPvs;
  		}
   //对非 autowiring 的属性进行依赖注入处理
   ...
  ```

  

  ```Java
  protected void autowireByName(
  			String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
  
  		String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
  		for (String propertyName : propertyNames) {
  			if (containsBean(propertyName)) {
  				Object bean = getBean(propertyName);
  				pvs.add(propertyName, bean);
  				registerDependentBean(propertyName, beanName);
  				if (logger.isTraceEnabled()) {
  					logger.trace("Added autowiring by name from bean name '" + beanName +
  							"' via property '" + propertyName + "' to bean named '" + propertyName + "'");
  				}
  			}
  			else {
  				if (logger.isTraceEnabled()) {
  					logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName +
  							"' by name: no matching bean found");
  				}
  			}
  		}
  	}
  ```

  ```Java
  //根据类型对属性进行自动依赖注入
  protected void autowireByType(
  			String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
  		//获取用户定义的类型转换器
  		TypeConverter converter = getCustomTypeConverter();
  		if (converter == null) {
  			converter = bw;
  		}
  
      	//存放解析的要注入的属性
  		Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
  		//对 Bean 对象中非简单属性(不是简单继承的对象，如原始类型，字符 //URL 等都是简单属性)进行处理
  		String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
  		for (String propertyName : propertyNames) {
  			try {
  				PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
  				// Don't try autowiring by type for type Object: never makes sense,
  				// even if it technically is a unsatisfied, non-simple property.
                  //不对 Object 类型的属性进行 autowiring 自动依赖注入
  				if (Object.class != pd.getPropertyType()) {
                      //获取属性的 setter 方法
  					MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
  					// Do not allow eager init for type matching in case of a prioritized post-processor.
                      //检查指定类型是否可以被转换为目标对象的类型
  					boolean eager = !PriorityOrdered.class.isInstance(bw.getWrappedInstance());
                      //创建一个要被注入的依赖描述
  					DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);
                      //根据容器的 Bean 定义解析依赖关系，返回所有要被注入的 Bean 对象
  					Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, converter);
                      //为属性赋值所引用的对象
  					if (autowiredArgument != null) {
  						pvs.add(propertyName, autowiredArgument);
  					}
  					for (String autowiredBeanName : autowiredBeanNames) {
                          //指定名称属性注册依赖 Bean 名称，进行属性依赖注入
  						registerDependentBean(autowiredBeanName, beanName);
  						if (logger.isTraceEnabled()) {
  							logger.trace("Autowiring by type from bean name '" + beanName + "' via property '" +
  									propertyName + "' to bean named '" + autowiredBeanName + "'");
  						}
  					}
                      //释放已自动注入的属性
  					autowiredBeanNames.clear();
  				}
  			}
  			catch (BeansException ex) {
  				throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
  			}
  		}
  	}
  ```

  ```java
  //为指定的 Bean 注入依赖的 Bean	
  public void registerDependentBean(String beanName, String dependentBeanName) {
      	//处理 Bean 名称，将别名转换为规范的 Bean 名称
  		String canonicalName = canonicalName(beanName);
  		//多线程同步，保证容器内数据的一致性
  		//先从容器中:bean 名称-->全部依赖 Bean 名称集合找查找给定名称 Bean 的依赖 Bean
  		synchronized (this.dependentBeanMap) {
              //获取给定名称 Bean 的所有依赖 Bean 名称
              //为 Bean 设置依赖 Bean 信息
  			Set<String> dependentBeans =
  					this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
              //向容器中:bean 名称-->全部依赖 Bean 名称集合添加 Bean 的依赖信息
              //即，将 Bean 所依赖的 Bean 添加到容器的集合中
  			if (!dependentBeans.add(dependentBeanName)) {
  				return;
  			}
  		}
  		
      	//从容器中:bean 名称-->指定名称 Bean 的依赖 Bean 集合找查找给定名称 Bean 的依赖 Bean
  		synchronized (this.dependenciesForBeanMap) {
              //向容器中:bean 名称-->指定 Bean 的依赖 Bean 名称集合添加 Bean 的依赖信息 
              //即，将 Bean 所依赖的 Bean 添加到容器的集合中
  			Set<String> dependenciesForBean =
  					this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
              
  			dependenciesForBean.add(canonicalName);
  		}
  }
  ```

  通过对 autowiring 的源码分析，我们可以看出，autowiring 的实现过程:
  a、对 Bean 的属性代调用 getBean 方法，完成依赖 Bean 的初始化和依赖注入。
  b、将依赖 Bean 的属性引用设置到被依赖的 Bean 属性上。
  c、将依赖 Bean 的名称和被依赖 Bean 的名称存储在 IOC 容器的集合中。

## Spring AOP实现原理

### Spring AOP体系结构

![Spring的AOP体系](/Users/denny/Documents/Spring的AOP体系.jpg)

* Adivce

  连接点处理逻辑。描述Spring AOP围绕方法调用而注入的切面行为。

* PointCut

  决定Adivce通知应该作用于那个连接点，就是通过PointCut定义需要增强的方法的集合。

* Advisor

  将通知(Adivce)和关注点(PointCut)进行结合。

Spring Aop的代理对象(AopProxy)通过如下三种实现生成：

* `ProxyFactoryBean`在IOC容器中完成声明式配置Spring AOP功能。配置target、advisor等信息。
* `ProxyFactory`则是编程式实现Spring AOP功能。
* `AspectJProxyFactory`采用AspectJ实现Spring AOP功能。

Spring的`AopProxy`接口：

![Spring AopProxy体系](/Users/denny/Documents/Spring AopProxy体系.jpg)

配置可以通过xml文件来进行，大概有四种方式：

1.        配置ProxyFactoryBean，显式地设置advisors, advice, target等。

2.        配置AutoProxyCreator，这种方式下，还是如以前一样使用定义的bean，但是从容器中获得的其实已经是代理对象。

3.        通过<aop:config>来配置

4.        通过<aop: aspectj-autoproxy>来配置，使用AspectJ的注解来标识通知及切入点。

## Spring MVC实现原理

### Spring MVC整体流程图





​	





















