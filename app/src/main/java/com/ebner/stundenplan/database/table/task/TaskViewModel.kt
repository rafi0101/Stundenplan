package com.ebner.stundenplan.database.table.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import com.ebner.stundenplan.database.table.mergedEntities.TaskLesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 02.07.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.task
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository: TaskRepository

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val taskDao = StundenplanDatabase.getInstance(application).taskDao()
        taskRepository = TaskRepository(taskDao)

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.delete(task)
    }

    fun allTask(yid: Int): LiveData<List<TaskLesson>> = taskRepository.getAllTask(yid)


}