# Nginx实践应用

## 反向代理配置

反向代理配置实例：

```java
server {
    listen 80;
    server_name localhost;
    location / {
       proxy_pass http://192.168.3.14:8080;
       proxy_set_header Host $host;
       proxy_set_header X-Real-IP $remote_addr;
       proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
	} 
}
```

 将客户端的信息放入到后端服务器请求头中：

```
       proxy_set_header Host $host;
       proxy_set_header X-Real-IP $remote_addr;
       proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;#如果中间有多次代理将获取所有客户端的IP地址。
```



## 负载均衡实战

* Upstream

  配置实例：

  ```shell
  upstream tomcat {
     server 192.168.3.14:8080;
     server 192.168.3.37:8080;
  }
  
  server {
     listen 80;
     server_name localhost;
  
     location / {
          proxy_pass http://tomcat;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          
     }
  }
  ```

* 负载均衡算法

  * 轮询算法(默认)，如果后端服务器宕机，会自动踢出。

  * `ip_hash`算法

    根据请求的IP地址进行Hash。每个访客固定访问一个后端服务器，可以解决session粘连的问题。如：

    ```shell
    upstream tomcat {
       ip_hash;
       server 192.168.3.14:8080;
       server 192.168.3.37:8080;
    }
    ```

    

  * 权重轮询算法

    指定每个服务器的权重，如：

    ```shell
    upstream tomcat {
       server 192.168.3.14:8080 weight=1;
       server 192.168.3.37:8080 weight=2;
    }
    ```

* 相关配置信息

  * proxy_next_upstream

    语法:`proxy_next_upstream [error | timeout | invalid_header | http_500 | http_502 | http_503 | http_504 | http_404 | off ]; `

    默认:`proxy_next_upstream error timeout`;
     配置块:http、server、location 这个配置表示当向一台上有服务器转发请求出现错误的时候，继续换一台上后服务器来处理这个请求。 

    默认情况下，上游服务器一旦开始发送响应数据，Nginx反向代理服务器会立刻把应答包转发给客户端。因此，一 旦Nginx开始向客户端发送响应包，如果中途出现错误也不允许切换到下一个上有服务器继续处理的。这样做的目 的是保证客户端只收到来自同一个上游服务器的应答。 

  * proxy_connect_timeout

    语法: `proxy_connect_timeout time`; 

    默认: `proxy_connect_timeout 60s`; 

    范围: http, server, location 

    用于设置nginx与upstream server的连接超时时间，比如我们直接在location中设置proxy_connect_timeout 1ms， 1ms很短，如果无法在指定时间建立连接，就会报错。 

  * proxy_send_timeout

    向后端写数据的超时时间，两次写操作的时间间隔如果大于这个值，也就是过了指定时间后端还没有收到数据，连接会被关闭。

  * proxy_read_timeout

    从后端读取数据的超时时间，两次读取操作的时间间隔如果大于这个值，那么nginx和后端的链接会被关闭，如果一个请求的处理时间比较长，可以把这个值设置得大一些。

  * proxy_upstream_fail_timeout

    设置了某一个upstream后端失败了指定次数(max_fails)后，在fail_timeout时间内不再去请求它,默认为10秒 语法 `server address [fail_timeout=30s]`。如：

    ```shell
    upstream backend { #服务器集群名字
     	server 192.168.218.129:8080 weight=1 max_fails=2 fail_timeout=600s; 
    	server 192.168.218.131:8080 weight=1 max_fails=2 fail_timeout=600s; 
    } 
    ```

##动静分离配置

在Nginx的conf目录下，有一个mime.types文件 。用户访问一个网站，然后从服务器端获取相应的资源通过浏览器进行解析渲染最后展示给用户，而服务端可以返回 各种类型的内容，比如xml、jpg、png、gif、flash、MP4、html、css等等，那么浏览器就是根据mime-type来决 定用什么形式来展示的服务器返回的资源给到浏览器时，会把媒体类型告知浏览器，这个告知的标识就是Content-Type，比如Content- Type:text/html。 

配置实例：

静态资源放在本地目录。

```java
location ~ .*\.(js|css|png|svg|ico|jpg)$ {
       valid_referers none blocked 192.168.11.160 www.gupaoedu.com;
       if ($invalid_referer) {
			return 404; 
       }
       root static-resource;
	   expires 1d; 
}
```

也可以单独搭建静态资源服务，通过Upstream负载均衡反向代理即可。

### 缓存

响应头：

`Cache-control/expires/etag`

