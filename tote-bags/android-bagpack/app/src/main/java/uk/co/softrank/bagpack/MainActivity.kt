package uk.co.softrank.bagpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.co.softrank.bagpack.data.PackItemEntity
import uk.co.softrank.bagpack.data.PackListEntity
import uk.co.softrank.bagpack.data.PackRepository

@OptIn(ExperimentalCoroutinesApi::class)
class PackViewModel(private val repository: PackRepository) : ViewModel() {
    val lists = repository.observeLists().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val selectedId = MutableStateFlow<Long?>(null)
    val selectedListId = selectedId

    val selectedList = selectedId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.observeList(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val items = selectedId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.observeItems(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun open(id: Long) { selectedId.value = id }
    fun back() { selectedId.value = null }

    fun create(name: String, seed: List<String>) = viewModelScope.launch {
        val id = repository.createList(name, seed)
        selectedId.value = id
    }

    fun deleteList(id: Long) = viewModelScope.launch {
        repository.deleteList(id)
        if (selectedId.value == id) selectedId.value = null
    }

    fun addItem(title: String) = viewModelScope.launch {
        selectedId.value?.let { repository.addItem(it, title) }
    }

    fun toggle(item: PackItemEntity) = viewModelScope.launch { repository.togglePacked(item) }
    fun deleteItem(item: PackItemEntity) = viewModelScope.launch { repository.deleteItem(item) }
}

class MainActivity : ComponentActivity() {
    private val vm: PackViewModel by viewModels {
        val app = application as App
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PackViewModel(app.repository) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF92400E),
                    onPrimary = Color.White,
                    secondary = Color(0xFFD97706),
                    background = Color(0xFFFAF7F2),
                    surface = Color.White
                )
            ) {
                BagPackApp(vm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BagPackApp(vm: PackViewModel) {
    val selected by vm.selectedList.collectAsState()
    if (selected == null) {
        ListsScreen(vm)
    } else {
        DetailScreen(vm, selected!!)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListsScreen(vm: PackViewModel) {
    val lists by vm.lists.collectAsState()
    var showCreate by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<PackListEntity?>(null) }
    val linkLabel = stringResource(R.string.resource_link_label)
    val linkUrl = stringResource(R.string.resource_url)

    Scaffold(
        topBar = { TopAppBar(title = { Text("BagPack") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreate = true }) {
                Icon(Icons.Filled.Add, contentDescription = "New list")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Packing lists for events, errands, and days out. Tick items as they go in your bag.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
            if (lists.isEmpty()) {
                Text("No lists yet. Create one or start from a template.")
                TextButton(onClick = {
                    vm.create("Conference day", PackRepository.CONFERENCE)
                }) { Text("Start conference template") }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(lists, key = { it.id }) { list ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { vm.open(list.id) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(list.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = { pendingDelete = list }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
            val annotated = buildAnnotatedString {
                append("Need a durable bag for giveaways? See ")
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

    if (showCreate) {
        CreateDialog(
            onDismiss = { showCreate = false },
            onCreate = { name, seed ->
                vm.create(name, seed)
                showCreate = false
            }
        )
    }
    pendingDelete?.let { list ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete list?") },
            text = { Text("Remove “${list.name}” and all of its items?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteList(list.id)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun CreateDialog(onDismiss: () -> Unit, onCreate: (String, List<String>) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New packing list") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("List name") },
                    singleLine = true
                )
                Text("Templates", style = MaterialTheme.typography.titleSmall)
                TextButton(onClick = { onCreate(name.ifBlank { "Conference day" }, PackRepository.CONFERENCE) }) {
                    Text("Conference day")
                }
                TextButton(onClick = { onCreate(name.ifBlank { "Grocery run" }, PackRepository.GROCERY) }) {
                    Text("Grocery run")
                }
                TextButton(onClick = { onCreate(name.ifBlank { "Day out" }, PackRepository.DAY_OUT) }) {
                    Text("Day out")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onCreate(name.trim(), emptyList())
            }) { Text("Blank list") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(vm: PackViewModel, list: PackListEntity) {
    val items by vm.items.collectAsState()
    var draft by remember { mutableStateOf("") }
    val packed = items.count { it.packed }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(list.name) },
                navigationIcon = {
                    IconButton(onClick = { vm.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("$packed of ${items.size} packed", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    label = { Text("Add item") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                TextButton(onClick = {
                    if (draft.isNotBlank()) {
                        vm.addItem(draft.trim())
                        draft = ""
                    }
                }) { Text("Add") }
            }
            if (items.isEmpty()) {
                Text("No items yet. Add what needs to go in the bag.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    items(items, key = { it.id }) { item ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = item.packed, onCheckedChange = { vm.toggle(item) })
                            Text(
                                item.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (item.packed) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            IconButton(onClick = { vm.deleteItem(item) }) {
                                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}
