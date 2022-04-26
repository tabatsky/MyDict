package jatx.mydict

import android.app.Activity
import android.app.Application
import android.content.Context
import jatx.mydict.contracts.Deps
import jatx.mydict.data.db.AppDatabase
import jatx.mydict.data.db.repository.WordRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}

val appModule = module {
    single<Deps> { DepsImpl(get()) }
    single { ActivityProvider() }
}

class DepsImpl(private val context: Context): Deps {
    override val wordRepository by lazy {
        WordRepositoryImpl(AppDatabase.invoke(context))
    }
}

class ActivityProvider {
    var currentActivity: Activity? = null
}