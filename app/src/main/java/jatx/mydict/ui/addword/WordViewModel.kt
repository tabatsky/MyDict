package jatx.mydict.ui.addword

import androidx.lifecycle.viewModelScope
import jatx.mydict.R
import jatx.mydict.contracts.AddWordScreen
import jatx.mydict.contracts.EditWordScreen
import jatx.mydict.contracts.WordScreen
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordViewModel : BaseViewModel() {
    lateinit var wordScreen: WordScreen

    fun addWord(original: String, comment: String, translation: String, initialOrderByValue: Int) {
        val screen = wordScreen
        if (screen is AddWordScreen) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val word = Word(
                        original = original,
                        comment = comment,
                        translation = translation,
                        language = screen.language,
                        orderByValue = initialOrderByValue
                    )
                    deps.wordRepository.addWord(word)
                    withContext(Dispatchers.Main) {
                        navigator.back()
                    }
                }
            }
        }
    }

    fun editWord(original: String, comment: String, translation: String) {
        val screen = wordScreen
        if (screen is EditWordScreen) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val changedWord = Word(
                        id = screen.word.id,
                        original = original,
                        comment = comment,
                        translation = translation,
                        language = screen.word.language,
                        orderByValue = screen.word.orderByValue
                    )
                    deps.wordRepository.editWord(changedWord)
                    withContext(Dispatchers.Main) {
                        navigator.back()
                    }
                }
            }
        }
    }

    fun askForDeleteWord() = dialogs
        .showConfirmDialog(R.string.question_delete_word) {
            deleteWord()
        }

    private fun deleteWord() {
        val screen = wordScreen
        if (screen is EditWordScreen) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    deps.wordRepository.deleteWord(screen.word)
                    withContext(Dispatchers.Main) {
                        navigator.back()
                    }
                }
            }
        }
    }
}