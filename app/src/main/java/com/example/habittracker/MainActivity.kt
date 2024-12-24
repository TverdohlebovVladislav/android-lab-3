package com.example.habittracker

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), HabitStatusListener {
    private lateinit var adapter: HabitAdapter
    private val habits = mutableListOf<Habit>()

    override fun saveHabitStatus(habitId: Int, isCompleted: Boolean) {
        val sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("habit_$habitId", isCompleted)
        editor.apply()
    }

    override fun loadHabitStatus(habitId: Int): Boolean {
        val sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("habit_$habitId", false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = HabitAdapter(habits, this) { habit ->
            // Обработка кликов на CheckBox
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchDataFromServer()
        startAutoRefresh() // Запускаем автообновление
    }

    private fun fetchDataFromServer() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = NetworkService.fetchHabits("https://v78qr.wiremockapi.cloud/habits")
                if (response != null) {
                    parseHabits(response)
                } else {
                    showError("Ошибка: пустой ответ")
                }
            } catch (e: Exception) {
                showError("Ошибка: ${e.message}")
            }
        }
    }

    private fun startAutoRefresh() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                fetchDataFromServer() // Загружаем новые данные
                Snackbar.make(
                    findViewById(R.id.recyclerView),
                    "Данные обновлены",
                    Snackbar.LENGTH_SHORT
                ).show()
                delay(60000) // Задержка 60 секунд
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(findViewById(R.id.recyclerView), message, Snackbar.LENGTH_LONG).show()
        Log.e("MainActivity", message)
    }


    private fun parseHabits(json: String) {
        val habitsObject = JSONObject(json)
        val habitsArray = habitsObject.getJSONArray("habits")
        val sharedPreferences = getSharedPreferences("HabitTrackerPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        habits.clear()
        for (i in 0 until habitsArray.length()) {
            val habitObject = habitsArray.getJSONObject(i)
            val id = habitObject.getInt("id")

            // Проверяем, есть ли статус в SharedPreferences
            val isCompleted = if (sharedPreferences.contains("habit_$id")) {
                sharedPreferences.getBoolean("habit_$id", false) // Берём локальный статус
            } else {
                val apiStatus = habitObject.getBoolean("completed") // Берём из API
                editor.putBoolean("habit_$id", apiStatus) // Сохраняем в SharedPreferences
                apiStatus
            }

            // Создаем объект привычки
            val habit = Habit(
                id = id,
                name = habitObject.getString("name"),
                goal = habitObject.getString("goal"),
                completed = isCompleted, // Используем локальный статус
                started = habitObject.getBoolean("started"),
                startedAt = habitObject.optString("started_at", null),
                completedAt = habitObject.optString("completed_at", null)
            )

            // Обновляем SharedPreferences
            editor.putBoolean("habit_$id", habit.completed)
            habits.add(habit)
        }

        editor.apply() // Сохраняем все изменения
        adapter.notifyDataSetChanged() // Обновляем UI
    }


}



