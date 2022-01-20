package jatx.mydict.contracts

import androidx.annotation.StringRes

interface Dialogs {
    fun showConfirmDialog(msg: String, onConfirm: () -> Unit)
    fun showConfirmDialog(@StringRes msgResId: Int, onConfirm: () -> Unit)
}