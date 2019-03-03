# Spring 常用设计模式及工厂模式

## 二十三种经典设计模式

| 分类   | 设计模式                                                     |
| ------ | ------------------------------------------------------------ |
| 创建型 | 工厂方法模式(Factory Method)、抽象工厂模式(Abstract Factory)、 建造者模式(Builder)、原型模式(Prototype)、单例模式(Singleton) |
| 结构型 | 适配器模式(Adapter)、桥接模式(Bridge)、组合模式(Composite)、 装饰器模式(Decorator)、门面模式(Facade)、享元模式(Flyweight)、 代理模式(Proxy) |
| 行为型 | 解释器模式(Interpreter)、模板方法模式(Template Method)、 责任链模式(Chain of Responsibility)、命令模式(Command)、 迭代器模式(Iterator)、调解者模式(Mediator)、备忘录模式(Memento)、 观察者模式(Observer)、状态模式(State)、策略模式(Strategy)、 访问者模式(Visitor) |

## 六大原则

* 开闭原则

  开闭原则就是说对扩展开放，对修改关闭。

* 里氏代换原则

  任何基类可以出现的地方，子类一定可以出现。LSP 是继承复用的基石，只有当衍生类可以
  替换掉基类，软件单位的功能不受到影响时，基类才能真正被复用，而衍生类也能够在基类的基础上增
  加新的行为。

* 依赖倒转原则

  针对接口编程，依赖于抽象而不依赖于具体。

* 接口隔离原则

  使用多个隔离的接口，比使用单个接口要好。还是一个降低类之间的耦合度的意思。

* 迪米特法原则

  一个实体应答尽量少的与其他实体之间发生相互作用，使得系统功能模块相对独立。

* 合成复用原则

  尽量使用合成/聚合的方式，而不是使用继承。

## 设计模式关系图



## Spring常用设计模式

### 工厂模式

#### 简单工厂模式

应用场景:简单工厂模式的实质是由一个工厂类根据传入的参数，动态决定应该创建哪一个产品类。
Spring 中的 BeanFactory 就是简单工厂模式的体现，根据传入一个唯一的标识来获得 Bean 对象，但
是否是在传入参数后创建还是传入参数前创建这个要根据具体情况来定

| 归类       | 特点                        | 穷举             |
| ---------- | --------------------------- | ---------------- |
| 创建型模式 | `是复杂工厂模式的思维模型 ` | 批量生产、标准化 |

类图如下：

![Model!简单工厂模式_0](/Users/denny/Documents/jpg/Model!简单工厂模式_0.jpg)

#### 工厂方法模式

应用场景:通常由应用程序直接使用 new 创建新的对象，为了将对象的创建和使用相分离，采用工厂模
式,即应用程序将对象的创建及初始化职责交给工厂对象。

一般情况下,应用程序有自己的工厂对象来创建 Bean.如果将应用程序自己的工厂对象交给 Spring 管
理,那么 Spring 管理的就不是普通的 Bean,而是工厂 Bean。

| 归类       | 特点                                                         | 穷举 |
| ---------- | ------------------------------------------------------------ | ---- |
| 创建型模式 | `对于调用者来说，隐藏了复杂的逻辑处理过程，调用 者只关心执行结果。 对于工厂来说要对结果负责，保证生产出符合规范的 产品。 ` |      |

BeanFactory和FactoryBean的区别：

* FactoryBean：以Bean结尾，表示它是个Bean，它并不是简单的Bean，而是一个能生产对象或者修饰对象的工厂Bean。实现org.spring-framework.beans.factory.FactoryBean接口，给出自己的对象实例化代码。
* BeanFactory：它是Spring IoC容器的一种形式，提供完整的IoC服务支持。即是一个管理Bean的工厂。

类图如下：

![Model1!工厂方法模式_1](/Users/denny/Documents/jpg/Model1!工厂方法模式_1.jpg)

#### 抽象工厂模式

应用场景：系统的产品有多于一个的产品族，而系统只消费其中某一族的产品。对于每一个产品族，都有一个具体工厂。而每一个具体工厂创建属于同一个产品族，但是分属于不同等级结构的产品。

Spring容器本身就是一个抽象工厂，不仅创建普通的Bean实例，也可以创建工厂Bean实例。同时管理Bean的生命周期及相互依赖关系(DI、IOC)。

类图如下：

![Model2!抽象工厂模式_2](/Users/denny/Documents/jpg/Model2!抽象工厂模式_2.jpg)

### 单例模式

