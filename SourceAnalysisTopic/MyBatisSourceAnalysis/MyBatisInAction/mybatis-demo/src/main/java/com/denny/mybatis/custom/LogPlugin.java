package com.denny.mybatis.custom;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Intercepts(value = {
        @Signature(type = StatementHandler.class, method = "prepare", args={Connection.class, Integer.class})
})
@Slf4j
public class LogPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("----------- intercept query start.... ---------");

        // 调用方法，实际上就是拦截的方法
        Object result = invocation.proceed();

        log.info("----------- intercept query end.... ---------");

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);//嵌套包装
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
