package jatx.mydict.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jatx.mydict.R
import jatx.mydict.databinding.MainFragmentBinding
import jatx.mydict.domain.Language
import jatx.mydict.navigation.DictScreen
import jatx.mydict.navigation.Navigator
import jatx.mydict.ui.base.BaseFragment

class MainFragment : BaseFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var mainFragmentBinding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setTitle(getString(R.string.app_name))

        mainFragmentBinding = MainFragmentBinding.inflate(inflater, container, false)

        mainFragmentBinding.btnEnglish.setOnClickListener {
            viewModel.selectLanguage(Language.ENGLISH)
        }

        mainFragmentBinding.btnDeutsch.setOnClickListener {
            viewModel.selectLanguage(Language.DEUTSCH)
        }

        mainFragmentBinding.btnJapanese.setOnClickListener {
            viewModel.selectLanguage(Language.JAPANESE)
        }

        return mainFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.e("MyDict", "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.injectActivity(requireActivity())
    }

}