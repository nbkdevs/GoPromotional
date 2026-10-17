package uk.co.softrank.eventplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uk.co.softrank.eventplanner.data.local.entity.EventEntity
import uk.co.softrank.eventplanner.data.local.entity.ExpenseEntity
import uk.co.softrank.eventplanner.data.local.entity.MerchandiseEntity
import uk.co.softrank.eventplanner.data.local.entity.TaskEntity
import uk.co.softrank.eventplanner.ui.budget.BudgetScreen
import uk.co.softrank.eventplanner.ui.budget.ExpenseEditScreen
import uk.co.softrank.eventplanner.ui.checklist.ChecklistScreen
import uk.co.softrank.eventplanner.ui.checklist.TaskEditScreen
import uk.co.softrank.eventplanner.ui.events.EventDetailScreen
import uk.co.softrank.eventplanner.ui.events.EventEditScreen
import uk.co.softrank.eventplanner.ui.events.EventsListScreen
import uk.co.softrank.eventplanner.ui.home.HomeScreen
import uk.co.softrank.eventplanner.ui.merchandise.MerchandiseEditScreen
import uk.co.softrank.eventplanner.ui.merchandise.MerchandiseScreen
import uk.co.softrank.eventplanner.ui.onboarding.OnboardingScreen
import uk.co.softrank.eventplanner.ui.themes.ThemeDetailScreen
import uk.co.softrank.eventplanner.ui.themes.ThemesListScreen
import uk.co.softrank.eventplanner.viewmodel.AppViewModel

