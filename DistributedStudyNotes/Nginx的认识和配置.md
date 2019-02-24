# Nginx的认识和配置

## 什么是Nginx

是一个高性能的反向代理服务器
正向代理代理的是客户端
反向代理代理的是服务端

## Apache、Tomcat和Nginx

Apache和Nginx为静态Web服务器。

Tomcat为jsp/serlvet容器。

## Nginx安装

下载Nginx并解压：

```
wget http://nginx.org/download/nginx-1.15.8.tar.gz

tar -zxvf nginx-1.15.8.tar.gz
```

安装依赖包：gcc-c++、OpenSSL、zlib、PCRE 。

```
# yum install gcc-c++
# yum install pcre
# yum install pcre-devel
# yum install zlib 
# yum install zlib-devel
# yum install openssl
# yum install openssl-devel
```

配置并安装：

```
./configure --prefix=/home/denny/Software/nginx

make & make install
```

非root用户启动nginx (非root用户无权限使用小于1024的端口)：

```shell
chown root:root nginx
chmod u+s nginx #非root用户拥有sudo权限，像root用户一样执行
```

启动、停止、重新加载nginx：

```shell
./nginx
#或者
./nginx -c /home/denny/Software/nginx/conf//nginx.conf

```

```shell
./nginx -s stop
```

```shell
./nginx -s reload
```

## Nginx配置信息

nginx.conf

### Main

配置用户；工作进程数；错误日志配置。

工作进程数(worker_processes)可以按CUP核心数进行配置。

```shell
#查看CPU核心数
$ lscpu
$ cat /proc/cpuinfo | grep 'processor' | wc -l
```

### Events

IO模型和允许的连接数。

* ### accept_mutex(on/off)

  accept_mutex参数将使每个可用的worker进程逐个接受新连接。如果accept_mutex为off，所有可用的worker将从等待状态唤醒，但只有一个worker处理连接。这导致惊群现象，每秒重复多次。 这种现象导致服务器性能下降，因为所有被唤醒的worker都在占用CPU时间。 这导致增加了非生产性CPU周期和未使用的上下文切换。

  ```shell
  accept_mutex on;
  ```

* ### accept_mutex_delay

  当启用accept_mutex时，只有一个具有互斥锁的worker程序接受连接，而其他工作程序则轮流等待。 accept_mutex_delay对应于worker等待的时间帧，然后它尝试获取互斥锁并开始接受新的连接。 默认值为500毫秒。

  ```shell
  accept_mutex_delay 500ms;
  ```

* ### worker_connections

  默认值为512.该指令设置worker进程最大打开的连接数。

  ```shell
  worker_connections 512;
  ```

* ### worker_rlimit_nofile

  同时连接的数量受限于系统上可用的文件描述符的数量，因为每个套接字将打开一个文件描述符。 如果NGINX尝试打开比可用文件描述符更多的套接字，会发现error.log中出现Too many opened files的信息。
  使用ulimit检查文件描述符的数量：

  ```shell
  $ ulimit -n
  ```

  现在，将此值增加到大于worker_processes * worker_connections的值。 应该是增加当前worker运行用户的最大文件打开数值。
  NGINX提供了worker_rlimit_nofile指令，这是除了ulimit的一种设置可用的描述符的方式。 该指令与使用ulimit对用户的设置是同样的效果。此指令的值将覆盖ulimit的值，如：

  ```shell
  worker_rlimit_nofile 20960;
  ```

* ### multi_accept(on/off)

  multi_accept指令使得Nginx worker能够在获得新连接的通知时尽可能多的接受连接。 此指令的作用是立即接受所有连接放到监听队列中。 如果指令被禁用，worker进程将逐个接受连接。

  ```shell
  multi_accept on;
  ```

### Http

作为web服务器的相关配置。

#### 虚拟主机配置

```shell
server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }
        
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
}
```

可以配置多个虚拟主机。

####基于IP的虚拟主机

`server_name`配置为IP地址。

####基于端口号的虚拟主机

```shell
    server {
        listen       8080;
        server_name  localhost;

        location / {
            root   html;
            index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
```