应用场景:保证一个类仅有一个实例，并提供一个访问它的全局访问点。

Spring 中的单例模式完成了后半句话，即提供了全局的访问点 BeanFactory。但没有从构造器级别去
控制单例，这是因为 Spring 管理的是是任意的 Java 对象。 Spring 下默认的 Bean 均为单例。

| 归类       | 特点                                                         | 穷举                     |
| ---------- | ------------------------------------------------------------ | ------------------------ |
| 创建型模式 | 保证从系统启动到系统终止，全过程只会产生一个实 例。 当我们在应用中遇到功能性冲突的时候，需要使用单 例模式。  							 						 					配置文件、日历、IOC 容器 | 配置文件、日历、IOC 容器 |

为了避免多线程并发问题，保证线程安全。单例的可实现方式：

#### 饿汉式

在单例使用前，不管使用与否都先产生实例，避免线程安全问题。

```java
public class EagerSingleton {

    private static final EagerSingleton INSTANCE = new EagerSingleton();

    private EagerSingleton(){

    }

    public static EagerSingleton getInstance(){

        return INSTANCE;

    }

}
```

#### 懒汉式

默认在加载时不实例化，在使用时才实例化。

同步块实现：

```java
public class LazySingleton {

    private static LazySingleton INSTANCE;

    private LazySingleton(){

    }

    /**
     *  不管实例化与否都要进入同步块.
     * @return
     */
    public static synchronized LazySingleton getInstance(){
        if(INSTANCE == null)
            INSTANCE = new LazySingleton();
        return INSTANCE;
    }

}
```

双重检查锁实现:

```java
public class LazySingletonDoubleCheck {

    private static volatile LazySingletonDoubleCheck INSTANCE;

    private LazySingletonDoubleCheck(){

    }

    /**
     *  不管实例化与否都要进入同步块.
     * @return
     */
    public static LazySingletonDoubleCheck getInstance() {
        if (INSTANCE == null){
            synchronized(LazySingletonDoubleCheck.class){
                if(INSTANCE == null)
                    INSTANCE = new LazySingletonDoubleCheck();
                return INSTANCE;
            }
        }
        return INSTANCE;
    }

}
```

#### 注册登记式

每使用一次，都往固定容器中去注册并且将使用过的对象进行缓存，下次去取对象的时候，就直接从缓存中取值，以保证每次获取的都是同一个实例。Spring中的单例模式就是典型的注册登记式。

```java
public class SinglegonRegister {

    private static Map<String, Object> SINGLETON_CACHE = new ConcurrentHashMap<String, Object>();

    static {
        SINGLETON_CACHE.put("com.denny.spring.design.pattern.singleton.RegSinglegon", new SinglegonRegister());
    }

    private SinglegonRegister(){}

    private static synchronized Object getInstance(String name){
        if(name == null){
            name = "com.denny.spring.design.pattern.singleton.RegSinglegon";
        }
        Object instance = SINGLETON_CACHE.get(name);
        if(instance == null){
            register(name);
            instance = SINGLETON_CACHE.get(name);
        }
        return instance;
    }

    private static void register(String className){
        Class instanceClass = null;
        try {
            instanceClass = Class.forName(className);
            SINGLETON_CACHE.putIfAbsent(className, instanceClass.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    private static void unregister(String className){
        if(SINGLETON_CACHE.containsKey(className)){
            SINGLETON_CACHE.remove(className);
        }
    }

}
```



#### 内部类实现

```java
public class LazySingletonInner {

    public static LazySingletonInner getInstance(){
        return LazySingletonHolder.INSTANCE;
    }

    private LazySingletonInner(){}

    private static class LazySingletonHolder{
        private static final LazySingletonInner INSTANCE = new LazySingletonInner();
    }

}
```

#### 单例防止反射攻击

```java
public class SingletonNoReflectAttack {

    private static boolean IS_INSTANCED = false;

    private static final SingletonNoReflectAttack INSTANCE = new SingletonNoReflectAttack();

    private SingletonNoReflectAttack(){

        synchronized(SingletonNoReflectAttack.class){
            if(!IS_INSTANCED){
                IS_INSTANCED = !IS_INSTANCED;
            }else{
                throw new RuntimeException("单例已产生！");
            }

        }

    }

    public static SingletonNoReflectAttack getInstance(){
        return INSTANCE;

    }
}
```

#### 反序列化防止单例失效

