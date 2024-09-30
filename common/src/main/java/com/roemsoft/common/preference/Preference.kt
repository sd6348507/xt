package com.roemsoft.common.preference

import android.content.Context
import android.content.SharedPreferences
import com.roemsoft.common.BaseApp
import java.io.*
import kotlin.reflect.KProperty

class Preference<T>(val name: String, private val default: T) {

    companion object {
        const val LOGIN_KEY = "login"
        const val USERNAME_KEY = "username"
        const val PASSWORD_KEY = "password"
        const val TOKEN_KEY = "token"
        const val HAS_NETWORK_KEY = "has_network"

        const val BLUETOOTH_MAC = "bluetooth_mac_address"

        const val BLUETOOTH_NAME = "bluetooth_name"

        const val URL_KEY = "url"

        private const val FILE_NAME = "ssd_wj_file"

        private val prefs: SharedPreferences by lazy {
            BaseApp.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        }

        fun clearPreference() {
            prefs.edit().clear().apply()
        }

        fun clearPreference(key: String) {
            prefs.edit().remove(key).apply()
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSharedPreferences(name, default)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSharedPreferences(name, value)
    }

    private fun putSharedPreferences(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun getSharedPreferences(name: String, default: T): T = with(prefs) {
        val result = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(getString(name, serialize(default)) ?: "")
        }
        return result as T
    }



    /**
     * 序列化对象

     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象

     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }
}