/**   
 * @Title: Curator.java 
 * @Package com.denny.zookeeper 
 * @Description: TODO
 * @author Zhangkui zhangkui_java@163.com   
 * @date 2018年12月27日 下午3:51:30 
 * @version V1.0   
 */
package com.denny.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/** 
 * @ClassName: Curator 
 * @Description: TODO
 * @author Zhangkui zhangkui_java@163.com 
 * @date 2018年12月27日 下午3:51:30 
 * 使用Curator
 */
public class Curator {

	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public Curator() {
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
		//CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("10.20.29.64,10.20.29.65,10.20.29.66", 4000, 4000, retryPolicy);
		CuratorFramework cf = CuratorFrameworkFactory.builder()
							   .connectString("10.20.29.64,10.20.29.65,10.20.29.66")
							   .sessionTimeoutMs(4000)
							   .connectionTimeoutMs(4000)
							   .retryPolicy(retryPolicy)
							   .build();
	  
		cf.start(); //开启客户端
		cf.create().forPath("/denny-zk-df", "222".getBytes());
		cf.checkExists().creatingParentsIfNeeded().forPath("/denny-zk-df/childNode01");
		cf.close(); //关闭客户端
		
	}

}
