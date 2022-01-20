package jatx.mydict.ui.base

import android.app.Activity
import androidx.lifecycle.ViewModel
import jatx.mydict.backup.Backup
import jatx.mydict.contracts.Deps
import jatx.mydict.contracts.Dialogs
import jatx.mydict.contracts.Toasts
import jatx.mydict.navigation.Navigator

open class BaseViewModel: ViewModel() {
    lateinit var navigator: Navigator
    lateinit var deps: Deps
    lateinit var backup: Backup
    lateinit var toasts: Toasts
    lateinit var dialogs: Dialogs

    fun injectFromActivity(activity: Activity) {
        navigator = activity as Navigator
        deps = activity as Deps
        backup = activity as Backup
        toasts = activity as Toasts
        dialogs = activity as Dialogs
    }
}