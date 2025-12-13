package jatx.mydict.ui.base

import android.app.Activity
import androidx.lifecycle.ViewModel
import jatx.mydict.ActivityProvider
import jatx.mydict.contracts.*
import org.koin.java.KoinJavaComponent.inject

open class BaseViewModel: ViewModel() {
    private val activityProvider: ActivityProvider by inject(ActivityProvider::class.java)
    private val activity: Activity?
        get() = activityProvider.currentActivity

    val deps: Deps by inject(Deps::class.java)

    val navigator: Navigator
        get() = activity as? Navigator ?: throw IllegalStateException()

    val backup: Backup
        get() = activity as? Backup ?: throw IllegalStateException()

    val toasts: Toasts
        get() = activity as? Toasts ?: throw IllegalStateException()

    val dialogs: Dialogs
        get() = activity as? Dialogs ?: throw IllegalStateException()

    val auth: Auth
        get() = activity as? Auth ?: throw IllegalStateException()

    val speaker: Speaker
        get() = activity as? Speaker ?: throw IllegalStateException()

    val mistaker: Mistaker
        get() = activity as? Mistaker ?: throw IllegalStateException()
}