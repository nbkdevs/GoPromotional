package uk.co.softrank.bagpack

import android.app.Application
import uk.co.softrank.bagpack.data.AppDatabase
import uk.co.softrank.bagpack.data.PackRepository

class App : Application() {
    lateinit var repository: PackRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = PackRepository(AppDatabase.get(this))
    }
}
