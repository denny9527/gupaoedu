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
		CountDownLatch cdl = new CountDownLatch(1);
		ZooKeeper zk = new ZooKeeper("10.20.29.64,10.20.29.65,10.20.29.66", 4000, new Watcher() {

			/* (non-Javadoc) 
			 * <p>Title: process</p> 
			 * <p>Description: </p> 
			 * @param event 
			 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent) 
			 */
			public void process(WatchedEvent event) {
				System.out.println("监听事件："+event.getPath()+event.getType());
				
			}
			
		}, true);
		System.out.println("ZooKeeper当前状态："+zk.getState());
		Thread.sleep(1000);
		System.out.println("ZooKeeper当前状态："+zk.getState());
		cdl.countDown();
		zk.create("/denny-zk", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);//持久节点创建
		Stat stat = new Stat();
		zk.getData("/denny-zk", false, stat);
		System.out.println(stat.toString());
	}

}
