package jatx.mydict.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import jatx.mydict.databinding.MainFragmentBinding
import jatx.mydict.domain.Language
import jatx.mydict.ui.base.BaseFragment

class MainFragment : BaseFragment() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var mainFragmentBinding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainFragmentBinding = MainFragmentBinding.inflate(inflater, container, false)

        with(mainFragmentBinding) {
            btnEnglish.setOnClickListener {
                viewModel.selectLanguage(Language.ENGLISH)
            }

            btnDeutsch.setOnClickListener {
                viewModel.selectLanguage(Language.DEUTSCH)
            }

            btnJapanese.setOnClickListener {
                viewModel.selectLanguage(Language.JAPANESE)
            }

            btnAuth.setOnClickListener {
                viewModel.auth()
            }

            btnLoadData.setOnClickListener {
                viewModel.loadData()
            }

            btnSaveData.setOnClickListener {
                viewModel.saveData()
            }
        }

        return mainFragmentBinding.root
    }

}