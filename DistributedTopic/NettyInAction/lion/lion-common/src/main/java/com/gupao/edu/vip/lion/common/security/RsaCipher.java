
package com.gupao.edu.vip.lion.common.security;

import com.gupao.edu.vip.lion.api.connection.Cipher;
import com.gupao.edu.vip.lion.tools.crypto.RSAUtils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 */
public final class RsaCipher implements Cipher {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public RsaCipher(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return RSAUtils.decryptByPrivateKey(data, privateKey);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return RSAUtils.encryptByPublicKey(data, publicKey);
    }

    @Override
    public String toString() {
        return "RsaCipher [privateKey=" + new String(privateKey.getEncoded()) + ", publicKey=" + new String(publicKey.getEncoded()) + "]";
    }

    public static RsaCipher create() {
        return new RsaCipher(CipherBox.I.getPrivateKey(), CipherBox.I.getPublicKey());
    }
}
