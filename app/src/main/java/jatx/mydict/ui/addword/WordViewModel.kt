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
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class WordViewModel : BaseViewModel() {
    lateinit var wordScreen: WordScreen

    fun addWord(original: String, comment: String, translation: String, initialOrderByValue: Int) {
        val screen = wordScreen
        if (screen is AddWordScreen) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val existingWord = deps.wordRepository.searchForWord(original, screen.language)
                    existingWord?.let {
                        withContext(Dispatchers.Main) {
                            toasts.showToast(R.string.word_already_exists)
                        }
                    } ?: run {
                        val newWord = Word(
                            original = original,
                            comment = comment,
                            translation = translation,
                            language = screen.language,
                            orderByValue = initialOrderByValue
                        )
                        deps.wordRepository.addWord(newWord)
                        withContext(Dispatchers.Main) {
                            navigator.back()
                        }
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
                    val existingWord =
                        if (original != screen.word.original)
                            deps.wordRepository.searchForWord(original, screen.word.language)
                        else
                            null
                    existingWord?.let {
                        withContext(Dispatchers.Main) {
                            toasts.showToast(R.string.word_already_exists)
                        }
                    } ?: run {
                        val changedWord = Word(
                            id = screen.word.id,
                            original = original,
                            comment = comment,
                            translation = translation,
                            language = screen.word.language,
                            correctAnswerCount = screen.word.correctAnswerCount,
                            incorrectAnswerCount = screen.word.incorrectAnswerCount,
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