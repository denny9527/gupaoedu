/**   
 * @Title: ZooKeeperDemo.java 
 * @Package com.denny.zookeeper 
 * @Description: TODO
 * @author Zhangkui zhangkui_java@163.com   
 * @date 2018年12月26日 下午5:25:00 
 * @version V1.0   
 */
package com.denny.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/** 
 * @ClassName: ZooKeeperDemo 
 * @Description: TODO
 * @author Zhangkui zhangkui_java@163.com 
 * @date 2018年12月26日 下午5:25:00 
 * Zookeeper的应用场景
 * 1、分布式锁
 * 2、协议地址维护(寻址)，服务注册中心
 * 3、服务上下线动态感知
 * 4、负载均衡
 * 节点数据说明：
 * 	czxid:创建事务ID
 * 	ctime:创建时间
 *  mzxid:修改事务ID
 *  mtime:修改时间
 *  pzxid:修改事务ID
 *  cversion:创建版本
 *  dataVersion:数据版本(乐观锁版本控制)
 *  aclVersion:访问控制权限版本
 *  ephemeralOwner:临时节点所有者
 *  dataLength:数据长度
 *  numChildren:子节点数量
 *  
 * Watcher监听
 * 注册监听操作方法：exists/getData/getChildren
 * exists：可以监听节点创建、修改(set)及删除操作
 * getData：可以监听节点修改(set)及删除操作
 * getChildren：可以监听子节点的创建和删除操作
 * 
 * EventType(事件类型)
 * 	None (-1) 连接事件
 * 	NodeCreated (1) 节点创建事件
 * 	NodeDeleted (2) 节点删除事件
 * 	NodeDataChanged (3) 节点数据变更事件
 * 	NodeChildrenChanged (4) 子节点变更事件
 * 
 * 节点类型
 * 持久节点、临时节点、有序节点、同级节点唯一、
 * 
 * 节点创建模式(CreateMode)：
 * PERSISTENT：持久节点
 * PERSISTENT_SEQUENTIAL：持久有序节点
 * EPHEMERAL：临时节点
 * EPHEMERAL_SEQUENTIAL：临时有序节点
 * 
 */
public class ZooKeeperDemo {

	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public ZooKeeperDemo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws KeeperException 
	 * @throws InterruptedException 
	 * @throws IOException  
	 * @Title: main 
	 * @Description: TODO 
	 * @param @param args
	 * @return void
	 * @throws 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		// TODO Auto-generated method stub
		final CountDownLatch cdl = new CountDownLatch(1);//多线程协调
		final ZooKeeper zk = new ZooKeeper("10.20.29.64,10.20.29.65,10.20.29.66", 4000, new Watcher() {

			/* (non-Javadoc) 
			 * <p>Title: process</p> 
			 * <p>Description: </p> 
			 * @param event 
			 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent) 
			 */
			public void process(WatchedEvent event) { //监听客户状态及节点事件(可重复监听)
				if(event.getType().equals(Watcher.Event.EventType.None)) {
					System.out.println("客户端连接已完成："+event.getType());
					System.out.println("监听事件："+event.getPath()+" "+event.getType());
					cdl.countDown();//释放当前线程
				}else {
					System.out.println("监听事件："+event.getPath()+" "+event.getType());
				}
			}
			
		}, true); //参数：connectString->集群IP逗号分隔;sessionTimeout->客户端会话超时时间;watcher->事件监听器;canBeReadOnly->是否只读.
		System.out.println("ZooKeeper当前状态："+zk.getState());
		//Thread.sleep(1000);
		cdl.await();//阻塞当前线程
		System.out.println("ZooKeeper当前状态："+zk.getState());
		Stat stat;
		//stat = zk.exists("/denny-zk", true);
		stat = zk.exists("/denny-zk", new Watcher() {//一次性监听事件

			/* (non-Javadoc) 
			 * <p>Title: process</p> 
			 * <p>Description: </p> 
			 * @param event 
			 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent) 
			 */
			public void process(WatchedEvent event) {
				System.out.println("监听节点事件："+event.getPath()+" "+event.getType());
				try {
					zk.exists("/denny-zk", new Watcher() {//重复监听事件设置

						/* (non-Javadoc) 
						 * <p>Title: process</p> 
						 * <p>Description: </p> 
						 * @param event 
						 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent) 
						 */
						public void process(WatchedEvent event) {
							System.out.println("监听节点事件："+event.getPath()+" "+event.getType());
							
						}
						
					});
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		if(null == stat) {//监听节点创建、修改和删除事件(create/setData/delete)
			zk.create("/denny-zk", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);//创建持久节点
		}
		Stat stat1 = new Stat();
		zk.getData("/denny-zk", true, stat1);//获取节点数据.watch:true 由ZooKeeper类中的Watcher监听事件 false:不监听事件
		zk.setData("/denny-zk", "33".getBytes(), stat1.getVersion());//修改节点数据.
		System.out.println("cZxid:"+stat1.getCzxid());
		System.out.println("ctime:"+stat1.getCtime());
		System.out.println("mZxid:"+stat1.getMzxid());
		System.out.println("mtime:"+stat1.getMtime());
		System.out.println("pZxid:"+stat1.getPzxid());
		System.out.println("dataVersion:"+stat1.getVersion());
		System.out.println("aclVersion:"+stat1.getAversion());
		System.out.println("ephemeralOwner:"+stat1.getEphemeralOwner());
		System.out.println("dataLength:"+stat1.getDataLength());
		
		//子节点控制(事件触发)
		zk.getChildren("/denny-zk", true);
		Stat stat2 = new Stat();
		stat2 = zk.exists("/denny-zk/childNode01", false);
		if(null == stat2) {
			zk.create("/denny-zk/childNode01", "100".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);			
		}
	}

}
