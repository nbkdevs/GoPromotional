package uk.co.softrank.eventplanner.ui.themes

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.co.softrank.eventplanner.data.seed.EventTheme
import uk.co.softrank.eventplanner.ui.components.AppScaffold
import uk.co.softrank.eventplanner.ui.components.EmptyState
import uk.co.softrank.eventplanner.ui.components.IdeaList
import uk.co.softrank.eventplanner.ui.components.SectionCard

@Composable
fun ThemesListScreen(
    themes: List<EventTheme>,
    favorites: Set<String>,
    onOpen: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    var showFavoritesOnly by remember { mutableStateOf(false) }
    val visible = if (showFavoritesOnly) {
        themes.filter { it.id in favorites }
    } else {
        themes
    }

    AppScaffold(title = "Themes") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilterChip(
                selected = showFavoritesOnly,
                onClick = { showFavoritesOnly = !showFavoritesOnly },
                label = { Text("Favorites") },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (visible.isEmpty()) {
                EmptyState(
                    title = "No favorite themes yet",
                    body = "Open a theme and tap the heart to save it for later."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visible, key = { it.id }) { theme ->
                        SectionCard(
                            modifier = Modifier.clickable { onOpen(theme.id) }
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(theme.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        theme.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onToggleFavorite(theme.id) }) {
                                    Icon(
                                        imageVector = if (theme.id in favorites) {
                                            Icons.Filled.Favorite
                                        } else {
                                            Icons.Outlined.FavoriteBorder
                                        },
                                        contentDescription = "Favorite",
                                        tint = if (theme.id in favorites) {
                                            MaterialTheme.colorScheme.secondary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeDetailScreen(
    theme: EventTheme?,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    if (theme == null) {
        AppScaffold(title = "Theme", onBack = onBack) { padding ->
            EmptyState(
                title = "Theme not found",
                body = "This theme is unavailable.",
                modifier = Modifier.padding(padding),
                actionLabel = "Back",
                onAction = onBack
            )
        }
        return
    }

    AppScaffold(
        title = theme.name,
        onBack = onBack,
        actions = {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite"
                )
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
                Text(theme.description, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Colour / style",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    theme.colorStyle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SectionCard { IdeaList("Décor ideas", theme.decorIdeas) }
            SectionCard { IdeaList("Invitation ideas", theme.invitationIdeas) }
            SectionCard { IdeaList("Signage ideas", theme.signageIdeas) }
            SectionCard { IdeaList("Activity ideas", theme.activityIdeas) }
            SectionCard { IdeaList("Merchandise ideas", theme.merchandiseIdeas) }
        }
    }
}
