package com.example.hce

import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.crypto.KeyGenerator

object KeyGeneratorHelper {
    private const val AES_KEY_SIZE = 256 // Change this to the desired key size

    fun generateRandomKey(): ByteArray {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(AES_KEY_SIZE, SecureRandom())
        val secretKey: SecretKey = keyGenerator.generateKey()
        return secretKey.encoded
    }
}