当一个客户端请求web服务器, 请求的内容可以从以下几个地方获取:服务器、浏览器缓存中或缓存服务器中。这 取决于服务器端输出的页面信息浏览器缓存将文件保存在客户端，好的缓存策略可以减少对网络带宽的占用，可以提高访问速度，提高用户的体 验，还可以减轻服务器的负担nginx缓存配置 。

nginx会对静态资源响应加入`Etag`属性进行浏览器端缓存。

相关配置如：

```shell
location ~ .*\.(js|css|png|svg|ico|jpg)$ {
       valid_referers none blocked 192.168.11.160 www.gupaoedu.com;
       if ($invalid_referer) {
			return 404; 
       }
       etag on;
       root static-resource;
	   expires 1d; #过期时间1天
}
```



### 压缩

Gzip

我们一个网站一定会包含很多的静态文件，比如图片、脚本、样式等等，而这些css/js可能本身会比较大，那么在
网络传输的时候就会比较慢，从而导致网站的渲染速度。因此Nginx中提供了一种Gzip的压缩优化手段，可以对后
端的文件进行压缩传输，压缩以后的好处在于能够降低文件的大小来提高传输效率 "

配置参数：

* gzip on|off 是否开启gzip压缩
* gzip_buffers 4 16k #设置gzip申请内存的大小，作用是按指定大小的倍数申请内存空间。4 16k代表按照原始数据大小以16k为单位的4倍申请内存。
* gzip_comp_level[1-9] 压缩级别， 级别越高，压缩越小，但是会占用CPU资源。
* gzip_disable #正则匹配UA 表示什么样的浏览器不进行gzip。
* gzip_min_length #开始压缩的最小长度(小于多少就不做压缩)，可以指定单位，比如 1k。
* gzip_http_version 1.0|1.1 表示开始压缩的http协议版本。
* gzip_proxied (nginx 做前端代理时启用该选项，表示无论后端服务器的headers头返回什么信息，都无条件启用压缩)。
* gzip_type text/pliain,application/xml 对那些类型的文件做压缩 (conf/mime.conf)。
* gzip_vary on|off 是否传输gzip压缩标识; 启用应答头"Vary: Accept-Encoding";给代理服务器用的，有的浏览器支持压缩，有的不支持，所以避免浪费不支持的也压缩，所以根据客户端的HTTP头来判断，是否需要压缩。

配置实例：

```shell
http {
    include       mime.types;
    default_type  application/octet-stream;
    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';
    #access_log  logs/access.log  main;
    sendfile        on;
    #tcp_nopush     on;
    keepalive_timeout  60;
    include extra/*.conf;
    gzip  on;
    gzip_min_length 5k;
    gzip_comp_level 3;
    gzip_types application/javascript image/jpeg image/svg+xml;
    gzip_buffers 4 32k;
    gzip_vary on;
 }

```

### 防盗链

一个网站上会有很多的图片，如果你不希望其他网站直接用你的图片地址访问自己的图片，或者希望对图片有版权
保护。再或者不希望被第三方调用造成服务器的负载以及消耗比较多的流量问题，那么防盗链就是你必须要做的。

在Nginx中配置防盗链其实很简单，
语法: valid_referers none | blocked | server_names | string ...;
默认值: —
上下文: server, location

“Referer”请求头为指定值时，内嵌变量`$invalid_referer`被设置为空字符串，否则这个变量会被置成“1”。查找匹配时不区分大小写，其中none表示缺少referer请求头，blocked表示请求头存在，但是它的值被防火墙或者代理服务器删除，server_names表示referer请求头包含指定的虚拟主机名。

配置实例如下：

允许192.168.3.38访问静态资源。

```shell
location ~ .*.(gif|jpg|ico|png|css|svg|js)$ { 
	valid_referers none blocked 192.168.3.38;
	if ($invalid_referer) { 
		return 404;
	}
	root static-resource; 
}
```

### 跨域访问

什么叫跨域呢?如果两个节点的协议、域名、端口、子域名不同，那么进行的操作都是跨域的，浏览器为了安全问
题都是限制跨域访问，所以跨域其实是浏览器本身的限制。

允许跨域配置实例：

```shell
server{
   listen 80;
   server_name localhost;
   location / {
       proxy_pass http://192.168.3.37:8080;
       proxy_set_header Host $host;
       proxy_set_header X-Real-IP $remote_addr;
       proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
       proxy_send_timeout 60s;
	   proxy_read_timeout 60s;
       proxy_connect_timeout 60s;
       add_header 'Access-Control-Allow-Origin' '*'; #允许来自所有的访问地址
       add_header 'Access-Control-Allow-Methods' 'GET,PUT,POST,DELETE,OPTIONS'; #支持的
       请求方式
       add_header 'Access-Control-Allow-Header' 'Content-Type,*'; #支持的媒体类型
   }
   location ~ .*\.(gif|jpg|ico|png|css|svg|js)$ {
       root static-resource;
   }
}

```

