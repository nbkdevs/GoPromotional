package uk.co.softrank.eventplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.co.softrank.eventplanner.data.PreferencesStore
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.data.local.entity.ExpenseEntity
import uk.co.softrank.eventplanner.data.local.entity.MerchandiseEntity
import uk.co.softrank.eventplanner.data.local.entity.TaskEntity
import uk.co.softrank.eventplanner.data.repository.PlannerRepository
import uk.co.softrank.eventplanner.data.seed.EventTheme
import uk.co.softrank.eventplanner.data.seed.EventThemeLibrary

data class BudgetSummary(
    val totalBudget: Double,
    val estimated: Double,
    val actual: Double,
    val remaining: Double
)

data class HomeSummary(
    val eventCount: Int,
    val openTasks: Int,
    val selectedEvent: EventEntity?,
    val upcomingEvents: List<EventEntity>
)

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(
    private val repository: PlannerRepository,
    private val preferences: PreferencesStore
) : ViewModel() {

    val onboardingDone: StateFlow<Boolean?> = preferences.onboardingDone
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val events: StateFlow<List<EventEntity>> = repository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedEventId: StateFlow<Long?> = preferences.selectedEventId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val selectedEvent: StateFlow<EventEntity?> = combine(events, selectedEventId) { list, id ->
        when {
            id != null -> list.find { it.id == id } ?: list.firstOrNull()
            else -> list.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val homeSummary: StateFlow<HomeSummary> = combine(
        events,
        selectedEvent,
        repository.observeOpenTaskCount()
    ) { eventList, selected, openTasks ->
        HomeSummary(
            eventCount = eventList.size,
            openTasks = openTasks,
            selectedEvent = selected,
            upcomingEvents = eventList.take(3)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeSummary(0, 0, null, emptyList())
    )

    val tasks: StateFlow<List<TaskEntity>> = selectedEvent
        .flatMapLatest { event ->
            if (event == null) repository.observeAllTasks()
            else repository.observeTasks(event.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val expenses: StateFlow<List<ExpenseEntity>> = selectedEvent
        .flatMapLatest { event ->
            if (event == null) flowOf(emptyList())
            else repository.observeExpenses(event.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val merchandise: StateFlow<List<MerchandiseEntity>> = selectedEvent
        .flatMapLatest { event ->
            if (event == null) flowOf(emptyList())
            else repository.observeMerchandise(event.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val budgetSummary: StateFlow<BudgetSummary> = combine(selectedEvent, expenses) { event, list ->
        val estimated = list.sumOf { it.estimatedAmount }
        val actual = list.sumOf { it.actualAmount ?: 0.0 }
        val total = event?.budgetTotal ?: 0.0
        BudgetSummary(
            totalBudget = total,
            estimated = estimated,
            actual = actual,
            remaining = total - actual
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        BudgetSummary(0.0, 0.0, 0.0, 0.0)
    )

    val favoriteThemeIds: StateFlow<Set<String>> = repository.observeFavoriteThemeIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val themes: List<EventTheme> = EventThemeLibrary.themes

    fun completeOnboarding() = viewModelScope.launch {
        preferences.setOnboardingDone(true)
    }

    fun selectEvent(id: Long?) = viewModelScope.launch {
        preferences.setSelectedEventId(id)
    }

    suspend fun getEvent(id: Long) = repository.getEvent(id)
    suspend fun getTask(id: Long) = repository.getTask(id)
    suspend fun getExpense(id: Long) = repository.getExpense(id)
    suspend fun getMerchandise(id: Long) = repository.getMerchandise(id)

    fun saveEvent(event: EventEntity, onSaved: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repository.saveEvent(event)
        if (selectedEventId.value == null) {
            preferences.setSelectedEventId(id)
        }
        onSaved(id)
    }

    fun deleteEvent(event: EventEntity, onDone: () -> Unit = {}) = viewModelScope.launch {
        repository.deleteEvent(event)
        if (selectedEventId.value == event.id) {
            preferences.setSelectedEventId(null)
        }
        onDone()
    }

    fun saveTask(task: TaskEntity, onSaved: () -> Unit = {}) = viewModelScope.launch {
        repository.saveTask(task)
        onSaved()
    }

    fun deleteTask(task: TaskEntity, onDone: () -> Unit = {}) = viewModelScope.launch {
        repository.deleteTask(task)
        onDone()
    }

    fun toggleTaskComplete(task: TaskEntity) = viewModelScope.launch {
        repository.saveTask(
            task.copy(
                isCompleted = !task.isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun seedDefaultTasks() = viewModelScope.launch {
        repository.seedDefaultTasks(selectedEvent.value?.id)
    }

    fun saveExpense(expense: ExpenseEntity, onSaved: () -> Unit = {}) = viewModelScope.launch {
        repository.saveExpense(expense)
        onSaved()
    }

    fun deleteExpense(expense: ExpenseEntity, onDone: () -> Unit = {}) = viewModelScope.launch {
        repository.deleteExpense(expense)
        onDone()
    }

    fun saveMerchandise(item: MerchandiseEntity, onSaved: () -> Unit = {}) = viewModelScope.launch {
        repository.saveMerchandise(item)
        onSaved()
    }

    fun deleteMerchandise(item: MerchandiseEntity, onDone: () -> Unit = {}) = viewModelScope.launch {
        repository.deleteMerchandise(item)
        onDone()
    }

    fun toggleFavorite(themeId: String) = viewModelScope.launch {
        repository.toggleThemeFavorite(themeId)
    }

    fun themeById(id: String): EventTheme? = EventThemeLibrary.byId(id)

    companion object {
        fun factory(repository: PlannerRepository, preferences: PreferencesStore) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppViewModel(repository, preferences) as T
                }
            }
    }
}
