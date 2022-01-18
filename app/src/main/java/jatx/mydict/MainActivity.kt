package jatx.mydict

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import jatx.mydict.data.db.AppDatabase
import jatx.mydict.data.db.repository.WordRepositoryImpl
import jatx.mydict.deps.Deps
import jatx.mydict.domain.repository.WordRepository
import jatx.mydict.navigation.*
import jatx.mydict.ui.addword.AddWordFragment
import jatx.mydict.ui.dict.DictFragment
import jatx.mydict.ui.main.*

class MainActivity : AppCompatActivity(), Navigator, Deps {

    companion object {
        private var currentScreen: Screen? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            val fragment = MainFragment.newInstance()
            commitFragment(fragment)
        }
    }

    override fun navigateTo(screen: Screen) {
        currentScreen = screen
        val fragment = when (screen) {
            is MainScreen -> {
                MainFragment.newInstance()
            }
            is DictScreen -> {
                DictFragment.newInstance(screen.language)
            }
            is AddWordScreen -> {
                AddWordFragment.newInstance(screen.language)
            }
        }
        commitFragment(fragment)
    }

    override fun back() {
        when (val screen = currentScreen) {
            is MainScreen -> {
                finish()
            }
            is DictScreen -> {
                navigateTo(MainScreen)
            }
            is AddWordScreen -> {
                navigateTo(DictScreen(screen.language))
            }
        }
    }

    override fun onBackPressed() {
        back()
    }

    private fun commitFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()
    }

    override val wordRepository by lazy {
        WordRepositoryImpl(AppDatabase.invoke(applicationContext))
    }
}