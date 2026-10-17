package uk.co.softrank.siplog

import android.app.Application
import uk.co.softrank.siplog.data.AppDatabase
import uk.co.softrank.siplog.data.PreferencesStore
import uk.co.softrank.siplog.data.SipRepository

class App : Application() {
    lateinit var repository: SipRepository
        private set
    lateinit var preferences: PreferencesStore
        private set

    override fun onCreate() {
        super.onCreate()
        repository = SipRepository(AppDatabase.get(this))
        preferences = PreferencesStore(this)
    }
}
