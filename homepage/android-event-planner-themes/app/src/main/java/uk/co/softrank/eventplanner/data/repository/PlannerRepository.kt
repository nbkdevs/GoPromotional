package uk.co.softrank.eventplanner.data.repository

import kotlinx.coroutines.flow.Flow
import uk.co.softrank.eventplanner.data.local.AppDatabase
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.data.local.entity.ExpenseEntity
import uk.co.softrank.eventplanner.data.local.entity.MerchandiseEntity
import uk.co.softrank.eventplanner.data.local.entity.TaskEntity
import uk.co.softrank.eventplanner.data.local.entity.ThemeFavoriteEntity
import uk.co.softrank.eventplanner.util.AppConstants

class PlannerRepository(private val db: AppDatabase) {
    private val events = db.eventDao()
    private val tasks = db.taskDao()
    private val expenses = db.expenseDao()
    private val merchandise = db.merchandiseDao()
    private val favorites = db.themeFavoriteDao()

    fun observeEvents(): Flow<List<EventEntity>> = events.observeAll()
    fun observeEvent(id: Long): Flow<EventEntity?> = events.observeById(id)
    suspend fun getEvent(id: Long): EventEntity? = events.getById(id)

    suspend fun saveEvent(event: EventEntity): Long {
        return if (event.id == 0L) {
            events.insert(event)
        } else {
            events.update(event)
            event.id
        }
    }

    suspend fun deleteEvent(event: EventEntity) = events.delete(event)

    fun observeTasks(eventId: Long?): Flow<List<TaskEntity>> = tasks.observeForEvent(eventId)
    fun observeAllTasks(): Flow<List<TaskEntity>> = tasks.observeAll()
    fun observeOpenTaskCount(): Flow<Int> = tasks.observeOpenCount()
    suspend fun getTask(id: Long): TaskEntity? = tasks.getById(id)

    suspend fun saveTask(task: TaskEntity): Long {
        return if (task.id == 0L) {
            tasks.insert(task)
        } else {
            tasks.update(task)
            task.id
        }
    }

    suspend fun deleteTask(task: TaskEntity) = tasks.delete(task)

    suspend fun seedDefaultTasks(eventId: Long?) {
        val now = System.currentTimeMillis()
        val seed = AppConstants.DEFAULT_TASKS.map { item ->
            TaskEntity(
                eventId = eventId,
                title = item.title,
                category = item.category,
                priority = item.priority,
                dueDateEpochDay = null,
                notes = "",
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            )
        }
        tasks.insertAll(seed)
    }

    fun observeExpenses(eventId: Long): Flow<List<ExpenseEntity>> = expenses.observeForEvent(eventId)
    suspend fun getExpense(id: Long): ExpenseEntity? = expenses.getById(id)

    suspend fun saveExpense(expense: ExpenseEntity): Long {
        return if (expense.id == 0L) {
            expenses.insert(expense)
        } else {
            expenses.update(expense)
            expense.id
        }
    }

    suspend fun deleteExpense(expense: ExpenseEntity) = expenses.delete(expense)

    fun observeMerchandise(eventId: Long): Flow<List<MerchandiseEntity>> =
        merchandise.observeForEvent(eventId)

    suspend fun getMerchandise(id: Long): MerchandiseEntity? = merchandise.getById(id)

    suspend fun saveMerchandise(item: MerchandiseEntity): Long {
        return if (item.id == 0L) {
            merchandise.insert(item)
        } else {
            merchandise.update(item)
            item.id
        }
    }

    suspend fun deleteMerchandise(item: MerchandiseEntity) = merchandise.delete(item)

    fun observeFavoriteThemeIds(): Flow<List<String>> = favorites.observeFavoriteIds()

    suspend fun toggleThemeFavorite(themeId: String) {
        if (favorites.isFavorite(themeId)) {
            favorites.delete(themeId)
        } else {
            favorites.insert(ThemeFavoriteEntity(themeId, System.currentTimeMillis()))
        }
    }
}
