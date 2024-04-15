package com.example.hce

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class AES {
    fun aesEncrypt(data: ByteArray, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = generateSecureIV()
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        val encryptedData = cipher.doFinal(data)
        return Pair(encryptedData, iv)
    }
    private fun generateSecureIV(): ByteArray {
        val iv = ByteArray(16)
        val secureRandom = SecureRandom.getInstance("SHA1PRNG")
        secureRandom.nextBytes(iv)
        return iv
    }


    fun aesDecrypt(encryptedData: ByteArray, secretKey: SecretKey, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(encryptedData)
    }
}
