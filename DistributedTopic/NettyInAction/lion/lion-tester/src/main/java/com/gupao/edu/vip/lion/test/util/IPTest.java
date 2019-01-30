
package com.gupao.edu.vip.lion.test.util;

import com.gupao.edu.vip.lion.tools.Utils;
import org.junit.Test;

/**
 */
public class IPTest {
    @Test
    public void getLocalIP() throws Exception {
        System.out.println(Utils.lookupLocalIp());
        System.out.println(Utils.lookupExtranetIp());

    }
}

