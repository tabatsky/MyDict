package jatx.mydict.ui.addword

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jatx.mydict.R
import jatx.mydict.databinding.AddWordFragmentBinding
import jatx.mydict.domain.Language
import jatx.mydict.navigation.Navigator
import jatx.mydict.ui.base.BaseFragment

class AddWordFragment : BaseFragment() {

    companion object {
        private lateinit var language: Language

        fun newInstance(language: Language): AddWordFragment {
            val addWordFragment = AddWordFragment()
            this.language = language
            return addWordFragment
        }
    }

    private lateinit var addWordFragmentBinding: AddWordFragmentBinding
    private lateinit var viewModel: AddWordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setTitle(getString(R.string.title_add_word))

        addWordFragmentBinding = AddWordFragmentBinding.inflate(inflater, container, false)

        with(addWordFragmentBinding) {
            btnAddWord.setOnClickListener {
                val original = etOriginal.text.toString()
                val comment = etComment.text.toString()
                val translation = etTranslation.text.toString()
                viewModel.addWord(original, comment, translation)
            }
        }

        return addWordFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[AddWordViewModel::class.java]
        viewModel.injectActivity(requireActivity())

        viewModel.language = language
    }

}