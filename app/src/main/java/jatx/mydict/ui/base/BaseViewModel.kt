package jatx.mydict.ui.base

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import jatx.mydict.contracts.*

open class BaseViewModel: ViewModel() {
    private var activity: Activity? = null

    val navigator: Navigator
        get() = activity as? Navigator ?: throw IllegalStateException()

    val deps: Deps
        get() = activity as? Deps ?: throw IllegalStateException()

    val backup: Backup
        get() = activity as? Backup ?: throw IllegalStateException()

    val toasts: Toasts
        get() = activity as? Toasts ?: throw IllegalStateException()

    val dialogs: Dialogs
        get() = activity as? Dialogs ?: throw IllegalStateException()


    fun injectFromActivity(activity: Activity) {
        this.activity = activity
    }

    override fun onCleared() {
        super.onCleared()

        activity = null

        Log.e("BaseViewModel", "onCleared()")
    }
}