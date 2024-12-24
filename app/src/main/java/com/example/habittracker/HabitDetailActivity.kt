package com.example.habittracker

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HabitDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        val habitName = findViewById<TextView>(R.id.habitName)
        val habitGoal = findViewById<TextView>(R.id.habitGoal)
        val habitStartTime = findViewById<TextView>(R.id.habitStartTime)
        val habitEndTime = findViewById<TextView>(R.id.habitEndTime)
        val backButton = findViewById<Button>(R.id.backButton)

        // Получаем данные, переданные через Intent
        val name = intent.getStringExtra("habit_name")
        val goal = intent.getStringExtra("habit_goal")
        val startTime = intent.getStringExtra("habit_start_time")
        val endTime = intent.getStringExtra("habit_end_time")

        // Устанавливаем значения
        habitName.text = "Название: $name"
        habitGoal.text = "Цель: $goal"
        habitStartTime.text = "Начало: ${startTime ?: "Не начато"}"
        habitEndTime.text = "Завершено: ${endTime ?: "Не завершено"}"

        backButton.setOnClickListener {
            finish() // Возвращаемся на главный экран
        }
    }
}
