package jatx.mydict.ui.dict

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import jatx.mydict.databinding.DictFragmentBinding
import jatx.mydict.ui.base.BaseFragment

class DictFragment : BaseFragment() {

    private val args by navArgs<DictFragmentArgs>()

    private val viewModel: DictViewModel by lazy {
       ViewModelProvider(this)[DictViewModel::class.java].apply {
           language = args.language
       }
    }

    private lateinit var dictFragmentBinding: DictFragmentBinding

    private val adapter = DictAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dictFragmentBinding = DictFragmentBinding.inflate(inflater, container, false)

        dictFragmentBinding.btnAddWord.setOnClickListener {
            viewModel.addWord()
        }

        dictFragmentBinding.btnTesting.setOnClickListener {
            viewModel.openTesting()
        }

        dictFragmentBinding.tvOriginalHeader.setOnClickListener {
            viewModel.sortByOriginal()
        }

        dictFragmentBinding.tvTranslationHeader.setOnClickListener {
            viewModel.sortByTranslation()
        }

        dictFragmentBinding.rvWords.adapter = adapter
        dictFragmentBinding.rvWords.layoutManager = LinearLayoutManager(requireContext())

        return dictFragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.startJob(true)
        viewModel.words.observe(viewLifecycleOwner) {
            adapter.updateWords(it)
        }
        adapter.onWordClick = { viewModel.editWord(it) }
    }

    override fun onStop() {
        viewModel.stopJob()
        super.onStop()
    }

    override fun onActivityCreated() {
        super.onActivityCreated()

        Log.e("DictFragment", "onActivityCreated()")

        viewModel.injectFromActivity(requireActivity())
    }

}