##多进程模型原理

多进程+多路复用。

master、woker进程。

```
root      79989      1  0 01:28 ?        00:00:00 nginx: master process ./nginx
denny     79990  79989  0 01:28 ?        00:00:00 nginx: worker process
```

worker_processes 1 #设置为 CPU 总核心数

Linux epoll、select。

配置如下：

```
#user  nobody;
user denny;  #用户
worker_processes  1; #woker进程数，一般设置为CPU核心数

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;

events {
    use epoll #IO 模型
    worker_connections  1024; #每个worker支持的最大并发连接数。接受连接总数=worker_connections * worker_processes
}
...
```

##高可用集群实战

Nginx 作为反向代理服务器，所有的流量都会经过 Nginx，所以 Nginx 本身的可靠性是我们首先要考虑的问题。

### Keepalived

Keepalived 是 Linux 下一个轻量级别的高可用解决方案，Keepalived 软件起初是专为 LVS 负载均衡软件设计的，用来管理并监控 LVS 集群系统中各个服务节点的状态，后来又加入了可以实现高可用的 VRRP 功能。因此，Keepalived 除了能够管理 LVS 软件外，还可以作为其他服务(例如:Nginx、Haproxy、MySQL 等)的高可用解决方案软件。

Keepalived 软件主要是通过 VRRP 协议实现高可用功能的。VRRP 是 Virtual Router Redundancy Protocol(虚拟路由器冗余协议)的缩写，VRRP 出现的目的就是为了解决静态路由单点故障问题的，它能够保证当个别节点宕机时，整个网络可以不间断地运行;(简单来说，vrrp 就是把两台或多态路由器设备虚拟成一个设备，实现主备高可用)。

所以，Keepalived 一方面具有配置管理 LVS 的功能，同时还具有对 LVS 下面节点进行健康检查的功能，另一方面也可实现系统网络服务的高可用功能。

LVS 是 Linux Virtual Server 的缩写，也就是 Linux 虚拟服务器，在 linux2.4 内核以后，已经完全内置了 LVS 的各个功能模块。

关于四层负载，我们知道 osi 网络层次模型的 7 层模模型(应用层、表示层、会话 层、传输层、网络层、数据链路层、物理层);四层负载就是基于传输层，也就是 ip+端口的负载;而七层负载就是需要基于 URL 等应用层的信息来做负载，同时还 有二层负载(基于 MAC)、三层负载(IP); 

常见的四层负载有:LVS、F5; 七层负载有:Nginx、HAproxy; 在软件层面， Nginx/LVS/HAProxy 是使用得比较广泛的三种负载均衡软件。对于中小型的 Web 应用，可以使用 Nginx、大型网站或者重要的服务并且服务比 较多的时候，可以考虑使用 LVS。

轻量级的高可用解决方案

LVS 四层负载均衡软件(Linux virtual server)
监控 lvs 集群系统中的各个服务节点的状态
VRRP 协议(虚拟路由冗余协议)
linux2.4 以后，是内置在 linux 内核中的

lvs(四层) -> HAproxy 七层

lvs(四层) -> Nginx（七层）

#### Keepalived实现Nginx集群高可用

![996253-20180521160609590-1552379716](/Users/denny/Downloads/996253-20180521160609590-1552379716.png)

下载安装Keepalived

```
yum install keepalived
#或下载http://www.keepalived.org/software/keepalived-2.0.13.tar.gz
tar -zxvf keepalived-2.0.7.tar.gz
./configure --prefix=/usr/local/keepalived --sysconf=/etc
make & make install
```

安装依赖包：

```
yum -y install libnl libnl-devel
yum install -y libnfnetlink-devel
```



将keepalived加入到系统服务并启用：

```
ln -s /usr/local/keepalived/sbin/keepalived /sbin/
cp /usr/local/keepalived-2.0.7/keepalived/etc/init.d/keepalived /etc/init.d/
chkconfig --add keepalived
chkconfig keepalived on
```

启动keepalived服务：

```
service keepalived start
```

192.168.3.37和192.168.3.38分别安装nginx端口号：80。

VIP（虚拟IP）：192.168.3.100

