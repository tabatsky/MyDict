package jatx.mydict.ui.addword

import androidx.lifecycle.viewModelScope
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddWordViewModel : BaseViewModel() {
    lateinit var language: Language

    fun addWord(original: String, comment: String, translation: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val word = Word(
                original = original,
                comment = comment,
                translation = translation,
                language = language
            )
            deps.wordRepository.addWord(word)
        }
        withContext(Dispatchers.Main) {
            navigator.back()
        }
    }
}