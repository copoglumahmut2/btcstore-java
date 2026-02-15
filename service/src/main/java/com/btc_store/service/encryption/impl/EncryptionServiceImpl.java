package com.btc_store.service.encryption.impl;

import com.btc_store.service.encryption.EncryptionService;
import com.btc_store.service.exception.StoreRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EncryptionServiceImpl implements EncryptionService {
    @Value("${aes.encrypt.key}")
    protected String key;
    @Value("${aes.encrypt.initVector}")
    protected String initVector;
    @Value("${aes.encrypt.algo}")
    protected String algo;
    @Override
    public String encrypt(Object data) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            String dataString = data.toString();
            byte[] encrypted = cipher.doFinal(dataString.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            throw new StoreRuntimeException("Data couldn't not be encrypted");
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encryptedText));
            return new String(original);
        } catch (Exception ex) {
            throw new StoreRuntimeException("Encrypted text is invalid.");
        }
    }
}
