package com.example.habittracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


interface HabitStatusListener {
    fun saveHabitStatus(habitId: Int, isCompleted: Boolean)
    fun loadHabitStatus(habitId: Int): Boolean
}


class HabitAdapter(
    private val habits: List<Habit>,
    private val habitStatusListener: HabitStatusListener,
    private val onCheckBoxClicked: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.habitName)
        val goal: TextView = view.findViewById(R.id.habitGoal)
        val checkBox: CheckBox = view.findViewById(R.id.habitCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    // Попытка обновлять по API через PUT
//    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
//        val habit = habits[position]
//        holder.name.text = habit.name
//        holder.goal.text = "Цель: ${habit.goal}"
//        holder.checkBox.isChecked = habit.completed
//
//        holder.checkBox.setOnClickListener {
//            habit.completed = holder.checkBox.isChecked
//
//            CoroutineScope(Dispatchers.IO).launch {
//                val isUpdated = NetworkService.updateHabit("https://v78qr.wiremockapi.cloud/habits/${habit.id}", habit)
//                withContext(Dispatchers.Main) {
//                    if (isUpdated) {
//                        Snackbar.make(holder.itemView, "Привычка обновлена на сервере", Snackbar.LENGTH_SHORT).show()
//                    } else {
//                        Snackbar.make(holder.itemView, "Ошибка обновления", Snackbar.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }


    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.name.text = habit.name
        holder.goal.text = "Цель: ${habit.goal}"
        holder.checkBox.isChecked = habitStatusListener.loadHabitStatus(habit.id)

        holder.checkBox.setOnClickListener {
            val isChecked = holder.checkBox.isChecked
            habit.completed = isChecked
            habitStatusListener.saveHabitStatus(habit.id, isChecked)
            onCheckBoxClicked(habit)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, HabitDetailActivity::class.java).apply {
                putExtra("habit_name", habit.name)
                putExtra("habit_goal", habit.goal)
                putExtra("habit_start_time", habit.startedAt)
                putExtra("habit_end_time", habit.completedAt)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = habits.size
}

