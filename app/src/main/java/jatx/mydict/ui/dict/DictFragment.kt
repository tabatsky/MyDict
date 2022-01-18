package jatx.mydict.ui.dict

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import jatx.mydict.databinding.DictFragmentBinding
import jatx.mydict.ui.base.BaseFragment
import jatx.mydict.domain.Language
import jatx.mydict.navigation.Navigator

class DictFragment() : BaseFragment() {

    companion object {
        private lateinit var language: Language

        fun newInstance(language: Language): DictFragment {
            val dictFragment = DictFragment()
            this.language = language
            return dictFragment
        }
    }

    private lateinit var dictFragmentBinding: DictFragmentBinding
    private lateinit var viewModel: DictViewModel

    private val adapter = DictAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setTitle(language.rusString)

        dictFragmentBinding = DictFragmentBinding.inflate(inflater, container, false)

        dictFragmentBinding.btnAddWord.setOnClickListener {
            viewModel.addWord()
        }

        dictFragmentBinding.rvWords.adapter = adapter
        dictFragmentBinding.rvWords.layoutManager = LinearLayoutManager(requireContext())

        return dictFragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.startJob()
        viewModel.words.observe(viewLifecycleOwner) {
            adapter.updateWords(it)
        }
    }

    override fun onStop() {
        viewModel.stopJob()
        super.onStop()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[DictViewModel::class.java]
        viewModel.injectActivity(requireActivity())

        viewModel.language = language
    }

}