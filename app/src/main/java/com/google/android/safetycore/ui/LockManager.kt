package com.google.android.safetycore.ui

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import android.util.Base64

object LockManager {
    private const val PREFS = "SafetyCorePrefs"
    private const val KEY_LOCK_ENABLED = "lock_enabled"
    private const val KEY_PIN_HASH = "pin_hash"

    fun isLockEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_LOCK_ENABLED, false)

    fun setLockEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_LOCK_ENABLED, enabled).apply()
    }

    fun setPIN(context: Context, pin: String) {
        val hash = pin.reversed() + "_sc_" + pin.hashCode().toString(16)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_PIN_HASH, hash).apply()
        setLockEnabled(context, true)
    }

    fun verifyPIN(context: Context, pin: String): Boolean {
        val stored = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_PIN_HASH, null) ?: return false
        val expected = pin.reversed() + "_sc_" + pin.hashCode().toString(16)
        return stored == expected
    }

    fun disableLock(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(KEY_PIN_HASH)
            .putBoolean(KEY_LOCK_ENABLED, false)
            .apply()
    }
}
