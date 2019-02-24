package com.denny.mybatis.demo;

import com.denny.mybatis.mapper.TestMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MyBatiscXmlConfigDemo {

    public static void main(String[] args) throws FileNotFoundException {
        String userDir = System.getProperty("user.dir");
        FileInputStream inputStream = new FileInputStream(userDir + "/src/main/resources/mybatis-config.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sessionFactory.openSession();
        try {
            System.out.println(sqlSession.getMapper(TestMapper.class).selectByPrimaryKey(1));

            //TestMapper testMapper = sqlSession.getMapper(TestMapper.class);//动态代理生成代理对象$Proxy->MapperProxy
           // System.out.println(testMapper.selectByPrimaryKey(1));
        }finally {
            sqlSession.close();
        }
    }
}
