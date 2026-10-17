package uk.co.softrank.eventplanner.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("event_planner_prefs")

class PreferencesStore(private val context: Context) {
    private val onboardingDoneKey = booleanPreferencesKey("onboarding_done")
    private val selectedEventIdKey = longPreferencesKey("selected_event_id")

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[onboardingDoneKey] ?: false
    }

    val selectedEventId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[selectedEventIdKey]
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[onboardingDoneKey] = done }
    }

    suspend fun setSelectedEventId(id: Long?) {
        context.dataStore.edit { prefs ->
            if (id == null) {
                prefs.remove(selectedEventIdKey)
            } else {
                prefs[selectedEventIdKey] = id
            }
        }
    }
}
