/**   
 * @Title: CuratorDemo.java 
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
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/** 
 * @ClassName: CuratorDemo 
 * @Description: TODO
 * @author Zhangkui zhangkui_java@163.com 
 * @date 2018年12月27日 下午3:51:30 
 * Curator业务场景
 * 1、分布式锁
 * 2、Leader选举
 * 3、队列
 * 4、共享
 * 
 */
public class CuratorDemo {

	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public CuratorDemo() {
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
							   .namespace("curator")
							   .build();
	  
		cf.start(); //开启客户端
		//创建节点：/curator/denny-zk-df/childNode01
		//creatingParentsIfNeeded:原生API需要逐层创建节点，父节点必须存在子节点
		
		if(cf.checkExists().forPath("/denny-zk-df/childNode01") == null) {//判断节点是否存在
			cf.create().creatingParentsIfNeeded()
			   .withMode(CreateMode.PERSISTENT)
			   .forPath("/denny-zk-df/childNode01");
		}

		//修改节点：/curator/denny-zk-df/childNode02
		Stat stat = new Stat();
		cf.getData().storingStatIn(stat).forPath("/denny-zk-df/childNode01");
		cf.setData().withVersion(stat.getVersion()).forPath("/denny-zk-df/childNode01", "100".getBytes());
		
		//删除节点：/curator/denny-zk-df/childNode01
		cf.delete().deletingChildrenIfNeeded().forPath("/denny-zk-df/childNode01");

		cf.close(); //关闭客户端
		
	}

}
