package jatx.mydict

import android.app.Application
import android.content.Context
import jatx.mydict.contracts.Deps
import jatx.mydict.data.db.AppDatabase
import jatx.mydict.data.db.repository.WordRepositoryImpl
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class App: Application(), KodeinAware {
    override val kodein by Kodein.lazy {
        bind<Deps>() with singleton {
            DepsImpl(this@App)
        }
    }
}

class DepsImpl(private val context: Context): Deps {
    override val wordRepository by lazy {
        WordRepositoryImpl(AppDatabase.invoke(context))
    }
}