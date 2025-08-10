package jatx.mydict.ui.testing2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class Testing2ViewModel : BaseViewModel() {
    lateinit var language: Language

    private val wordList = arrayListOf<Word>()

    private var collectWordsJob: Job? = null

    private val _currentWord: MutableLiveData<Word?> = MutableLiveData(null)
    val currentWord: LiveData<Word?> = _currentWord

    private var firstQuestionInitDone = false

    fun startJob() {
        collectWordsJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getAllByLanguage(language).collect { words ->
                    withContext(Dispatchers.Default) {
                        wordList.clear()
                        wordList.addAll(words)
                        withContext(Dispatchers.Main) {
                            if (!firstQuestionInitDone) {
                                showNext()
                                firstQuestionInitDone = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun showNext() {
        val word = wordList.shuffled().firstOrNull()

        _currentWord.value = word
    }
}