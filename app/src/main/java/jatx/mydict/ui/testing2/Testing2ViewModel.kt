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

    private val _currentAnswer = MutableLiveData("")
    val currentAnswer: LiveData<String> = _currentAnswer

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

        _currentAnswer.value = ""
        _currentWord.value = word
    }

    fun updateAnswer(answer: String) {
        if (currentAnswer.value != answer) {
            _currentAnswer.value = answer
        }
    }

    fun speakCurrentWord() = speaker.speak(
        currentWord.value!!.original,
        currentWord.value!!.language,
        true
    )
}