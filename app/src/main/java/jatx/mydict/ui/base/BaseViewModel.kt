package jatx.mydict.ui.base

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import jatx.mydict.contracts.*

open class BaseViewModel: ViewModel() {
    private var _navigator: Navigator? = null
    private var _deps: Deps? = null
    private var _backup: Backup? = null
    private var _toasts: Toasts? = null
    private var _dialogs: Dialogs? = null

    val navigator: Navigator
        get() = _navigator ?: throw IllegalStateException()

    val deps: Deps
        get() = _deps ?: throw IllegalStateException()

    val backup: Backup
        get() = _backup ?: throw IllegalStateException()

    val toasts: Toasts
        get() = _toasts ?: throw IllegalStateException()

    val dialogs: Dialogs
        get() = _dialogs ?: throw IllegalStateException()


    fun injectFromActivity(activity: Activity) {
        _navigator = activity as Navigator
        _deps = activity as Deps
        _backup = activity as Backup
        _toasts = activity as Toasts
        _dialogs = activity as Dialogs
    }

    override fun onCleared() {
        super.onCleared()

        _navigator = null
        _deps = null
        _backup = null
        _toasts = null
        _dialogs = null

        Log.e("BaseViewModel", "onCleared()")
    }
}