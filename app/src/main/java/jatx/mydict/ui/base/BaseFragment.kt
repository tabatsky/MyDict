package jatx.mydict.ui.base

import androidx.fragment.app.Fragment
import jatx.mydict.navigation.Navigator

open class BaseFragment: Fragment() {

    fun setTitle(title: String) {
        requireActivity().title = title
    }
}