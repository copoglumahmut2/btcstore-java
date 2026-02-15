package com.btc_store.service.encryption;

public interface EncryptionService {
    String encrypt(Object data);
    String decrypt(String encryptedText);
}
