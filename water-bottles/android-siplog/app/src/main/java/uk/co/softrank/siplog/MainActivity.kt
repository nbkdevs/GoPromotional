package uk.co.softrank.siplog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.co.softrank.siplog.data.PreferencesStore
import uk.co.softrank.siplog.data.SipEntity
import uk.co.softrank.siplog.data.SipRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SipViewModel(
    private val repository: SipRepository,
    private val preferences: PreferencesStore
) : ViewModel() {
    private val today = LocalDate.now().toEpochDay()
    val sips = repository.observeDay(today).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val total = repository.observeTotal(today).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val goal = preferences.goalMl.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2000)

    fun add(amount: Int) = viewModelScope.launch { repository.add(amount, today) }
    fun delete(id: Long) = viewModelScope.launch { repository.delete(id) }
    fun setGoal(ml: Int) = viewModelScope.launch { preferences.setGoal(ml) }
    fun clearToday() = viewModelScope.launch { repository.clearDay(today) }
}

class MainActivity : ComponentActivity() {
    private val vm: SipViewModel by viewModels {
        val app = application as App
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SipViewModel(app.repository, app.preferences) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0E7490),
                    onPrimary = Color.White,
                    secondary = Color(0xFF22C5A8),
                    background = Color(0xFFF0F9FB),
                    surface = Color.White
                )
            ) {
                SipScreen(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SipScreen(vm: SipViewModel) {
    val sips by vm.sips.collectAsState()
    val total by vm.total.collectAsState()
    val goal by vm.goal.collectAsState()
    var showGoal by remember { mutableStateOf(false) }
    var showClear by remember { mutableStateOf(false) }
    var custom by remember { mutableStateOf("") }
    val progress = (total.toFloat() / goal.coerceAtLeast(1)).coerceIn(0f, 1f)
    val linkLabel = stringResource(R.string.resource_link_label)
    val linkUrl = stringResource(R.string.resource_url)
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("SipLog") },
            actions = {
                TextButton(onClick = { showGoal = true }) { Text("Goal") }
                TextButton(onClick = { showClear = true }) { Text("Clear") }
            }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocalDrink, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.padding(6.dp))
                        Text("Today", style = MaterialTheme.typography.titleLarge)
                    }
                    Text("$total ml of $goal ml", style = MaterialTheme.typography.headlineMedium)
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                    Text(
                        if (total >= goal) "Goal reached" else "${goal - total} ml to go",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Text("Quick add", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf(250, 330, 500).forEach { amount ->
                    FilledTonalButton(onClick = { vm.add(amount) }, modifier = Modifier.weight(1f)) {
                        Text("+${amount}")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = custom,
                    onValueChange = { custom = it.filter { ch -> ch.isDigit() }.take(4) },
                    label = { Text("Custom ml") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(onClick = {
                    custom.toIntOrNull()?.takeIf { it > 0 }?.let {
                        vm.add(it)
                        custom = ""
                    }
                }) { Text("Add") }
            }

            Text("Today’s log", style = MaterialTheme.typography.titleMedium)
            if (sips.isEmpty()) {
                Text(
                    "No drinks logged yet. Tap a quick-add amount to start.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    items(sips, key = { it.id }) { sip ->
                        SipRow(sip, timeFmt) { vm.delete(sip.id) }
                    }
                }
            }

            val annotated = buildAnnotatedString {
                append("Planning event hydration kits? See ")
                withLink(
                    LinkAnnotation.Url(
                        url = linkUrl,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    )
                ) { append(linkLabel) }
                append(".")
            }
            Text(annotated, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
        }
    }

    if (showGoal) {
        var draft by remember { mutableStateOf(goal.toString()) }
        AlertDialog(
            onDismissRequest = { showGoal = false },
            title = { Text("Daily goal") },
            text = {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it.filter { ch -> ch.isDigit() }.take(4) },
                    label = { Text("Millilitres") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    draft.toIntOrNull()?.let { vm.setGoal(it) }
                    showGoal = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showGoal = false }) { Text("Cancel") } }
        )
    }
    if (showClear) {
        AlertDialog(
            onDismissRequest = { showClear = false },
            title = { Text("Clear today?") },
            text = { Text("Removes all drinks logged for today.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearToday()
                    showClear = false
                }) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { showClear = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SipRow(sip: SipEntity, timeFmt: DateTimeFormatter, onDelete: () -> Unit) {
    val time = Instant.ofEpochMilli(sip.createdAt).atZone(ZoneId.systemDefault()).toLocalTime().format(timeFmt)
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("${sip.amountMl} ml", style = MaterialTheme.typography.titleMedium)
                Text(time, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}
