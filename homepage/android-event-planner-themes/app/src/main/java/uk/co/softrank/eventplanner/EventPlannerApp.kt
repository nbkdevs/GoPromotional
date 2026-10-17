package uk.co.softrank.eventplanner

import android.app.Application
import uk.co.softrank.eventplanner.data.PreferencesStore
import uk.co.softrank.eventplanner.data.local.AppDatabase
import uk.co.softrank.eventplanner.data.repository.PlannerRepository

class EventPlannerApp : Application() {
    lateinit var repository: PlannerRepository
        private set
    lateinit var preferences: PreferencesStore
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = PlannerRepository(db)
        preferences = PreferencesStore(this)
    }
}
