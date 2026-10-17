package uk.co.softrank.eventplanner.ui.merchandise

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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import uk.co.softrank.eventplanner.R
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.data.local.entity.MerchandiseEntity
import uk.co.softrank.eventplanner.ui.components.AppScaffold
import uk.co.softrank.eventplanner.ui.components.DeleteConfirmDialog
import uk.co.softrank.eventplanner.ui.components.DropdownField
import uk.co.softrank.eventplanner.ui.components.EmptyState
import uk.co.softrank.eventplanner.ui.components.FormTextField
import uk.co.softrank.eventplanner.ui.components.PrimaryFab
import uk.co.softrank.eventplanner.ui.components.SectionCard
import uk.co.softrank.eventplanner.util.AppConstants
import uk.co.softrank.eventplanner.util.Formatters

@Composable
fun MerchandiseScreen(
    selectedEvent: EventEntity?,
    items: List<MerchandiseEntity>,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (MerchandiseEntity) -> Unit,
    onCreateEvent: () -> Unit
) {
    var pendingDelete by remember { mutableStateOf<MerchandiseEntity?>(null) }
    val resourceUrl = stringResource(R.string.merch_resource_url)

    AppScaffold(
        title = "Merchandise",
        floatingActionButton = {
            if (selectedEvent != null) {
                PrimaryFab(Icons.Filled.Add, "Add item", onAdd)
            }
        }
    ) { padding ->
        if (selectedEvent == null) {
            EmptyState(
                title = "Select an event first",
                body = "Merchandise lists are tied to an event so you can plan quantities and costs.",
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
                Text("Planning for ${selectedEvent.name}", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Track giveaways and useful items for guests. Everything stays on this device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val linkLabel = stringResource(R.string.merch_resource_link_label)
                val annotated = buildAnnotatedString {
                    append("Looking for ideas for ")
                    withLink(
                        LinkAnnotation.Url(
                            url = resourceUrl,
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            )
                        )
                    ) {
                        append(linkLabel)
                    }
                    append(" for your event?")
                }
                Text(
                    text = annotated,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (items.isEmpty()) {
                EmptyState(
                    title = stringResource(R.string.empty_merch_title),
                    body = stringResource(R.string.empty_merch_body),
                    actionLabel = "Add item",
                    onAction = onAdd
                )
            } else {
                val total = items.sumOf { it.totalCost }
                Text(
                    "Estimated merchandise total: ${Formatters.formatMoney(total)}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        SectionCard {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${item.quantity} × ${Formatters.formatMoney(item.unitCost)} = ${Formatters.formatMoney(item.totalCost)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        item.status,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = { onEdit(item.id) }) { Text("Edit") }
                                IconButton(onClick = { pendingDelete = item }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { item ->
        DeleteConfirmDialog(
            title = "Delete item?",
            message = "Remove “${item.name}” from this merchandise list?",
            onConfirm = {
                onDelete(item)
                pendingDelete = null
            },
            onDismiss = { pendingDelete = null }
        )
    }
}

@Composable
fun MerchandiseEditScreen(
    existing: MerchandiseEntity?,
    eventId: Long?,
    onBack: () -> Unit,
    onSave: (MerchandiseEntity) -> Unit
) {
    var name by remember {
        mutableStateOf(existing?.name ?: AppConstants.MERCH_STARTERS.first())
    }
    var quantity by remember {
        mutableStateOf(existing?.quantity?.toString() ?: "50")
    }
    var unitCost by remember {
        mutableStateOf(existing?.unitCost?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var status by remember {
        mutableStateOf(existing?.status ?: AppConstants.MERCH_STATUSES.first())
    }
    var notes by remember { mutableStateOf(existing?.notes.orEmpty()) }
    var nameError by remember { mutableStateOf(false) }
    var qtyError by remember { mutableStateOf(false) }
    var eventMissing by remember { mutableStateOf(false) }

    LaunchedEffect(existing?.id) {
        if (existing != null) {
            name = existing.name
            quantity = existing.quantity.toString()
            unitCost = existing.unitCost.takeIf { it > 0 }?.toString().orEmpty()
            status = existing.status
            notes = existing.notes
        }
    }

    AppScaffold(
        title = if (existing == null) "New merchandise item" else "Edit item",
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
                    "Select an active event before saving merchandise.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            DropdownField(
                label = "Starter ideas",
                value = if (name in AppConstants.MERCH_STARTERS) name else "Other",
                options = AppConstants.MERCH_STARTERS,
                onSelected = { selected ->
                    if (selected != "Other") name = selected
                }
            )
            FormTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = "Item name",
                isError = nameError,
                supportingText = if (nameError) "Name is required" else null
            )
            FormTextField(
                value = quantity,
                onValueChange = {
                    quantity = it
                    qtyError = false
                },
                label = "Quantity",
                keyboardType = KeyboardType.Number,
                isError = qtyError,
                supportingText = if (qtyError) "Enter a whole number of 1 or more" else null
            )
            FormTextField(
                value = unitCost,
                onValueChange = { unitCost = it },
                label = "Estimated unit cost (£)",
                keyboardType = KeyboardType.Decimal
            )
            val qty = Formatters.parseInt(quantity) ?: 0
            val unit = Formatters.parseMoney(unitCost) ?: 0.0
            Text(
                "Total: ${Formatters.formatMoney(qty * unit)}",
                style = MaterialTheme.typography.titleMedium
            )
            DropdownField(
                label = "Status",
                value = status,
                options = AppConstants.MERCH_STATUSES,
                onSelected = { status = it }
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
                    val parsedQty = Formatters.parseInt(quantity)
                    if (parsedQty == null || parsedQty < 1) {
                        qtyError = true
                        return@Button
                    }
                    val targetEventId = existing?.eventId ?: eventId
                    if (targetEventId == null) {
                        eventMissing = true
                        return@Button
                    }
                    val now = System.currentTimeMillis()
                    onSave(
                        MerchandiseEntity(
                            id = existing?.id ?: 0,
                            eventId = targetEventId,
                            name = name.trim(),
                            quantity = parsedQty,
                            unitCost = Formatters.parseMoney(unitCost) ?: 0.0,
                            status = status,
                            notes = notes.trim(),
                            createdAt = existing?.createdAt ?: now,
                            updatedAt = now
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save item")
            }
        }
    }
}
