package jatx.mydict.ui.base

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.*

open class BaseFragment: Fragment() {

    open fun onActivityCreated() {
        Log.e("BaseFragment", "onActivityCreated()")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(
            object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    activity?.lifecycle?.removeObserver(this)
                    onActivityCreated()
                }
            }
        )
    }
}