```java
public class SingletonSerialization implements Serializable {

    private static final SingletonSerialization INSTANCE = new SingletonSerialization();

    private SingletonSerialization(){

    }

    public static SingletonSerialization getInstance(){

        return INSTANCE;

    }

    //防止反序列化单例失效
    private Object readResolve(){
        return INSTANCE;
    }
}
```

### 原型模式

应用场景:原型模式就是从一个对象再创建另外一个可定制的对象。Java中的Clone技术。

 如：DTO和VO类之间的属性值替换。Apache和Spring中的BeanUtils中的copyProperties方法。

| 归类       | 特点                                                         | 穷举         |
| ---------- | ------------------------------------------------------------ | ------------ |
| 创建型模式 | `首先有一个原型。 数据内容相同，但对象实例不同(完全两个个体)。 ` | 孙悟空吹毫毛 |

原型模式实例：

```java

public class Prototype implements Cloneable {

    private String name;

    private List<String> paramterList;

    public Prototype(String name, List<String> paramterList) {
        this.name = name;
        this.paramterList = paramterList;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Prototype{");
        sb.append("name='").append(name).append('\'');
        sb.append(", paramterList=").append(paramterList);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {

        List<String> paramterList = Arrays.asList("1", "2");
        Prototype prototype = new Prototype("denny", paramterList);

        try {
            System.out.println(prototype.clone().toString());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
```

浅度拷贝和深度拷贝：

```java
public class Prototype implements Cloneable, Serializable {

    private String name;

    private List<String> paramterList;

    public Prototype(String name, List<String> parameterList) {
        this.name = name;
        this.paramterList = parameterList;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        //return super.clone();//浅拷贝 paramterList 将引用原先的对象。可以手动深度拷贝。
        return deepClone();
    }

    /**
     * 深度拷贝 Clone后Prototype.paramterList 将为新对象实例。
     * @return
     */
    private Object deepClone(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        Object obj = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);

            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Prototype{");
        sb.append("name='").append(name).append('\'');
        sb.append(", paramterList=").append(paramterList);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {

        List<String> paramterList = Arrays.asList("1", "2");
        Prototype prototype = new Prototype("denny", paramterList);

        try {
            Prototype clonePrototype = (Prototype)prototype.clone();
            System.out.println(clonePrototype.paramterList == prototype.paramterList);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
```

### 模板方法模式



### 代理模式

应用场景:为其他对象提供一种代理以控制对这个对象的访问。从结构上来看和 Decorator 模式类似，
但 Proxy 是控制，更像是一种对功能的限制，而 Decorator 是增加职责。

AOP、拦截器、中介等功能。

Spring 的 Proxy 模式在 AOP 中有体现，比如 JdkDynamicAopProxy 和 Cglib2AopProxy。

| 归类       | 特点                                                         | 穷举                                                         |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 结构型模式 | `执行者、被代理人 对于被代理人来说，这件事情是一定要做的，但是我 自己又不想做或者没有时间做。 对于代理人而言，需要获取到被代理的人个人资料， 只是参与整个过程的某个或几个环节。 ` | 租房中介、售票黄牛、婚介、 经纪人、快递、事务代理、非 侵入式日志监听 |

* 静态代理

  类图如下：

  

