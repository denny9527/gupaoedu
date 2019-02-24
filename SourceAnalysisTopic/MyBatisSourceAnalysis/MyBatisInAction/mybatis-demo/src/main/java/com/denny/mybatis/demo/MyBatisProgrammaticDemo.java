package com.denny.mybatis.demo;

import com.denny.mybatis.beans.Test;
import com.denny.mybatis.mapper.TestMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;


public class MyBatisProgrammaticDemo {

    public static void main(String[] args) {

        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        PooledDataSource pooledDataSource = new PooledDataSource();

        pooledDataSource.setDriver("com.mysql.cj.jdbc.Driver");
        pooledDataSource.setUrl("jdbc:mysql://192.168.3.37:3306/denny-test");
        pooledDataSource.setUsername("denny");
        pooledDataSource.setPassword("19830203");

        Environment environment = new Environment("development", transactionFactory, pooledDataSource);

        Configuration configuration = new Configuration(environment);

        //configuration.addMappers("com.denny.mybatis.mapper");
        configuration.addMapper(TestMapper.class);

        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            TestMapper testMapper = sqlSession.getMapper(TestMapper.class);//动态代理生成代理对象$Proxy->MapperProxy
            System.out.println(testMapper.selectByPrimaryKey(1));

            Test test = (Test)sqlSession.selectOne("com.denny.mybatis.mapper.TestMapper.selectByPrimaryKey", 1);//ibatis的用法
            System.out.println(test);
        }finally {
            sqlSession.close();
        }
    }
}
