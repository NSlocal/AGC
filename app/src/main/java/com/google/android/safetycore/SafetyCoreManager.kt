package com.google.android.safetycore

import android.content.Context
import android.content.Intent
import android.os.RemoteException

class SafetyCoreManager(private val context: Context) {
    private var service: ISafetyCoreService? = null

    fun bindService(): Boolean {
        val intent = Intent("com.google.android.safetycore.ISafetyCoreService")
        intent.setPackage("com.google.android.safetycore")
        return context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private val connection = object : android.content.ServiceConnection {
        override fun onServiceConnected(name: android.content.ComponentName?, s: android.os.IBinder?) {
            service = ISafetyCoreService.Stub.asInterface(s)
        }
        override fun onServiceDisconnected(name: android.content.ComponentName?) {
            service = null
        }
    }

    fun setGlobalEnabled(enabled: Boolean): Boolean = try {
        SafetyCoreService.setEnabled(context, enabled); true
    } catch (e: Exception) { false }

    fun setFeatureState(featureId: String, enabled: Boolean): Boolean = try {
        service?.setFeatureEnabled(featureId, enabled); true
    } catch (e: RemoteException) { false }

    fun unbind() { runCatching { context.unbindService(connection) } }
}
