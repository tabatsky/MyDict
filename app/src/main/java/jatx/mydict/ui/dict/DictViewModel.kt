package jatx.mydict.ui.dict

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import jatx.mydict.R
import jatx.mydict.contracts.AddWordScreen
import jatx.mydict.contracts.EditWordScreen
import jatx.mydict.contracts.Testing2Screen
import jatx.mydict.contracts.TestingScreen
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi

const val MINIMUM_WORD_COUNT_FOR_TESTING = 4

@OptIn(InternalSerializationApi::class)
class DictViewModel : BaseViewModel() {
    lateinit var language: Language

    private val _words = MutableLiveData<List<Word>>(listOf())
    val words: LiveData<List<Word>> = _words

    private val _wordCount = MutableLiveData(0)
    val wordCount: LiveData<Int> = _wordCount

    val searchText = MutableLiveData("")

    private var initialOrderByValue: Int = 0

    private var collectJob: Job? = null

    var sortBy = SortBy.BY_ORIGINAL

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

    fun openTesting2() = viewModelScope.launch {
        val count = deps.wordRepository.getCountByLanguage(language)
        withContext(Dispatchers.Main) {
            if (count < 1) {
                toasts.showToast(R.string.toast_at_least_one_word)
            } else {
                navigator.navigateTo(Testing2Screen(language))
            }
        }
    }

    fun startJob() {
        val searchTextFlow = searchText.asFlow()
        collectJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getAllByLanguage(language)
                    .combine(searchTextFlow) { words, searchTextString ->
                        words.filter {
                            it.original.lowercase()
                                .contains(searchTextString.lowercase()) ||
                                    it.translation.lowercase()
                                        .contains(searchTextString.lowercase())
                        }
                    }.collect {
                        withContext(Dispatchers.Main) {
                            _words.value = it.sortedBy { word ->
                                when (sortBy) {
                                    SortBy.BY_ORIGINAL -> {
                                        word.original
                                            .replace("der ", "", ignoreCase = true)
                                            .replace("die ", "", ignoreCase = true)
                                            .replace("das ", "", ignoreCase = true)
                                    }
                                    SortBy.BY_TRANSLATION -> {
                                        word.translation
                                    }
                                    SortBy.BY_ID_DESC -> {
                                        (999999999 - word.id).toString()
                                    }
                                }
                            }
                            _wordCount.value = it.size
                            initialOrderByValue = it.minOfOrNull { word -> word.orderByValue } ?: 0
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

    fun toggleSortByOriginal() {
        stopJob()
        sortBy = if (sortBy == SortBy.BY_ORIGINAL) SortBy.BY_ID_DESC else SortBy.BY_ORIGINAL
        startJob()
    }

    fun toggleSortByTranslation() {
        stopJob()
        sortBy = if (sortBy == SortBy.BY_TRANSLATION) SortBy.BY_ID_DESC else SortBy.BY_TRANSLATION
        startJob()
    }
}

enum class SortBy {
    BY_ORIGINAL, BY_TRANSLATION, BY_ID_DESC
}