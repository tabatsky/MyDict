package jatx.mydict.ui.base

import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    fun setTitle(title: String) {
        requireActivity().title = title
    }
}