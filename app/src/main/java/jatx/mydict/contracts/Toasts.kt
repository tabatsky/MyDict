package jatx.mydict.contracts

import androidx.annotation.StringRes

interface Toasts {
    fun showToast(toastText: String)
    fun showToast(@StringRes resId: Int)
}