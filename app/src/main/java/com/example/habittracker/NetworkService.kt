package com.example.habittracker

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object NetworkService {
    private val client = OkHttpClient()

    suspend fun fetchHabits(url: String): String? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
            } else {
                Log.e("NetworkService", "Ошибка: ${response.code} ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.e("NetworkService", "Ошибка запроса: ${e.message}")
            null
        }
    }

    suspend fun updateHabit(url: String, habit: Habit): Boolean = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("id", habit.id)
            put("name", habit.name)
            put("goal", habit.goal)
            put("completed", habit.completed)
            put("started", habit.started)
            put("started_at", habit.startedAt)
            if (habit.completedAt != null) {
                put("completed_at", habit.completedAt)
            } else {
                put("completed_at", JSONObject.NULL)
            }
        }

        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$url/${habit.id}")
            .put(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            Log.d("NetworkService", "Запрос отправлен: $json")
            Log.d("NetworkService", "Ответ сервера: ${response.code} ${response.message}")
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("NetworkService", "Ошибка запроса: ${e.message}")
            false
        }
    }


}
