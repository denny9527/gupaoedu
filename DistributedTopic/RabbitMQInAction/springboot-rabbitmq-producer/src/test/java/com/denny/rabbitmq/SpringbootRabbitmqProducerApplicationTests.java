package com.denny.rabbitmq;

import com.denny.rabbitmq.producer.TestProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitmqProducerApplicationTests {

    @Autowired
    private TestProducer testProducer;

    @Test
    public void contextLoads() {
        testProducer.sendMsg();
    }

}

