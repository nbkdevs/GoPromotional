package uk.co.softrank.eventplanner.ui.events

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uk.co.softrank.eventplanner.R
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.ui.components.AppScaffold
import uk.co.softrank.eventplanner.ui.components.DeleteConfirmDialog
import uk.co.softrank.eventplanner.ui.components.DropdownField
import uk.co.softrank.eventplanner.ui.components.EmptyState
import uk.co.softrank.eventplanner.ui.components.FormTextField
import uk.co.softrank.eventplanner.ui.components.LabeledRow
import uk.co.softrank.eventplanner.ui.components.PrimaryFab
import uk.co.softrank.eventplanner.ui.components.SectionCard
import uk.co.softrank.eventplanner.util.AppConstants
import uk.co.softrank.eventplanner.util.Formatters
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun EventsListScreen(
    events: List<EventEntity>,
    selectedEventId: Long?,
    onSelect: (Long) -> Unit,
    onOpen: (Long) -> Unit,
    onCreate: () -> Unit
) {
    AppScaffold(
        title = "Events",
        floatingActionButton = {
            PrimaryFab(Icons.Filled.Add, "Add event", onCreate)
        }
    ) { padding ->
        if (events.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.empty_events_title),
                body = stringResource(R.string.empty_events_body),
                modifier = Modifier.padding(padding),
                actionLabel = "Create event",
                onAction = onCreate
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    SectionCard(
                        modifier = Modifier.clickable {
                            onSelect(event.id)
                            onOpen(event.id)
                        }
                    ) {
                        Text(event.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${event.type} · ${Formatters.formatDate(event.dateEpochDay)} · ${Formatters.formatTime(event.timeMinutes)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!event.venue.isBlank()) {
                            Text(
                                event.venue,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (event.id == selectedEventId) {
                            Text(
                                "Active event",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventDetailScreen(
    event: EventEntity?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSelectActive: () -> Unit
) {
    var showDelete by remember { mutableStateOf(false) }
    if (event == null) {
        AppScaffold(title = "Event", onBack = onBack) { padding ->
            EmptyState(
                title = "Event not found",
                body = "It may have been deleted.",
                modifier = Modifier.padding(padding),
                actionLabel = "Back",
                onAction = onBack
            )
        }
        return
    }

    AppScaffold(
        title = event.name,
        onBack = onBack,
        actions = {
            IconButton(onClick = onEdit) {
                Icon(Icons.Outlined.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { showDelete = true }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionCard {
                LabeledRow("Type", event.type)
                LabeledRow("Date", Formatters.formatDate(event.dateEpochDay))
                LabeledRow("Time", Formatters.formatTime(event.timeMinutes))
                LabeledRow("Venue", event.venue.ifBlank { "—" })
                LabeledRow(
                    "Attendees",
                    event.expectedAttendees?.toString() ?: "—"
                )
                LabeledRow("Budget", Formatters.formatMoney(event.budgetTotal))
            }
            if (event.notes.isNotBlank()) {
                SectionCard {
                    Text("Notes", style = MaterialTheme.typography.titleMedium)
                    Text(event.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Button(onClick = onSelectActive, modifier = Modifier.fillMaxWidth()) {
                Text("Use as active event")
            }
        }
    }

    if (showDelete) {
        DeleteConfirmDialog(
            title = "Delete event?",
            message = "This also removes related tasks, expenses, and merchandise for this event.",
            onConfirm = {
                showDelete = false
                onDelete()
            },
            onDismiss = { showDelete = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditScreen(
    existing: EventEntity?,
    onBack: () -> Unit,
    onSave: (EventEntity) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var type by remember { mutableStateOf(existing?.type ?: AppConstants.EVENT_TYPES.first()) }
    var venue by remember { mutableStateOf(existing?.venue.orEmpty()) }
    var attendees by remember {
        mutableStateOf(existing?.expectedAttendees?.toString().orEmpty())
    }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var budget by remember {
        mutableStateOf(
            existing?.budgetTotal?.takeIf { it > 0 }?.toString().orEmpty()
        )
    }
    var dateEpochDay by remember { mutableStateOf(existing?.dateEpochDay) }
    var timeMinutes by remember { mutableStateOf(existing?.timeMinutes) }
    var nameError by remember { mutableStateOf(false) }
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }

    LaunchedEffect(existing?.id) {
        if (existing != null) {
            name = existing.name
            type = existing.type
            venue = existing.venue
            attendees = existing.expectedAttendees?.toString().orEmpty()
            notes = existing.notes
            budget = existing.budgetTotal.takeIf { it > 0 }?.toString().orEmpty()
            dateEpochDay = existing.dateEpochDay
            timeMinutes = existing.timeMinutes
        }
    }

    AppScaffold(
        title = if (existing == null) "New event" else "Edit event",
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
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = "Event name",
                isError = nameError,
                supportingText = if (nameError) "Name is required" else null
            )
            DropdownField(
                label = "Event type",
                value = type,
                options = AppConstants.EVENT_TYPES,
                onSelected = { type = it }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { showDate = true }) {
                    Text(Formatters.formatDate(dateEpochDay))
                }
                TextButton(onClick = { showTime = true }) {
                    Text(Formatters.formatTime(timeMinutes))
                }
            }
            FormTextField(value = venue, onValueChange = { venue = it }, label = "Venue")
            FormTextField(
                value = attendees,
                onValueChange = { attendees = it },
                label = "Expected attendees",
                keyboardType = KeyboardType.Number
            )
            FormTextField(
                value = budget,
                onValueChange = { budget = it },
                label = "Total budget (£)",
                keyboardType = KeyboardType.Decimal
            )
            FormTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                singleLine = false,
                minLines = 3
            )
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val now = System.currentTimeMillis()
                    onSave(
                        EventEntity(
                            id = existing?.id ?: 0,
                            name = name.trim(),
                            type = type,
                            dateEpochDay = dateEpochDay,
                            timeMinutes = timeMinutes,
                            venue = venue.trim(),
                            expectedAttendees = Formatters.parseInt(attendees),
                            notes = notes.trim(),
                            budgetTotal = Formatters.parseMoney(budget) ?: 0.0,
                            createdAt = existing?.createdAt ?: now,
                            updatedAt = now
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save event")
            }
        }
    }

    if (showDate) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = dateEpochDay?.let {
                LocalDate.ofEpochDay(it).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        dateEpochDay = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC)
                            .toLocalDate().toEpochDay()
                    }
                    showDate = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDate = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showTime) {
        val state = rememberTimePickerState(
            initialHour = (timeMinutes ?: 540) / 60,
            initialMinute = (timeMinutes ?: 540) % 60,
            is24Hour = true
        )
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTime = false },
            confirmButton = {
                TextButton(onClick = {
                    timeMinutes = state.hour * 60 + state.minute
                    showTime = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTime = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = state) }
        )
    }
}
