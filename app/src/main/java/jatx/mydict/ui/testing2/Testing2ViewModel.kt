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
import kotlin.random.Random

@OptIn(InternalSerializationApi::class)
class Testing2ViewModel : BaseViewModel() {
    lateinit var language: Language

    private val wordList = arrayListOf<Word>()

    private var collectWordsJob: Job? = null
    private var collectStatsJob: Job? = null

    private val _currentWord: MutableLiveData<Word?> = MutableLiveData(null)
    val currentWord: LiveData<Word?> = _currentWord

    private val _currentAnswer = MutableLiveData("")
    val currentAnswer: LiveData<String> = _currentAnswer

    private val _stats = MutableLiveData(0 to 0)
    val stats: LiveData<Pair<Int, Int>> = _stats

    private var firstQuestionInitDone = false

    private val rnd = Random(137)

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
        collectStatsJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getStatsByLanguage(language).collect {
                    withContext(Dispatchers.Main) {
                        _stats.value = it
                    }
                }
            }
        }
    }

    fun stopJob() {
        collectWordsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        collectStatsJob?.let {
            if (!it.isCancelled) it.cancel()
        }
    }

    fun showNext() {
        val wordRange = wordList.indices.toList()
        val word = wordRange
            .sortedBy {
                -wordList[it].id
            }
            .mapIndexed { sortIndex, wordIndex ->
                val sortValue = wordList[wordIndex].let { word ->
                    10000 * word.correctedOrderByValue + sortIndex + rnd.nextInt(20) + rnd.nextDouble(1.0)
                }
                Triple(sortValue, sortIndex, wordIndex)
            }
            .sortedBy {
                it.first
            }
            .map {
                wordList[it.third]
            }
            .first()

        _currentAnswer.value = ""
        _currentWord.value = word
    }

    fun updateAnswer(answer: String) {
        if (currentAnswer.value != answer) {
            _currentAnswer.value = answer
        }
    }

    fun correctAnswer() {
        _currentWord.value?.let { word ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    word.correctAnswerCount += 1
                    word.orderByValue += 1
                    deps.wordRepository.editWord(word)
                }
            }
        }
    }

    fun speakCurrentWord() = speaker.speak(
        currentWord.value!!.original,
        currentWord.value!!.language,
        true
    )
}