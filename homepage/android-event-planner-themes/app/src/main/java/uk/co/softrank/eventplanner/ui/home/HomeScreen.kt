package uk.co.softrank.eventplanner.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.ui.components.AppScaffold
import uk.co.softrank.eventplanner.ui.components.IconLabel
import uk.co.softrank.eventplanner.ui.components.SectionCard
import uk.co.softrank.eventplanner.ui.components.StatChip
import uk.co.softrank.eventplanner.util.Formatters
import uk.co.softrank.eventplanner.viewmodel.HomeSummary

@Composable
fun HomeScreen(
    summary: HomeSummary,
    onOpenEvents: () -> Unit,
    onOpenChecklist: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenBudget: () -> Unit,
    onOpenMerchandise: () -> Unit,
    onOpenEvent: (Long) -> Unit,
    onCreateEvent: () -> Unit
) {
    AppScaffold(title = "Event Planner & Themes") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Plan your next gathering with checklists, themes, budget tracking, and merchandise lists.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatChip(label = "Events", value = summary.eventCount.toString())
                    StatChip(label = "Open tasks", value = summary.openTasks.toString())
                    StatChip(
                        label = "Active event",
                        value = summary.selectedEvent?.name?.take(14)
                            ?: "None"
                    )
                }
            }

            SectionCard {
                Text("Quick links", style = MaterialTheme.typography.titleMedium)
                ShortcutRow("Events", Icons.Outlined.Event, onOpenEvents)
                ShortcutRow("Checklist", Icons.Outlined.CheckCircle, onOpenChecklist)
                ShortcutRow("Themes", Icons.Outlined.Palette, onOpenThemes)
                ShortcutRow("Budget", Icons.Outlined.AccountBalanceWallet, onOpenBudget)
                ShortcutRow("Merchandise", Icons.Outlined.ShoppingBag, onOpenMerchandise)
            }

            SectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Your events", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = if (summary.eventCount == 0) onCreateEvent else onOpenEvents) {
                        Text(if (summary.eventCount == 0) "Create" else "See all")
                    }
                }
                if (summary.upcomingEvents.isEmpty()) {
                    Text(
                        "No events yet. Create one to unlock checklist, budget, and merchandise planning.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    summary.upcomingEvents.forEach { event ->
                        EventRow(event = event, onClick = { onOpenEvent(event.id) })
                    }
                }
            }

            Text(
                text = "Your data stays on this device. No account or internet is required.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ShortcutRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        IconLabel(icon = icon, text = label)
    }
}

@Composable
private fun EventRow(event: EventEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(event.name, style = MaterialTheme.typography.titleMedium)
        Text(
            "${event.type} · ${Formatters.formatDate(event.dateEpochDay)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