####基于域名的虚拟主机

一级域名如：dennyedu.com

二级域名如：www.dennyedu.com、bbs.dennyedu.com、git.dennyedu.com、ask.dennyedu.com

配置虚拟主机，映射不同的域名。

```shell
   server {
        listen       80;
        server_name  www.dennyedu.com;

        location / {
            root   html;
            index  www.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
    server {
        listen       80;
        server_name  bbs.dennyedu.com;

        location / {
            root   html;
            index  bbs.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
    server {
        listen       80;
        server_name  git.dennyedu.com;

        location / {
            root   html;
            index  bbs.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
    
    server {
        listen       80;
        server_name  ask.dennyedu.com;

        location / {
            root   html;
            index  ask.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
```

#### Location

根据用户请求的URL进行匹配，响应请求。

##### 匹配语法

`location [= | ~* | ^~ ] /uri/ {...}`

##### 配置规则

* 精准匹配

  `location = /url`

* 前缀匹配

  `location ^~ /url`

* 正则匹配

  `location ~ /url`区分大小写

  `location ~* /url`不区分大小写

* 通用匹配

  `location /`

可以动静分离。

##### 配置规则优先级

```
1、location = /
2、location = /index
3、location ^~ /article/
4、location ^~ /article/files
5、location ~ \.(git|pgn|js|css)$
6、location /

http://192.168.3.37/ -> 1
http://192.168.3.37/index -> 2
http://192.168.3.37/article/files/1.txt -> 4
http://192.168.3.37/denny.png -> 5
```

* 精准匹配优先级最高
* 普通匹配，存在多个有效匹配，采用最长的匹配
* 正则匹配

##### 实际使用建议

```
location = / {
    
}

location / {
    
}

location ~ /.(png|css|js|git|)$ {
    
}
```

### Nginx模块

反向代理、email等。

#### 模块分类

* 核心模块
  * ngx_http_core_module
* 标准模块
  * http模块
* 第三方模块

##### ngx_http_core_module

包括：server块。

`location`实现URL到文件系统路径的映射。

`error_page`根据错误码重定向指定路径页面。

```shell
error_page   500 502 503 504  /50x.html;
location = /50x.html {
	root   html;
}
```

#####ngx_http_access_module

限制HTTP请求访问。

```shell
location ~ /\.ht {
   deny  all;
}
```

##### 添加第三方模块

不支持动态添加第三方模块。

* 原有所安装的配置，必须在安装新模块时加上。如：

  ```shell
  [denny@znode3 sbin]$ ./nginx -V
  nginx version: nginx/1.15.8
  built by gcc 4.8.5 20150623 (Red Hat 4.8.5-36) (GCC) 
  configure arguments: --prefix=/home/denny/Software/nginx
  ```

  不能直接`make install`这样会覆盖掉原先所有的配置(如：nginx.conf中的配置)。

* 安装方法

  * 安装命令

  ```shell
  ./configure --prefix=/home/denny/Software/nginx --add-moudule=/第三方模块的目录
  ```

  ```shell
  ./configure --prefix=/home/denny/Software/nginx --with-模块名称
  ```

  如：

  ```shell
  ./configure --prefix=/home/denny/Software/nginx --with-http_stub_status_module --with-http_random_index_module
  ```

  `make`并拷贝覆盖：

  ```shell
   make
   cp /home/denny/Software/nginx-1.15.8/objs/nginx /home/denny/Software/nginx/sbin/nginx
  ```

##### http_stub_status_module

监控nginx HTTP请求。

配置：

```shell
location /status {
	stub_status;
}
```

如下：

![image-20190219150050474](/Users/denny/Library/Application Support/typora-user-images/image-20190219150050474.png)

信息包括：活动连接数、accepts(接受的请求数)、handled(已处理的请求数)、requests(总共请求数)。

##### http_random_index_module

首页随机展示。

配置如下：

```shell
        location / {
            root   html;
            random_index on;
            index  www.html index.htm;
        }
```

随机展示`html`目录下的页面。





 2013/7







