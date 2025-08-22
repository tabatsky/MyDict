package jatx.mydict.ui.dict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import jatx.mydict.databinding.DictFragmentBinding
import jatx.mydict.ui.base.BaseFragment
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class DictFragment : BaseFragment() {

    private val args by navArgs<DictFragmentArgs>()

    private val viewModel: DictViewModel by lazy {
       ViewModelProvider(this)[DictViewModel::class.java].apply {
           language = args.language
           afterSort = {
               dictFragmentBinding.rvWords.scrollToPosition(0)
           }
       }
    }

    private lateinit var dictFragmentBinding: DictFragmentBinding

    private val adapter by lazy {
        DictAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dictFragmentBinding = DictFragmentBinding.inflate(inflater, container, false)

        dictFragmentBinding.lifecycleOwner = this
        dictFragmentBinding.viewmodel = viewModel

        dictFragmentBinding.rvWords.adapter = adapter

        return dictFragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.startJob()
        adapter.onWordClick = { viewModel.editWord(it) }
    }

    override fun onStop() {
        viewModel.stopJob()
        super.onStop()
    }

}