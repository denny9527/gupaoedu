

package com.gupao.edu.vip.lion.api.connection;


public interface Cipher {

    byte[] decrypt(byte[] data);

    byte[] encrypt(byte[] data);

}
