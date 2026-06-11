package com.kiriplatform.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream

object CacheManager {
    private val gson = Gson()

    suspend fun <T> saveCache(context: Context, key: String, data: T) = withContext(Dispatchers.IO) {
        try {
            val json = gson.toJson(data)
            val file = File(context.filesDir, "$key.json")
            // Use buffered writer for better performance on large strings
            file.bufferedWriter().use { out ->
                out.write(json)
            }
        } catch (e: Exception) {
            android.util.Log.e("CacheManager", "Error saving cache: $key", e)
        }
    }

    suspend fun <T> getCache(context: Context, key: String, typeToken: TypeToken<T>): T? = withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, "$key.json")
            if (file.exists()) {
                file.bufferedReader().use { reader ->
                    gson.fromJson(reader, typeToken.type)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("CacheManager", "Error reading cache: $key", e)
            null
        }
    }

    suspend fun clearCache(context: Context) = withContext(Dispatchers.IO) {
        context.filesDir.listFiles()?.forEach { 
            if (it.name.endsWith(".json")) it.delete()
        }
    }
}
