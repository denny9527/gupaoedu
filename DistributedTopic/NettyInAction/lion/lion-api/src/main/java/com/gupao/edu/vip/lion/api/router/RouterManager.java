
package com.gupao.edu.vip.lion.api.router;

import java.util.Set;


public interface RouterManager<R extends Router> {

    /**
     * 注册路由
     *
     * @param userId 用户ID
     * @param router 新路由
     * @return 如果有旧的的路由信息则返回之，否则返回空。
     */
    R register(String userId, R router);

    /**
     * 删除路由
     *
     * @param userId     用户ID
     * @param clientType 客户端类型
     * @return true:成功，false:失败
     */
    boolean unRegister(String userId, int clientType);

    /**
     * 查询路由
     *
     * @param userId 用户ID
     * @return userId对应的所有的路由信息
     */
    Set<R> lookupAll(String userId);

    /**
     * 查询指定设备类型的用户路由信息
     *
     * @param userId     用户ID
     * @param clientType 客户端类型
     * @return 指定类型的路由信息
     */
    R lookup(String userId, int clientType);
}
