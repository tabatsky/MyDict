package jatx.mydict.ui.base

import android.app.Activity
import androidx.lifecycle.ViewModel
import jatx.mydict.backup.Backuper
import jatx.mydict.deps.Deps
import jatx.mydict.navigation.Navigator

open class BaseViewModel: ViewModel() {
    lateinit var navigator: Navigator
    lateinit var deps: Deps
    lateinit var backuper: Backuper

    fun injectFromActivity(activity: Activity) {
        navigator = activity as Navigator
        deps = activity as Deps
        backuper = activity as Backuper
    }
}