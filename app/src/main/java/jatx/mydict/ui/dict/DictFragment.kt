package jatx.mydict.ui.dict

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import jatx.mydict.databinding.DictFragmentBinding
import jatx.mydict.ui.base.BaseFragment
import jatx.mydict.domain.Language
import java.lang.IllegalArgumentException

class DictFragment : BaseFragment() {

    companion object {
        private val KEY_LANGUAGE = "language"

        fun newInstance(language: Language): DictFragment {
            val dictFragment = DictFragment()
            dictFragment.arguments = bundleOf(KEY_LANGUAGE to language)
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
        val language = requireArguments().getSerializable(KEY_LANGUAGE)
                as? Language ?: throw IllegalArgumentException()
        setTitle(language.rusString)

        dictFragmentBinding = DictFragmentBinding.inflate(inflater, container, false)

        dictFragmentBinding.btnAddWord.setOnClickListener {
            viewModel.addWord()
        }

        dictFragmentBinding.btnTesting.setOnClickListener {
            viewModel.openTesting()
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
        adapter.onWordClick = { viewModel.editWord(it) }
    }

    override fun onStop() {
        viewModel.stopJob()
        super.onStop()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[DictViewModel::class.java]
        viewModel.injectFromActivity(requireActivity())

        val language = requireArguments().getSerializable(KEY_LANGUAGE)
                as? Language ?: throw IllegalArgumentException()

        viewModel.language = language
    }

}