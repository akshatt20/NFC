package com.example.hce

import android.content.Context
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hce.EncryptionHelper.aesEncrypt

import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class  HostCardEmulatorService: HostApduService() {
    companion object {
        val TAG = "Host Card Emulator"
        val STATUS_SUCCESS = "9000"
        val STATUS_FAILED = "6F00"
        val CLA_NOT_SUPPORTED = "6E00"
        val INS_NOT_SUPPORTED = "6D00"
        val AID = "A0000002471001"
        val SELECT_INS = "A4"
        val DEFAULT_CLA = "00"
        val MIN_APDU_LENGTH = 12
    }
    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "Deactivated: $reason")
        Toast.makeText(this, "HCE Deactivated", Toast.LENGTH_SHORT).show()
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null || commandApdu.size < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = Utils.toHex(commandApdu)

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) {
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED)
        }

        if (hexCommandApdu.substring(10, 24) == AID) {
            // Response data to be encrypted
            val sharedPref = applicationContext.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
            val uid = sharedPref.getString("uid", null)
            val employeeId = sharedPref.getString("employee_id", "-1")
            val key = sharedPref.getString("key", "0")

            // Ensure key, uid, and employeeId are not null
            if (key.isNullOrEmpty() || uid.isNullOrEmpty() || employeeId.isNullOrEmpty()) {
                return Utils.hexStringToByteArray(STATUS_FAILED)
            }

            // Encrypt the response data with a secure IV
            val keyBytes = key.toByteArray(StandardCharsets.UTF_8)
            val secretKey = SecretKeySpec(keyBytes, "AES")
            val iv = generateSecureIV() // Generate a secure IV
            val ivSpec = IvParameterSpec(iv)
            val encryptedEmployeeId = aesEncrypt(employeeId.toByteArray(), secretKey)

            val editor = sharedPref.edit()
            editor.putString("encrypted_employee_id", Utils.toHex(encryptedEmployeeId))
            editor.putString("iv", Utils.toHex(iv))
            editor.apply()

            // Construct the response APDU
            val responseData = "$uid,${Utils.toHex(encryptedEmployeeId)},${Utils.toHex(iv)}".toByteArray()
            return Utils.hexStringToByteArray(Utils.toHex(responseData) + STATUS_SUCCESS)
        }

        return Utils.hexStringToByteArray(STATUS_FAILED)
    }


    private fun generateSecureIV(): ByteArray {
        val iv = ByteArray(16)
        val secureRandom = SecureRandom.getInstance("SHA1PRNG")
        secureRandom.nextBytes(iv)
        return iv
    }


}