package com.example.util

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtil {
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val input = "$salt:$password"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val calculatedHash = hashPassword(password, salt)
        return calculatedHash.equals(expectedHash, ignoreCase = true)
    }

    fun isValidFreeFireUid(uid: String): Boolean {
        val trimmed = uid.trim()
        return trimmed.length in 7..12 && trimmed.all { it.isDigit() }
    }
}
