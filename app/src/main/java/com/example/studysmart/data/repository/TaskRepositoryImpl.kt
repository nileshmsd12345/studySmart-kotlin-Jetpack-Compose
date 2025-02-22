package com.example.studysmart.data.repository

import com.example.studysmart.data.local.TaskDao
import com.example.studysmart.domain.model.Task
import com.example.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }


    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    override fun getTasksBySubjectId(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksBySubjectId(subjectId)
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { tasks ->
            tasks.filter { task ->
                task.isComplete.not()
            }
        }.map { tasks -> sortTask(tasks) }
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksBySubjectId(subjectId).map { tasks ->
            tasks.filter { task -> task.isComplete.not() }
        }.map { tasks -> sortTask(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksBySubjectId(subjectId).map { tasks ->
            tasks.filter { task -> task.isComplete }
        }.map { tasks -> sortTask(tasks) }
    }

    private fun sortTask(tasks: List<Task>): List<Task> {
        return tasks.sortedWith(
            comparator = compareBy<Task> {
                it.dueDate
            }.thenByDescending {
                it.priority
            }
        )
    }
}