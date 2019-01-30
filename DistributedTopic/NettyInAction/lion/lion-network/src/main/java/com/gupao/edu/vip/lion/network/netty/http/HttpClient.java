
package com.gupao.edu.vip.lion.network.netty.http;

import com.gupao.edu.vip.lion.api.service.Service;


public interface HttpClient extends Service {
    void request(RequestContext context) throws Exception;
}