主备：192.168.3.37（MASTER）、192.168.3.38（BACKUP）

RIP（真实IP）：192.168.3.37、192.168.3.38

192.168.3.37上的/etc/keepalived/keepalived.conf配置：

```shell
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL #运行 keepalived 服务器的标识，在一个网络内应该是唯一的
}

vrrp_instance VI_1 { #vrrp 实例定义部分
    state BACKUP #设置lvs的状态，MASTER和BACKUP两种，必须大写
    interface ens33 #设置对外服务的接口
    virtual_router_id 51 #设置虚拟路由标示，这个标示是一个数字，同一个 vr
rp 实例使用唯一标示
    priority 50 #定义优先级，数字越大优先级越高，在一个 vrrp——instance 下，
master 的优先级必须大于 backup
    advert_int 1 #设定 master 与 backup 负载均衡器之间同步检查的时间间隔，单
位是秒
    authentication { #设置验证类型和密码
        auth_type PASS
        auth_pass 1111 #验证密码，同一个 vrrp_instance 下 MASTER 和 BACKU P 密码必须相同
    }
    virtual_ipaddress { #设置虚拟 ip 地址，可以设置多个，每行一个
        192.168.3.100
    }
}

virtual_server 192.168.3.100 80 { #设置虚拟服务器，需要指定虚拟 ip 和服务 端口
    delay_loop 6 #健康检查时间间隔
    lb_algo rr #负载均衡调度算法
    lb_kind NAT #负载均衡转发规则
    persistence_timeout 50 #设置会话保持时间
    protocol TCP #指定转发协议类型，有 TCP 和 UDP 两种

    real_server 192.168.3.38 80 { #配置服务器节点 1，需要指定 real serve r 的真实 IP 地址和端口
        weight 1 #设置权重，数字越大权重越高
        TCP_CHECK { #realserver的状态监测设置部分单位秒
            connect_timeout 3 #超时时间
            retry 3 
            delay_before_retry 3 #重试间隔
	    	connect_port 80 #监测端口
        }
    }
}

```

192.168.3.38上的/etc/keepalived/keepalived.conf配置：

```shell
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL
}

vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.3.100
    }
}

virtual_server 192.168.3.100 80 {
    delay_loop 6
    lb_algo rr
    lb_kind NAT
    persistence_timeout 50
    protocol TCP

    real_server 192.168.3.38 80 {
        weight 1
        TCP_CHECK {
            connect_timeout 3
            retry 3
            delay_before_retry 3
	    connect_port 80
        }
    }
}
```

**keepalived** 日志文件配置

* 首先看一下/etc/sysconfig/keepalived 文件

  vi /etc/sysconfig/keepalived

  ```
  KEEPALIVED_OPTIONS="-D -d -S 0"
  ```

  “-D” 就是输出日志的选项
  这里的“-S 0”表示 local0.* 具体的还需要看一下/etc/syslog.conf 文件

* 在/etc/rsyslog.conf 里添加：

  ```shell
  local0.*               /var/log/keepalived.log
  ```

* 重新启动 keepalived 和 rsyslog 服务: 

  service rsyslog restart 

  service keepalived restart 

通过脚本实现动态切换

* 在Mater和Backup节点上的/usr/local/nginx/sbin/nginx_status_check.sh添加如下脚本：

```shell
#!/bin/bash
A=`ps -C nginx --no-header |wc -l`        
if [ $A -eq 0 ];then          
	sleep 2
    /usr/local/nginx/sbin/nginx                #重启nginx
    if [ `ps -C nginx --no-header |wc -l` -eq 0 ];then    #nginx重启失败
        echo 'nginx server is died'
        service keepalived stop
        exit 1
    else
        exit 0
    fi
else
    exit 0
fi
```

* 在 keepalived.conf 文件中添加脚本配置：

```shell
! Configuration File for keepalived

global_defs {
   router_id LVS_DEVEL
   enable_script_security
}
#添加脚本
vrrp_script nginx_status_process {
    script "/usr/local/nginx/sbin/nginx_status_check.sh"
    user root
    interval 3
    weight -10
}

vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.3.100
    }
    track_script {#执行监控的服务
    	nginx_status_process
    }
}

virtual_server 192.168.3.100 80 {
    delay_loop 6
    lb_algo rr
    lb_kind NAT
    persistence_timeout 50
    protocol TCP

    real_server 192.168.3.38 80 {
        weight 1
        TCP_CHECK {
            connect_timeout 3
            retry 3
            delay_before_retry 3
	    connect_port 80
        }
    }
}

```



### Openrestry



