package jatx.mydict.ui.testing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestingViewModel : BaseViewModel() {
    lateinit var language: Language

    private val wordList = arrayListOf<Word>()

    private val originals = arrayListOf<String>()
    private val translations = arrayListOf<String>()

    private var collectWordsJob: Job? = null
    private var collectStatsJob: Job? = null

    private val _currentQuestion: MutableLiveData<CurrentQuestion?> = MutableLiveData(null)
    val currentQuestion: LiveData<CurrentQuestion?> = _currentQuestion

    private val _stats = MutableLiveData(0 to 0)
    val stats: LiveData<Pair<Int, Int>> = _stats

    private var firstQuestionInitDone = false

    fun startJob() {
        collectWordsJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getAllByLanguage(language).collect { words ->
                    withContext(Dispatchers.Default) {
                        wordList.clear()
                        originals.clear()
                        translations.clear()
                        wordList.addAll(words)
                        originals.addAll(words.map { it.original })
                        translations.addAll(words.map { it.translation })
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
        val range1 = originals.indices
        val range2 = 0..1

        val allWords = listOf(originals, translations)

        val indices1 = range1.shuffled().sortedBy { wordList[it].orderByValue } .subList(0, 4)
        val indices2 = range2.shuffled()

        val words1 = allWords[indices2[0]]
        val words2 = allWords[indices2[1]]

        val questionWord = wordList[indices1[0]]
        val question = words1[indices1[0]]

        val answers = arrayListOf<String>()
        indices1.forEach {
            answers.add(words2[it])
        }

        val correctAnswer = answers[0]
        val shuffledAnswers = answers.shuffled()
        val correctIndex = shuffledAnswers.indexOf(correctAnswer)

        val currentQuestion = CurrentQuestion(
            questionWord,
            question,
            shuffledAnswers,
            correctIndex
        )

        _currentQuestion.value = currentQuestion
    }

    fun correctAnswer() {
        _currentQuestion.value?.questionWord?.let { word ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    word.correctAnswerCount += 1
                    word.orderByValue += 1
                    deps.wordRepository.editWord(word)
                }
            }
        }
    }

    fun incorrectAnswer() {
        _currentQuestion.value?.questionWord?.let { word ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    word.incorrectAnswerCount += 1
                    deps.wordRepository.editWord(word)
                }
            }
        }
    }
}