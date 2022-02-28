package jatx.mydict.ui.dict

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jatx.mydict.R
import jatx.mydict.contracts.AddWordScreen
import jatx.mydict.contracts.EditWordScreen
import jatx.mydict.contracts.TestingScreen
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val MINIMUM_WORD_COUNT_FOR_TESTING = 4

class DictViewModel : BaseViewModel() {
    lateinit var language: Language

    private val _words = MutableLiveData<List<Word>>(listOf())
    val words: LiveData<List<Word>> = _words

    private var initialOrderByValue: Int = 0

    private var collectJob: Job? = null

    fun addWord() {
        navigator.navigateTo(AddWordScreen(language, initialOrderByValue))
    }

    fun editWord(word: Word) {
        navigator.navigateTo(EditWordScreen(word))
    }

    fun openTesting() = viewModelScope.launch {
        val count = deps.wordRepository.getCountByLanguage(language)
        withContext(Dispatchers.Main) {
            if (count < MINIMUM_WORD_COUNT_FOR_TESTING) {
                toasts.showToast(R.string.toast_minimum_word_count)
            } else {
                navigator.navigateTo(TestingScreen(language))
            }
        }
    }

    fun startJob() {
        collectJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getAllByLanguage(language).collect {
                    withContext(Dispatchers.Main) {
                        _words.value = it
                        initialOrderByValue = it.minOf { word -> word.orderByValue }
                    }
                }
            }
        }
    }

    fun stopJob() {
        collectJob?.let {
            if (!it.isCancelled) it.cancel()
        }
    }
}