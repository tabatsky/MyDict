package jatx.mydict.ui.addword

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import jatx.mydict.contracts.AddWordScreen
import jatx.mydict.contracts.EditWordScreen
import jatx.mydict.contracts.WordScreen
import jatx.mydict.databinding.WordFragmentBinding
import jatx.mydict.ui.base.BaseFragment
import kotlinx.serialization.json.Json

class WordFragment : BaseFragment() {

    private val args by navArgs<WordFragmentArgs>()

    private val viewModel: WordViewModel by lazy {
        ViewModelProvider(this)[WordViewModel::class.java].apply {
            val screen = Json.decodeFromString(WordScreen.serializer(), args.wordScreenStr)
            wordScreen = screen
        }
    }

    private lateinit var wordFragmentBinding: WordFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        wordFragmentBinding = WordFragmentBinding.inflate(inflater, container, false)

        with(wordFragmentBinding) {
            val screen = Json.decodeFromString(WordScreen.serializer(), args.wordScreenStr)
            when (screen) {
                is AddWordScreen -> {
                    btnAddWord.visibility = View.VISIBLE
                    btnSave.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                    btnAddWord.setOnClickListener {
                        val original = etOriginal.text.toString().trim()
                        val comment = etComment.text.toString().trim()
                        val translation = etTranslation.text.toString().trim()
                        viewModel.addWord(original, comment, translation, screen.initialOrderByValue)
                    }
                }
                is EditWordScreen -> {
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

}