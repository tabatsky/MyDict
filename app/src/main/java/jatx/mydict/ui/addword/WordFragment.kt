package jatx.mydict.ui.addword

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jatx.mydict.R
import jatx.mydict.databinding.WordFragmentBinding
import jatx.mydict.navigation.AddWordScreen
import jatx.mydict.navigation.EditWordScreen
import jatx.mydict.navigation.WordScreen
import jatx.mydict.ui.base.BaseFragment

class WordFragment : BaseFragment() {

    companion object {
        private lateinit var wordScreen: WordScreen

        fun newInstance(wordScreen: WordScreen): WordFragment {
            this.wordScreen = wordScreen
            return WordFragment()
        }
    }

    private lateinit var wordFragmentBinding: WordFragmentBinding
    private lateinit var viewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        wordFragmentBinding = WordFragmentBinding.inflate(inflater, container, false)

        with(wordFragmentBinding) {
            when (val screen = wordScreen) {
                is AddWordScreen -> {
                    setTitle(getString(R.string.title_add_word))
                    btnAddWord.visibility = View.VISIBLE
                    btnSave.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                    btnAddWord.setOnClickListener {
                        val original = etOriginal.text.toString().trim()
                        val comment = etComment.text.toString().trim()
                        val translation = etTranslation.text.toString().trim()
                        viewModel.addWord(original, comment, translation)
                    }
                }
                is EditWordScreen -> {
                    setTitle(getString(R.string.title_edit_word))
                    btnAddWord.visibility = View.GONE
                    btnSave.visibility = View.VISIBLE
                    btnDelete.visibility = View.VISIBLE
                    etOriginal.setText(screen.word.original)
                    etComment.setText(screen.word.comment)
                    etTranslation.setText(screen.word.translation)
                    btnSave.setOnClickListener {
                        val original = etOriginal.text.toString().trim()
                        val comment = etComment.text.toString().trim()
                        val translation = etTranslation.text.toString().trim()
                        viewModel.editWord(original, comment, translation)
                    }
                    btnDelete.setOnClickListener {
                        viewModel.askForDeleteWord()
                    }
                }
            }
        }

        return wordFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[WordViewModel::class.java]
        viewModel.injectFromActivity(requireActivity())

        viewModel.wordScreen = wordScreen
    }

}