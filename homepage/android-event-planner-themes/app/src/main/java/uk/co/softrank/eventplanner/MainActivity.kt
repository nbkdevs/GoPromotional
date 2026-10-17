package uk.co.softrank.eventplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import uk.co.softrank.eventplanner.ui.navigation.AppNavHost
import uk.co.softrank.eventplanner.ui.theme.EventPlannerTheme
import uk.co.softrank.eventplanner.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels {
        val app = application as EventPlannerApp
        AppViewModel.factory(app.repository, app.preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splash.setKeepOnScreenCondition {
            viewModel.onboardingDone.value == null
        }
        setContent {
            EventPlannerTheme {
                AppNavHost(viewModel = viewModel)
            }
        }
    }
}
