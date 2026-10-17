package uk.co.softrank.mugday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import uk.co.softrank.mugday.data.MugLogEntity
import uk.co.softrank.mugday.data.MugRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MugViewModel(private val repository: MugRepository) : ViewModel() {
    private val today = LocalDate.now().toEpochDay()
    private val weekStart = LocalDate.now().minusDays(6).toEpochDay()

    val recent = repository.observeRecent().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val reusableToday = repository.observeReusableToday(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val reusableWeek = repository.observeReusableSince(weekStart)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val disposableWeek = repository.observeDisposableSince(weekStart)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun logReusable(drink: String) = viewModelScope.launch { repository.log(true, drink) }
    fun logDisposable(drink: String) = viewModelScope.launch { repository.log(false, drink) }
    fun delete(id: Long) = viewModelScope.launch { repository.delete(id) }
}

class MainActivity : ComponentActivity() {
    private val vm: MugViewModel by viewModels {
        val app = application as App
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                MugViewModel(app.repository) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF7C2D12),
                    onPrimary = Color.White,
                    secondary = Color(0xFFB45309),
                    background = Color(0xFFFAF5F0),
                    surface = Color.White
                )
            ) {
                MugScreen(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MugScreen(vm: MugViewModel) {
    val recent by vm.recent.collectAsState()
    val reusableToday by vm.reusableToday.collectAsState()
    val reusableWeek by vm.reusableWeek.collectAsState()
    val disposableWeek by vm.disposableWeek.collectAsState()
    var drink by remember { mutableStateOf("") }
    val linkLabel = stringResource(R.string.resource_link_label)
    val linkUrl = stringResource(R.string.resource_url)
    val timeFmt = remember { DateTimeFormatter.ofPattern("EEE HH:mm") }

    Scaffold(topBar = { TopAppBar(title = { Text("MugDay") }) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Coffee, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("  Reusable cup tracker", style = MaterialTheme.typography.titleLarge)
                    }
                    Text("Today: $reusableToday reusable use${if (reusableToday == 1) "" else "s"}")
                    Text("Last 7 days: $reusableWeek reusable · $disposableWeek disposable")
                    Text(
                        "Log each drink to see how often you bring your travel mug.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            OutlinedTextField(
                value = drink,
                onValueChange = { drink = it.take(40) },
                label = { Text("Drink (optional)") },
                placeholder = { Text("Coffee, tea…") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        vm.logReusable(drink.trim())
                        drink = ""
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Used travel mug") }
                FilledTonalButton(
                    onClick = {
                        vm.logDisposable(drink.trim())
                        drink = ""
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Disposable cup") }
            }

            Text("Recent log", style = MaterialTheme.typography.titleMedium)
            if (recent.isEmpty()) {
                Text(
                    "Nothing logged yet. Tap “Used travel mug” after your next drink.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    modifier = Modifier.height(260.dp)
                ) {
                    items(recent, key = { it.id }) { log ->
                        LogRow(log, timeFmt) { vm.delete(log.id) }
                    }
                }
            }

            val annotated = buildAnnotatedString {
                append("Choosing cups for a commute kit or event? See ")
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
            Text(annotated, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun LogRow(log: MugLogEntity, timeFmt: DateTimeFormatter, onDelete: () -> Unit) {
    val time = Instant.ofEpochMilli(log.createdAt).atZone(ZoneId.systemDefault()).format(timeFmt)
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (log.usedReusable) "Travel mug · ${log.drinkName}" else "Disposable · ${log.drinkName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(time, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}
