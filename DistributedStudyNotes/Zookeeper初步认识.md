# Zookeeper初步认识

## 什么是Zookeeper

分布式协调服务。

### 主要用途：

* 命名服务
  * 服务协议地址维护(服务注册发现)
* 配置管理
  * 分布式配置信息管理
* 分布式协调服务/通知
  * 服务上下线感知
* 集群管理
  * 负载均衡
  * Master/Slave集群管理，Leader选举。
* 分布式锁
  * 实现排他锁和共享锁

### 数据存储

采用K-V形式存储。Key：以路径形式表示，可存在子节点路径(如：/api/v1)。`/`为根路径。value为二进制数据

### 节点类型及特点

* 同级节点唯一性

* 节点类型

  * 临时节点（EPHEMERAL）

    客户端和Zookeeper服务器会话期间有效。当客户端与Zookeeper服务断开连接将自动删除。

  * 持久化节点（PERSISTENT）

  * 有序节点

    编号自动递增。包括：持久有序节点（PERSISTENT_SEQUENTIAL）、临时有序节点（EPHEMERAL_SEQUENTIAL）。

### 节点角色

* Leader

  事务性请求唯一调度和处理者，保证事务处理顺序性。

* Fllower

  处理客户端非事务性请求，将事务性请求转发给Leader。参与事务性请求投票和Leader选举投票。

* Observer

  提供非事物请求服务，同步集群中Leader状态变化。不采用事务性请求投票和Leader选举投票。

### 常用命令

* 启动命令

  ```shell
  bin/zkServer.sh start
  ```

* 客户端连接命令

  ```shell
  ./zkCli.sh -server 192.168.3.36:2181
  ```

* 节点操作

  * 创建节点

    ```shell
    #create [-s] [-e] path data acl
    create -e /orderService orderService
    ```

    -e 为临时节点；-s 为持久节点

  * 更新节点

    ```
    #set path data [version]
    
    ```

  * 删除节点

    ```shell
    #delete path [version]
    rmr /orderService
    ```

  * 查询节点

    ```shell
    #ls path [watch]
    ls /orderService
    ```

  * 统计节点

     ```shell
    #stat path [watch]
    stat /orderService
     ```

    

  * 获取节点信息

    ```shell
    #get path [watch]
    get /orderService
    ```

## Zookeeper的安装部署

### 集群安装

集群服务器：

192.168.3.14 znode1 

192.168.3.14 znode2

192.168.3.14 znode3

* 安装包下载：https://archive.apache.org/dist/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz

* 解压

  ```shell
  tar -zxvf zookeeper-3.4.13.tar.gz
  ```

  

* 配置信息

  * 三个节点的$ZK_HOME/conf/zoo.cfg，配置参数：

    ```properties
    #定义时间单位为：2000毫秒
    tickTime=2000
    #初始化同步。 Follower在启动过程中，会从Leader同步所有最新数据，然后确定自己能够对外服务的起始状态。Leader允许Fllower在 initLimit 时间内完成这个工作。(即:10*2000)
    initLimit=10
    # Leader与Follower之间心跳响应时间长度(即:5*2000)。在运行过程中，Leader负责与ZK集群中所有机器进行通信，例如通过一些心跳检测机制，来检测机器的存活状态。如果L发出心跳包在syncLimit之后，还没有从F那里收到响应，那么就认为这个F已经不在线了。
    syncLimit=5
    #数据存储目录
    dataDir=/home/denny/data/zookeeper
    #客户端连接端口
    clientPort=2181
    #集群节点配置
    server.1=znode1:2888:3888
    server.2=znode2:2888:3888
    server.3=znode3:2888:3888
    ```

  * 在znode1、znode2和znode3节点上的Zookeeper数据存储目录中的myid文件中依次配置如下参数：

    `znode1`

    ```
    1
    ```

    `znode2`

    ```
    2
    ```

    `znode3`

    ```
    3
    ```

    

  * 分别启动znode1、znode2和znode3的Zookeeper服务。效果如图：

    ![image-20190205085747350](/Users/denny/Library/Application Support/typora-user-images/image-20190205085747350.png)

## zoo.cfg配置文件分析

参数配置：

* tickTime

  定义时间单位为：2000毫秒。如：

  ```properties
  tickTime=2000
  ```

* initLimit

  初始化同步。 Follower在启动过程中，会从Leader同步所有最新数据，然后确定自己能够对外服务的起始状态。Leader允许Fllower在 initLimit 时间内完成这个工作。(即:10*2000)

* syncLimit

  Leader与Follower之间心跳响应时间长度(即:5*2000)。在运行过程中，Leader负责与ZK集群中所有机器进行通信，例如通过一些心跳检测机制，来检测机器的存活状态。如果L发出心跳包在syncLimit之后，还没有从F那里收到响应，那么就认为这个F已经不在线了。

* dataDir

  数据存储目录

* clientPort

  客户端连接端口，一般为：2181

* 集群配置：

  ```properties
  server.1=znode1:2888:3888
  server.2=znode2:2888:3888
  server.3=znode3:2888:3888
  ```

  



