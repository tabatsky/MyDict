package jatx.mydict.ui.base

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import jatx.mydict.contracts.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

open class BaseViewModel: ViewModel(), KodeinAware {
    private var activity: Activity? = null

    override val kodein by lazy {
        val _kodein by closestKodein(activity ?: throw IllegalStateException("Cannot init kodein"))
        _kodein
    }

    val navigator: Navigator
        get() = activity as? Navigator ?: throw IllegalStateException()

    val deps by instance<Deps>()

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