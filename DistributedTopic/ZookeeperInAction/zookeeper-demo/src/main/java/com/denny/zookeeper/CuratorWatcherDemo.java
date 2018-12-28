/**   
 * @Title: CuratorWatcherDemo.java 
 * @Package com.denny.zookeeper 
 * @Description: TODO
 * @author Zhangkui zhangkui_java@163.com   
 * @date 2018年12月28日 下午5:18:19 
 * @version V1.0   
 */
package com.denny.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/** 
 * @ClassName: CuratorWatcherDemo 
 * @Description: TODO
 * @author Zhangkui zhangkui_java@163.com 
 * @date 2018年12月28日 下午5:18:19 
 *  
 */
public class CuratorWatcherDemo {

	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public CuratorWatcherDemo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws Exception  
	 * @Title: main 
	 * @Description: TODO 
	 * @param @param args
	 * @return void
	 * @throws 
	 */
	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);//休眠毫秒数、重试次数
		CuratorFramework cf = CuratorFrameworkFactory.builder()
							   .connectString("10.20.29.64,10.20.29.65,10.20.29.66")
							   .sessionTimeoutMs(4000)
							   .connectionTimeoutMs(4000)
							   .retryPolicy(retryPolicy)
							   .namespace("curator")
							   .build();
		cf.start();//客户端启动
		
		addListenerWithNodeCache(cf, "/denny-zk-df");
		
		//cf.close();//客户端关闭
		System.in.read();
	}
	
	
	/**
	 * 
	 * PathChildCache 监听一个节点下的子节点的创建、删除和更新
	 * NodeCache 监听一个节点的创建和更新
	 * TreeCache 包含PathChildCache和NodeCache
	 * @throws Exception 
	 * 
	 */
	
	public static void addListenerWithNodeCache(CuratorFramework cf, String path) throws Exception {
		final NodeCache nodeCache = new NodeCache(cf, path);
		NodeCacheListener ncl = new NodeCacheListener() {

			/* (non-Javadoc) 
			 * <p>Title: nodeChanged</p> 
			 * <p>Description: </p> 
			 * @throws Exception 
			 * @see org.apache.curator.framework.recipes.cache.NodeCacheListener#nodeChanged() 
			 */
			public void nodeChanged() throws Exception {
				System.out.println("Receive Event "+nodeCache.getCurrentData().getPath());
				
			}
		};
		nodeCache.getListenable().addListener(ncl);
		nodeCache.start();
	}
	

}
