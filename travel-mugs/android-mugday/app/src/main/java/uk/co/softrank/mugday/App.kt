package uk.co.softrank.mugday

import android.app.Application
import uk.co.softrank.mugday.data.AppDatabase
import uk.co.softrank.mugday.data.MugRepository

class App : Application() {
    lateinit var repository: MugRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = MugRepository(AppDatabase.get(this))
    }
}
