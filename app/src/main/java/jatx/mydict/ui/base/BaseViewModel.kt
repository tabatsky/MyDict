package jatx.mydict.ui.base

import android.app.Activity
import androidx.lifecycle.ViewModel
import jatx.mydict.deps.Deps
import jatx.mydict.navigation.Navigator

open class BaseViewModel: ViewModel() {
    lateinit var navigator: Navigator
    lateinit var deps: Deps

    fun injectActivity(activity: Activity) {
        this.navigator = activity as Navigator
        this.deps = activity as Deps
    }
}