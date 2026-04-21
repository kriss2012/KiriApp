package com.apex.asg.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object CacheManager {
    private val gson = Gson()

    fun <T> saveCache(context: Context, key: String, data: T) {
        try {
            val json = gson.toJson(data)
            val file = File(context.filesDir, "$key.json")
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <T> getCache(context: Context, key: String, typeToken: TypeToken<T>): T? {
        return try {
            val file = File(context.filesDir, "$key.json")
            if (file.exists()) {
                val json = file.readText()
                gson.fromJson(json, typeToken.type)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearCache(context: Context) {
        context.filesDir.listFiles()?.forEach { 
            if (it.name.endsWith(".json")) it.delete()
        }
    }
}
