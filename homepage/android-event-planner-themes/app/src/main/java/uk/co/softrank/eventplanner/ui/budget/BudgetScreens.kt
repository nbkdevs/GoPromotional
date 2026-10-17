package uk.co.softrank.eventplanner.ui.budget

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uk.co.softrank.eventplanner.R
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.data.local.entity.ExpenseEntity
import uk.co.softrank.eventplanner.ui.components.AppScaffold
import uk.co.softrank.eventplanner.ui.components.DeleteConfirmDialog
import uk.co.softrank.eventplanner.ui.components.DropdownField
import uk.co.softrank.eventplanner.ui.components.EmptyState
import uk.co.softrank.eventplanner.ui.components.FormTextField
import uk.co.softrank.eventplanner.ui.components.PrimaryFab
import uk.co.softrank.eventplanner.ui.components.SectionCard
import uk.co.softrank.eventplanner.ui.components.StatChip
import uk.co.softrank.eventplanner.util.AppConstants
import uk.co.softrank.eventplanner.util.Formatters
import uk.co.softrank.eventplanner.viewmodel.BudgetSummary

@Composable
fun BudgetScreen(
    selectedEvent: EventEntity?,
    summary: BudgetSummary,
    expenses: List<ExpenseEntity>,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (ExpenseEntity) -> Unit,
    onCreateEvent: () -> Unit
) {
    var pendingDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    AppScaffold(
        title = "Budget",
        floatingActionButton = {
            if (selectedEvent != null) {
                PrimaryFab(Icons.Filled.Add, "Add expense", onAdd)
            }
        }
    ) { padding ->
        if (selectedEvent == null) {
            EmptyState(
                title = "Select an event first",
                body = "Budgets are tracked per event. Create or open an event, then add expenses.",
                modifier = Modifier.padding(padding),
                actionLabel = "Create event",
                onAction = onCreateEvent
            )
            return@AppScaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SectionCard(modifier = Modifier.padding(16.dp)) {
                Text(selectedEvent.name, style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatChip("Budget", Formatters.formatMoney(summary.totalBudget))
                    StatChip("Estimated", Formatters.formatMoney(summary.estimated))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatChip("Actual", Formatters.formatMoney(summary.actual))
                    StatChip("Remaining", Formatters.formatMoney(summary.remaining))
                }
            }

            if (expenses.isEmpty()) {
                EmptyState(
                    title = stringResource(R.string.empty_expenses_title),
                    body = stringResource(R.string.empty_expenses_body),
                    actionLabel = "Add expense",
                    onAction = onAdd
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        SectionCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(expense.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        expense.category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Est. ${Formatters.formatMoney(expense.estimatedAmount)} · Act. ${
                                            expense.actualAmount?.let { Formatters.formatMoney(it) } ?: "—"
                                        }",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                TextButton(onClick = { onEdit(expense.id) }) { Text("Edit") }
                                IconButton(onClick = { pendingDelete = expense }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { expense ->
        DeleteConfirmDialog(
            title = "Delete expense?",
            message = "Remove “${expense.name}” from this budget?",
            onConfirm = {
                onDelete(expense)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
fun ExpenseEditScreen(
    existing: ExpenseEntity?,
    eventId: Long?,
    onBack: () -> Unit,
    onSave: (ExpenseEntity) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var category by remember {
        mutableStateOf(existing?.category ?: AppConstants.EXPENSE_CATEGORIES.first())
    }
    var estimated by remember {
        mutableStateOf(existing?.estimatedAmount?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var actual by remember {
        mutableStateOf(existing?.actualAmount?.toString().orEmpty())
    }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var nameError by remember { mutableStateOf(false) }
    var eventMissing by remember { mutableStateOf(false) }

    LaunchedEffect(existing?.id) {
        if (existing != null) {
            name = existing.name
            category = existing.category
            estimated = existing.estimatedAmount.takeIf { it > 0 }?.toString().orEmpty()
            actual = existing.actualAmount?.toString().orEmpty()
            notes = existing.notes
        }
    }

    AppScaffold(
        title = if (existing == null) "New expense" else "Edit expense",
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
            if (eventMissing) {
                Text(
                    "Select an active event before saving an expense.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            FormTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = "Expense name",
                isError = nameError,
                supportingText = if (nameError) "Name is required" else null
            )
            DropdownField(
                label = "Category",
                value = category,
                options = AppConstants.EXPENSE_CATEGORIES,
                onSelected = { category = it }
            )
            FormTextField(
                value = estimated,
                onValueChange = { estimated = it },
                label = "Estimated amount (£)",
                keyboardType = KeyboardType.Decimal
            )
            FormTextField(
                value = actual,
                onValueChange = { actual = it },
                label = "Actual amount (£)",
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
                    val targetEventId = existing?.eventId ?: eventId
                    if (targetEventId == null) {
                        eventMissing = true
                        return@Button
                    }
                    val now = System.currentTimeMillis()
                    onSave(
                        ExpenseEntity(
                            id = existing?.id ?: 0,
                            eventId = targetEventId,
                            name = name.trim(),
                            category = category,
                            estimatedAmount = Formatters.parseMoney(estimated) ?: 0.0,
                            actualAmount = Formatters.parseMoney(actual),
                            notes = notes.trim(),
                            createdAt = existing?.createdAt ?: now,
                            updatedAt = now
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save expense")
            }
        }
    }
}
