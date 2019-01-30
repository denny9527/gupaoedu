
package com.gupao.edu.vip.lion.api.router;

import com.gupao.edu.vip.lion.api.spi.router.ClientClassifierFactory;


public interface ClientClassifier {
    ClientClassifier I = ClientClassifierFactory.create();

    int getClientType(String osName);
}