private data class TopDest(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val topDestinations = listOf(
    TopDest(Routes.HOME, "Home", Icons.Outlined.Home),
    TopDest(Routes.EVENTS, "Events", Icons.Outlined.Event),
    TopDest(Routes.CHECKLIST, "Tasks", Icons.Outlined.CheckCircle),
    TopDest(Routes.THEMES, "Themes", Icons.Outlined.Palette),
    TopDest(Routes.BUDGET, "Budget", Icons.Outlined.AccountBalanceWallet),
    TopDest(Routes.MERCHANDISE, "Merch", Icons.Outlined.ShoppingBag)
)

@Composable
fun AppNavHost(viewModel: AppViewModel) {
    val onboardingDone by viewModel.onboardingDone.collectAsState()
    if (onboardingDone == null) return
    if (!onboardingDone!!) {
        OnboardingScreen(onContinue = { viewModel.completeOnboarding() })
        return
    }

    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = topDestinations.any { it.route == currentRoute }

    val events by viewModel.events.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val selectedEventId by viewModel.selectedEventId.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val merchandise by viewModel.merchandise.collectAsState()
    val budgetSummary by viewModel.budgetSummary.collectAsState()
    val favorites by viewModel.favoriteThemeIds.collectAsState()
    val homeSummary by viewModel.homeSummary.collectAsState()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topDestinations.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    summary = homeSummary,
                    onOpenEvents = { navController.navigate(Routes.EVENTS) },
                    onOpenChecklist = { navController.navigate(Routes.CHECKLIST) },
                    onOpenThemes = { navController.navigate(Routes.THEMES) },
                    onOpenBudget = { navController.navigate(Routes.BUDGET) },
                    onOpenMerchandise = { navController.navigate(Routes.MERCHANDISE) },
                    onOpenEvent = { navController.navigate(Routes.eventDetail(it)) },
                    onCreateEvent = { navController.navigate(Routes.eventEdit()) }
                )
            }
            composable(Routes.EVENTS) {
                EventsListScreen(
                    events = events,
                    selectedEventId = selectedEvent?.id,
                    onSelect = { viewModel.selectEvent(it) },
                    onOpen = { navController.navigate(Routes.eventDetail(it)) },
                    onCreate = { navController.navigate(Routes.eventEdit()) }
                )
            }
            composable(
                route = Routes.EVENT_DETAIL,
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("eventId") ?: return@composable
                var event by remember { mutableStateOf<EventEntity?>(null) }
                androidx.compose.runtime.LaunchedEffect(id, events) {
                    event = events.find { it.id == id } ?: viewModel.getEvent(id)
                }
                EventDetailScreen(
                    event = event,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Routes.eventEdit(id)) },
                    onDelete = {
                        event?.let {
                            viewModel.deleteEvent(it) {
                                navController.popBackStack()
                            }
                        }
                    },
                    onSelectActive = {
                        viewModel.selectEvent(id)
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Routes.EVENT_EDIT,
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("eventId") ?: -1L
                var existing by remember { mutableStateOf<EventEntity?>(null) }
                androidx.compose.runtime.LaunchedEffect(id) {
                    existing = if (id > 0) viewModel.getEvent(id) else null
                }
                EventEditScreen(
                    existing = existing,
                    onBack = { navController.popBackStack() },
                    onSave = { entity ->
                        viewModel.saveEvent(entity) {
                            navController.popBackStack()
                        }
                    }
                )
            }
            composable(Routes.CHECKLIST) {
                ChecklistScreen(
                    tasks = tasks,
                    selectedEvent = selectedEvent,
                    onAdd = { navController.navigate(Routes.taskEdit()) },
                    onEdit = { navController.navigate(Routes.taskEdit(it)) },
                    onToggle = viewModel::toggleTaskComplete,
                    onDelete = { viewModel.deleteTask(it) },
                    onSeedDefaults = viewModel::seedDefaultTasks
                )
            }
            composable(
                route = Routes.TASK_EDIT,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("taskId") ?: -1L
                var existing by remember { mutableStateOf<TaskEntity?>(null) }
                androidx.compose.runtime.LaunchedEffect(id) {
                    existing = if (id > 0) viewModel.getTask(id) else null
                }
                TaskEditScreen(
                    existing = existing,
                    selectedEventId = selectedEvent?.id ?: selectedEventId,
                    onBack = { navController.popBackStack() },
                    onSave = { task ->
                        viewModel.saveTask(task) { navController.popBackStack() }
                    }
                )
            }
            composable(Routes.THEMES) {
                ThemesListScreen(
                    themes = viewModel.themes,
                    favorites = favorites,
                    onOpen = { navController.navigate(Routes.themeDetail(it)) },
                    onToggleFavorite = viewModel::toggleFavorite
                )
            }
            composable(
                route = Routes.THEME_DETAIL,
                arguments = listOf(navArgument("themeId") { type = NavType.StringType })
            ) { entry ->
                val themeId = entry.arguments?.getString("themeId").orEmpty()
                val theme = viewModel.themeById(themeId)
                ThemeDetailScreen(
                    theme = theme,
                    isFavorite = themeId in favorites,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = { viewModel.toggleFavorite(themeId) }
                )
            }
            composable(Routes.BUDGET) {
                BudgetScreen(
                    selectedEvent = selectedEvent,
                    summary = budgetSummary,
                    expenses = expenses,
                    onAdd = { navController.navigate(Routes.expenseEdit()) },
                    onEdit = { navController.navigate(Routes.expenseEdit(it)) },
                    onDelete = { viewModel.deleteExpense(it) },
                    onCreateEvent = { navController.navigate(Routes.eventEdit()) }
                )
            }
            composable(
                route = Routes.EXPENSE_EDIT,
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("expenseId") ?: -1L
                var existing by remember { mutableStateOf<ExpenseEntity?>(null) }
                androidx.compose.runtime.LaunchedEffect(id) {
                    existing = if (id > 0) viewModel.getExpense(id) else null
                }
                ExpenseEditScreen(
                    existing = existing,
                    eventId = selectedEvent?.id,
                    onBack = { navController.popBackStack() },
                    onSave = { expense ->
                        viewModel.saveExpense(expense) { navController.popBackStack() }
                    }
                )
            }
            composable(Routes.MERCHANDISE) {
                MerchandiseScreen(
                    selectedEvent = selectedEvent,
                    items = merchandise,
                    onAdd = { navController.navigate(Routes.merchEdit()) },
                    onEdit = { navController.navigate(Routes.merchEdit(it)) },
                    onDelete = { viewModel.deleteMerchandise(it) },
                    onCreateEvent = { navController.navigate(Routes.eventEdit()) }
                )
            }
            composable(
                route = Routes.MERCH_EDIT,
                arguments = listOf(navArgument("itemId") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("itemId") ?: -1L
                var existing by remember { mutableStateOf<MerchandiseEntity?>(null) }
                androidx.compose.runtime.LaunchedEffect(id) {
                    existing = if (id > 0) viewModel.getMerchandise(id) else null
                }
                MerchandiseEditScreen(
                    existing = existing,
                    eventId = selectedEvent?.id,
                    onBack = { navController.popBackStack() },
                    onSave = { item ->
                        viewModel.saveMerchandise(item) { navController.popBackStack() }
                    }
                )
            }
        }
    }
}