* 动态代理

  * Java动态代理

    原理：

    1、获取被代理对象的引用，获取被代理对象的所有接口。

    2、使用JDK中Proxy类重新成一个代理类如：$Proxy0，同时生成的代理类要实现被代理对象中所有的需要实现的接口。

    3、把新的业务方法由一定的逻辑代码去调用。

    4、编译新生成的代理类生成class文件（字节码动态生成）

    5、最后加载到JVM中运行。

    使用Proxy 和InvocationHandler接口实现动态代理。

    ```java
    public interface Subject {
    
        public void operation();
    
    }
    ```

    ```java
    public class RealSubject implements Subject {
    
        @Override
        public void operation() {
            System.out.println("目标对象执行！");
        }
    
    }
    ```

    ```java
    public class DynamicProxy {
    
        private Subject subject = new RealSubject();
    
        public Subject getProxy(){
            Subject proxy = (Subject)Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), new Class[]{Subject.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("代理对象执行开始！");
                    Object object = method.invoke(subject, args);
                    System.out.println("代理对象执行结束！");
                    return object;
                }
            });
            return proxy;
        }
    
    }
    ```

    ```java
    public class DynamicProxyDemo {
    
        public static void main(String[] args) {
            DynamicProxy dynamicProxy = new DynamicProxy();
            dynamicProxy.getProxy().operation();
        }
    }
    ```

    动态代理类分析：

    使用ProxyGenerator.generateProxyClass生成代理类class文件，如下：

    ```java
    public class JavaProxyClassDemo {
    
        public static void main(String[] args) {
            FileOutputStream outputStream = null;
            try {
                byte[] bytes = ProxyGenerator.generateProxyClass("$Proxy0", new Class[]{Subject.class});
                String userDir = System.getProperty("user.dir");
                outputStream = new FileOutputStream(userDir+"/$Proxy0.class");
                outputStream.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    ```

    反编译代理类如下：

    ```java
    //
    // Source code recreated from a .class file by IntelliJ IDEA
    // (powered by Fernflower decompiler)
    //
    
    import com.denny.spring.design.pattern.proxy.Subject;
    import java.lang.reflect.InvocationHandler;
    import java.lang.reflect.Method;
    import java.lang.reflect.Proxy;
    import java.lang.reflect.UndeclaredThrowableException;
    
    public final class $Proxy0 extends Proxy implements Subject {
        private static Method m1;
        private static Method m3;
        private static Method m2;
        private static Method m0;
    
        public $Proxy0(InvocationHandler var1) throws  {
            super(var1);
        }
    
        public final boolean equals(Object var1) throws  {
            try {
                return (Boolean)super.h.invoke(this, m1, new Object[]{var1});
            } catch (RuntimeException | Error var3) {
                throw var3;
            } catch (Throwable var4) {
                throw new UndeclaredThrowableException(var4);
            }
        }
    
        public final void operation() throws  {
            try {
                super.h.invoke(this, m3, (Object[])null);
            } catch (RuntimeException | Error var2) {
                throw var2;
            } catch (Throwable var3) {
                throw new UndeclaredThrowableException(var3);
            }
        }
    
        public final String toString() throws  {
            try {
                return (String)super.h.invoke(this, m2, (Object[])null);
            } catch (RuntimeException | Error var2) {
                throw var2;
            } catch (Throwable var3) {
                throw new UndeclaredThrowableException(var3);
            }
        }
    
        public final int hashCode() throws  {
            try {
                return (Integer)super.h.invoke(this, m0, (Object[])null);
            } catch (RuntimeException | Error var2) {
                throw var2;
            } catch (Throwable var3) {
                throw new UndeclaredThrowableException(var3);
            }
        }
    
        static {
            try {
                m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
                m3 = Class.forName("com.denny.spring.design.pattern.proxy.Subject").getMethod("operation");
                m2 = Class.forName("java.lang.Object").getMethod("toString");
                m0 = Class.forName("java.lang.Object").getMethod("hashCode");
            } catch (NoSuchMethodException var2) {
                throw new NoSuchMethodError(var2.getMessage());
            } catch (ClassNotFoundException var3) {
                throw new NoClassDefFoundError(var3.getMessage());
            }
        }
    }
    
    ```

  * CGLib代理

    使用CGLIB实现类代理。如下：

    ```java
    public class CGLIBProxyDemo {
    
        public static void main(String[] args) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(RealSubject.class);//设置父类
            enhancer.setCallback(new MethodInterceptor() {//设置方法拦截回调
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                    System.out.println("代理对象执行开始！");
                    Object obj = methodProxy.invokeSuper(o, args);
                    System.out.println("代理对象执行结束！");
                    return obj;
                }
            });
            RealSubject realSubject = (RealSubject)enhancer.create();//创建子类的实例对象
            realSubject.operation();
        }
    
    }
    ```

### 策略模式

应用场景:定义一系列的算法，把它们一个个封装起来，并且使它们可相互替换。本模式使得算法可独
立于使用它的客户而变化。符合开闭原则。可以与工厂模式进行优化和改善。

Spring 中在实例化对象的时候用到 Strategy 模式，在 SimpleInstantiationStrategy 有使用。

| 归类       | 特点                                                 | 穷举                     |
| ---------- | ---------------------------------------------------- | ------------------------ |
| 行为型模式 | `最终执行结果是固定的。 执行过程和执行逻辑不一样。 ` | 旅游出行方式、支付方式等 |

类图如下：

![策略模式](/Users/denny/Documents/策略模式.jpg)

Spring中InstantiationStrategy案例：

![Spring的实例化策略模式](/Users/denny/Documents/Spring的实例化策略模式.jpg)

### 模板方法模式

定义一个操作中的算法的骨架，而将一些步骤延迟到子类中。Template Method 使得子类可以不改变
一个算法的结构即可重定义该算法的某些特定步骤。

