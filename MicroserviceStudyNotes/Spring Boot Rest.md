## 第三节 Spring Boot Rest

### 主要内容

####Rest理论基础

Rpc(Remote Procedure Call)

* 语言相关	
  * Java-RMI(Remote Method Invocation)
  * .NET-Com+
* 语言无关
  * SOA
    * Web Service
      * SOAP(传输介质协议)
      * Http、SMTP(通讯协议)
  * 微服务(MSA)
    * Rest
      * HTML、JSON、XML等
      * HTTP(通讯协议)
        * Http1.1
          * 短连接
          * Keep-Alive
          * 连接池
          * Long Polling
        * WebScokets
        * Http/2
          * 长连接
      * 技术
        * Spring客户端：RestTemplate
        * Spring WebMVC：@RestController = @Controller + @ResponseBoby
        * Spring Cloud：`RestTmplate`扩展 + `@LoadBalanced`

####REST(英文)

#####Cacheability(可缓存性)

`@ResponseBoby`->响应体

* 响应(Response)

  * 响应头(Headers)

    * 请求方法

      * HEAD

    * 元信息(Meta-Data)

      * Accept Language->`Locale`
      * Connection->Keep-Alive

    * 实现

      多值Map`MultiValueMap`

      key:value=1:n

      ```
      public class HttpHeaders implements MultiValueMap<String, String>, Serializable {
      	...
      }
      ```

      

  * 响应体

    * 业务信息
    * Boby：HTTP实体、REST
      * `@ResponseBoby`
      * `HttpEntity.body`属性(泛型)
    * Payload：消息JMS、事件、SOAP

  ```dd
  public class HttpEntity<T> {
      public static final HttpEntity<?> EMPTY = new HttpEntity();
      private final HttpHeaders headers;
      @Nullable
      private final T body;
   }
  ```

  

#####Http状态码(org.springframework.http.HttpStatus)

* 200
  * org.springframework.http.HttpStatus#OK
* 304
  * org.springframework.http.HttpStatus#NOT_MODIFIED
  * 第一次完整请求，获取响应头(200)，直接获取
  * 第二次请求，当响应头为：304时，取上次的结果
* 404
  * org.springframework.http.HttpStatus#NOT_FOUND
* 500
  * org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR

##### Http 缓存设置

* *if-None-Match:< ETag>* 

  * Spring Boot 使用ShallowEtagHeaderFilter，配置类如下：

    ```
    @Configuration
    public class WebConfig {
    
    
        @Bean
        public Filter shallowEtagHeaderFilter() {
            return new ShallowEtagHeaderFilter();
        }
    }
    ```

    使用实例：

    ```
    @RequestMapping("/testEtag")
    public ResponseEntity<String> testEtag(@RequestParam(value = "val", required = false) String val){
    
           if(null == val){
               val = "testEtag";
           }
           String etag =  DigestUtils.md5DigestAsHex(val.getBytes());
    
           ResponseEntity<String> entity = ResponseEntity.ok().eTag(etag).body("Hello World");
    
           return entity;
        }
    ```

* Cache-Control:max-age=xxx

##### Uniform Interface(统一接口)

######URI 资源定位

###### 资源操作-HTTP动词

* **GET**

  * `@GetMapping`

  * 注解别名属性和覆盖

    ```
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @RequestMapping(  //注解派生性
        method = {RequestMethod.POST}
    )
    public @interface PostMapping {
        @AliasFor( //注解别名
            annotation = RequestMapping.class
        )
        String name() default "";
        ...
    }
    ```

    * `@PostMapping`是注解
    * @RequestMapping`是元注解标注了`@PostMapping`

    `@AliasFor`只能标注目标注解属性，该注解的`annotation()`属性必须是元注解，该注解的`attribute()`属性必须是元注解的属性。

* **PUT**

  * `@PutMapping`

* **PATCH**

  * `@PatchMapping`

  * Servlet API中没有规定PATCH

  * Spring MVC 对其做了扩展

    FrameworkServlet

    ```
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
            if (httpMethod != HttpMethod.PATCH && httpMethod != null) {
                super.service(request, response);
            } else {
                this.processRequest(request, response);
            }
    
        }
    ```

    

* **POST**

  * `@PostMapping`

* **DELETE**

  * `@DeleteMapping`

###### 自描述信息

###### 注解驱动

* `@RequestBoby`

​        JSON-> `MappingJackson2CborHttpMessageConverter`

* `@ResponseBody`

​        JSON-> `MappingJackson2CborHttpMessageConverter`

​        Text->`StringHttpMessageConverter`

返回值处理类：`RequestResponseBodyMethodProcessor`



媒体类型(`MediaType`)

* org.springframework.http.MediaType#APPLICATION_JSON_UTF8_VALUE
  * "application/json;charset=UTF-8"



消息转换器(HttpMessageConverter)：

* application/json
  * `MappingJackson2CborHttpMessageConverter`
* text/html
  * `StringHttpMessageConverter`

接口编程

`ResponseEntity` extends `HttpEntity`



`RequestEntity` extends `HttpEntity`

返回值处理类：

###### 代码导读

`EnableWebMvc`

* 导入`DelegatingWebMvcConfiguration` (配置Bean)

* 注册`WebMvcConfigurer`

  * 装配Spring Mvc所需要的Bean

  * 注解驱动扩展点

    * `HandlerMethodArgumentResolver`

    * `HandlerMethodReturnValueHandler`

    * `@RequestBoby`和`@ResponseBody`处理实现类

      * `RequestResponseBodyMethodProcessor`

        处理`@RequestBoby`和`@DeleteMapping`



实现`WebMvcConfigurer`

`WebMvcConfigurerAdapter`实现



URI与URL区别：

U：Uniform

R：Resource

I：鉴别

L：定位



URI

```
URI = scheme:[//authority]path[?query][#fragment]
```

scheme：http、webchat

URL

protocol协议

#### Spring  MVC 拦截器

`HandlerInterceptor`拦截Action方法：

```
public interface HandlerInterceptor {
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
```

实现类：

* HandlerInterceptorAdapter
* LocaleChangeInterceptor
* ...

####Spring  MVC 异常处理

`HandlerExceptionResolver`

注解实现：

`@ExceptionHandler`  需要和`@ControllerAdvice`配合使用

```
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {
    Class<? extends Throwable>[] value() default {};
}

```

实例：

```
@ControllerAdvice
public class CustExceptionResolver {

    @ExceptionHandler(value = Exception.class)
    public @ResponseBody String exceptionHandler(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod){
        return handlerMethod.getBeanType().getName() + "." + handlerMethod.getMethod().getName() + "处理发生异常！";
    }
}

```







