package jatx.mydict.ui.dict

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.navigation.AddWordScreen
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DictViewModel : BaseViewModel() {
    lateinit var language: Language

    private val _words = MutableLiveData<List<Word>>(listOf())
    val words: LiveData<List<Word>> = _words

    private var collectJob: Job? = null

    fun addWord() {
        navigator.navigateTo(AddWordScreen(language))
    }

    fun startJob() {
        collectJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getAllByLanguage(language).collect {
                    withContext(Dispatchers.Main) {
                        _words.value = it
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