| 归类       | 特点                                                         | 穷举                                                         |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 行为型模式 | `执行流程固定，但中间有些步骤有细微差别(运行时 才确定)。 可实现批量生产。 ` | Spring ORM 数据模型、BeanFactory类体系中大量使用了模板方法模式。 |

类图如下：

![模板方法模式](/Users/denny/Documents/模板方法模式.jpg)

### 委派模式

不属于 23 种设计模式之一，是面向对象设计模式中常用的一种模式。这种模式的原理是多个类去完成一项的工作，其中一个类去分发任务，其他类做具体的任务，而具体表现是这个委派类的工作，具体过程是被委派类来操作的。可以认为是代理模式和策略模式的结合。

应用场景如：

用户请求（Boss）将任务委派给项目经理（Leader），Leader将任务细化，根据每个人擅长的某一方面将细化后的任务分给指定的员工（Target）。项目经理看上去像是老板和员工间的中介，类似代理模式；项目经理分配任务前，需要做权衡（选择），类似策略模式。

| 归类       | 特点                                                         | 穷举                                                         |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 行为型模式 | `要和代理模式区分开来。 持有被委托人的引用。 不关心过程，只关心结果。 ` | 经理派发工作任务、  								DispatcherServlet、 |

DispatcherServlet负责将请求分配给各个解析器进行处理，进行全局的流程控制。如下图：

![image-20190214141848678](/Users/denny/Library/Application Support/typora-user-images/image-20190214141848678.png)

### 适配器模式

原理：将一个类的接口转换成客户希望的另一个接口。适配器模式让那些接口不兼容的类可以一起工作

Spring AOP 模块对 BeforeAdvice、AfterAdvice、ThrowsAdvice 三种通知类型的支持实际上是借助适配器模式来实现的，这样的好处是使得框架允许用户向框架中加入自己想要支持的任何一种通知类型，上述三种通知类型是 Spring AOP 模块定义的，它们是 AOP 联盟定义的 Advice 的子类型。

| 归类       | 特点                                                         | 穷举                                                |
| ---------- | ------------------------------------------------------------ | --------------------------------------------------- |
| 结构型模式 | 注重兼容、转换。 适配者与被适配这之间没有层级关系，也没有必然联 系。  满足 has-a 的关系。 | 编码解码、一拖三充电头、 HDMI 转 VGA、Type-C 转 USB |

* 类适配

![适配器模式(类适配)](/Users/denny/Documents/适配器模式(类适配).jpg)

* 对象适配

  ![适配器模式(对象适配)](/Users/denny/Documents/适配器模式(对象适配).jpg)

* SpringMVC 中的HandlerAdapter就是典型的适配器模式，如下：

![SpringMVC的HandlerAdapter模式](/Users/denny/Documents/SpringMVC的HandlerAdapter模式.jpg)

###装饰者模式

装饰者模式又名包装(Wrapper)模式。装饰者模式以对客户端透明的方式扩展对象的功能，是继承关系的一个替代方案。装饰者模式动态地将责任附加到对象身上。若要扩展功能，装饰者提供了比继承更有弹性的替代方案。

| 归类       | 特点                                                         | 穷举                             |
| ---------- | ------------------------------------------------------------ | -------------------------------- |
| 结构型模式 | 1、注重覆盖、扩展。 2、装饰器和被装饰器都实现同一个接口，主要目的 | IO 流包装、数据源包装、简 历包装 |

spring中用到的包装器模式在类名上有两种表现：一种是类名中含有Wrapper，另一种是类名中含有Decorator。基本上都是动态地给一个对象添加一些额外的职责。如： BeanWrapper等。

类图如下：

![装饰者模式](/Users/denny/Documents/装饰者模式.jpg)

### 观察者模式

定义了对象间的一种一对多的组合关系，以便一个对象的状态发生变化时，所有依赖于它的对象都得到通知并自动刷新。

Spring 中 Observer 模式常用的地方是 Listener 的实现。如 ApplicationListener。

| 归类       | 特点                                                         | 穷举                                  |
| ---------- | ------------------------------------------------------------ | ------------------------------------- |
| 行为型模式 | `一般由两个角色组成:发布者和订阅者(观察者)。 观察者通常有一个回调，也可以没有。 ` | 监听器、日志收集、短信通知、 邮件通知 |

类图如下：

![观察者模式](/Users/denny/Documents/观察者模式.jpg)

Spring的事件通知机制，如下：

![Spring事件机制](/Users/denny/Documents/Spring事件机制.jpg)