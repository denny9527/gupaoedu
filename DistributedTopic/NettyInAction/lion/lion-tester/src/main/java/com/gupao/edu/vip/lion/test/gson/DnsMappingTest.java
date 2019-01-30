
package com.gupao.edu.vip.lion.test.gson;

import com.gupao.edu.vip.lion.api.spi.net.DnsMapping;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class DnsMappingTest {

    @Test
    public void test() throws MalformedURLException {
        String url = "http://baidu.com:9001/xxx/xxx?s=nc=1";
        DnsMapping mapping = new DnsMapping("127.0.0.1", 8080);
        String s = mapping.translate(new URL(url));
        System.out.println(url);
        System.out.println(s);

    }

}
