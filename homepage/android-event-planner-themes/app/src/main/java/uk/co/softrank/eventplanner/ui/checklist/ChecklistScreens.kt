package uk.co.softrank.eventplanner.ui.checklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.co.softrank.eventplanner.R
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.data.local.entity.TaskEntity
import uk.co.softrank.eventplanner.ui.components.AppScaffold
import uk.co.softrank.eventplanner.ui.components.DeleteConfirmDialog
import uk.co.softrank.eventplanner.ui.components.DropdownField
import uk.co.softrank.eventplanner.ui.components.EmptyState
import uk.co.softrank.eventplanner.ui.components.FormTextField
import uk.co.softrank.eventplanner.ui.components.PrimaryFab
import uk.co.softrank.eventplanner.ui.components.SectionCard
import uk.co.softrank.eventplanner.util.AppConstants
import uk.co.softrank.eventplanner.util.Formatters
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun ChecklistScreen(
    tasks: List<TaskEntity>,
    selectedEvent: EventEntity?,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onToggle: (TaskEntity) -> Unit,
    onDelete: (TaskEntity) -> Unit,
    onSeedDefaults: () -> Unit
) {
    var pendingDelete by remember { mutableStateOf<TaskEntity?>(null) }

    AppScaffold(
        title = "Checklist",
        floatingActionButton = {
            PrimaryFab(Icons.Filled.Add, "Add task", onAdd)
        },
        actions = {
            TextButton(onClick = onSeedDefaults) { Text("Starter") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = selectedEvent?.let { "Tasks for ${it.name}" }
                    ?: "All tasks (create an event to organise by event)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (tasks.isEmpty()) {
                EmptyState(
                    title = stringResource(R.string.empty_tasks_title),
                    body = stringResource(R.string.empty_tasks_body),
                    actionLabel = "Add starter checklist",
                    onAction = onSeedDefaults
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        SectionCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { onToggle(task) }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (task.isCompleted) {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    Text(
                                        "${task.category} · ${task.priority} · ${Formatters.formatDate(task.dueDateEpochDay)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = { onEdit(task.id) }) { Text("Edit") }
                                IconButton(onClick = { pendingDelete = task }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { task ->
        DeleteConfirmDialog(
            title = "Delete task?",
            message = "Remove “${task.title}” from your checklist?",
            onConfirm = {
                onDelete(task)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    existing: TaskEntity?,
    selectedEventId: Long?,
    onBack: () -> Unit,
    onSave: (TaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title.orEmpty()) }
    var category by remember {
        mutableStateOf(existing?.category ?: AppConstants.TASK_CATEGORIES.first())
    }
    var priority by remember {
        mutableStateOf(existing?.priority ?: "Medium")
    }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var dueDate by remember { mutableStateOf(existing?.dueDateEpochDay) }
    var completed by remember { mutableStateOf(existing?.isCompleted ?: false) }
    var titleError by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(false) }

    LaunchedEffect(existing?.id) {
        if (existing != null) {
            title = existing.title
            category = existing.category
            priority = existing.priority
            notes = existing.notes
            dueDate = existing.dueDateEpochDay
            completed = existing.isCompleted
        }
    }

    AppScaffold(
        title = if (existing == null) "New task" else "Edit task",
        onBack = onBack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = "Task",
                isError = titleError,
                supportingText = if (titleError) "Task title is required" else null
            )
            DropdownField(
                label = "Category",
                value = category,
                options = AppConstants.TASK_CATEGORIES,
                onSelected = { category = it }
            )
            DropdownField(
                label = "Priority",
                value = priority,
                options = AppConstants.TASK_PRIORITIES,
                onSelected = { priority = it }
            )
            TextButton(onClick = { showDate = true }) {
                Text("Due: ${Formatters.formatDate(dueDate)}")
            }
            FormTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                singleLine = false,
                minLines = 3
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = completed, onCheckedChange = { completed = it })
                Text("Completed")
            }
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }
                    val now = System.currentTimeMillis()
                    onSave(
                        TaskEntity(
                            id = existing?.id ?: 0,
                            eventId = existing?.eventId ?: selectedEventId,
                            title = title.trim(),
                            category = category,
                            priority = priority,
                            dueDateEpochDay = dueDate,
                            notes = notes.trim(),
                            isCompleted = completed,
                            createdAt = existing?.createdAt ?: now,
                            updatedAt = now
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save task")
            }
        }
    }

    if (showDate) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = dueDate?.let {
                LocalDate.ofEpochDay(it).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        dueDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC)
                            .toLocalDate().toEpochDay()
                    }
                    showDate = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    dueDate = null
                    showDate = false
                }) { Text("Clear") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}
