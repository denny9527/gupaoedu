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
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
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
		
		//addListenerWithNodeCache(cf, "/denny-zk-df");
		//addListenerWithPathChildCache(cf, "/denny-zk-df");
		addListenerWithTreeCache(cf, "/denny-zk-df");
		
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
	
	/**
	 * 加入节点事件监听
	 * @Title: addListenerWithNodeCache 
	 * @Description: TODO 
	 * @param @param cf
	 * @param @param path
	 * @param @throws Exception
	 * @return void
	 * @throws
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
			//节点的创建和更新
			public void nodeChanged() throws Exception {
				System.out.println("Receive Event "+nodeCache.getCurrentData().getPath());
				
			}
		};
		nodeCache.getListenable().addListener(ncl);
		nodeCache.start();
	}
	
	/**
	 * 加入子节点事件监听
	 * @Title: addListenerWithPathChildCache 
	 * @Description: TODO 
	 * @param @param cf
	 * @param @param path
	 * @param @throws Exception
	 * @return void
	 * @throws
	 */
	public static void addListenerWithPathChildCache(CuratorFramework cf, String path) throws Exception {
		final PathChildrenCache pathChildCache = new PathChildrenCache(cf, path, true);
		PathChildrenCacheListener pccl = new PathChildrenCacheListener() {

			/* (non-Javadoc) 
			 * <p>Title: childEvent</p> 
			 * <p>Description: </p> 
			 * @param client
			 * @param event
			 * @throws Exception 
			 * @see org.apache.curator.framework.recipes.cache.PathChildrenCacheListener#childEvent(org.apache.curator.framework.CuratorFramework, org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent) 
			 */
			//子节点的创建、删除和更新
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				System.out.println("Receive Event "+event.getData().getPath()+" Event Type "+event.getType());
			}
			
		};
		pathChildCache.getListenable().addListener(pccl);
		pathChildCache.start();
	}
	
	/**
	 * @throws Exception 
	 * 加入TreeCache监听
	 * @Title: addListenerWithTreeCache 
	 * @Description: TODO 
	 * @param @param cf
	 * @param @param path
	 * @return void
	 * @throws
	 */
	public static void addListenerWithTreeCache(CuratorFramework cf, String path) throws Exception {
		final TreeCache treeCache = new TreeCache(cf, path);
		TreeCacheListener tcl = new TreeCacheListener() {

			/* (non-Javadoc) 
			 * <p>Title: childEvent</p> 
			 * <p>Description: </p> 
			 * @param client
			 * @param event
			 * @throws Exception 
			 * @see org.apache.curator.framework.recipes.cache.TreeCacheListener#childEvent(org.apache.curator.framework.CuratorFramework, org.apache.curator.framework.recipes.cache.TreeCacheEvent) 
			 */
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				System.out.println("Receive Event "+event.getData().getPath()+" Event Type "+event.getType());
				
			}
			
		};
		treeCache.getListenable().addListener(tcl);
		treeCache.start();
	}
	
}
