package jatx.mydict.ui.testing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import jatx.mydict.domain.Language
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestingViewModel : BaseViewModel() {
    lateinit var language: Language

    private val originals = arrayListOf<String>()
    private val translations = arrayListOf<String>()

    private var collectJob: Job? = null

    private val _currentQuestion: MutableLiveData<CurrentQuestion?> = MutableLiveData(null)
    val currentQuestion: LiveData<CurrentQuestion?> = _currentQuestion

    fun startJob() {
        collectJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getAllByLanguage(language).collect { words ->
                    withContext(Dispatchers.Main) {
                        originals.clear()
                        translations.clear()
                        originals.addAll(words.map { it.original })
                        translations.addAll(words.map { it.translation })
                        withContext(Dispatchers.Main) {
                            showNext()
                        }
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

    fun showNext() {
        val range1 = originals.indices
        val range2 = 0..1

        val allWords = listOf(originals, translations)

        val indices1 = range1.shuffled().subList(0, 4)
        val indices2 = range2.shuffled()

        val words1 = allWords[indices2[0]]
        val words2 = allWords[indices2[1]]

        val question = words1[indices1[0]]

        val answers = arrayListOf<String>()
        indices1.forEach {
            answers.add(words2[it])
        }

        val correctAnswer = answers[0]
        val shuffledAnswers = answers.shuffled()
        val correctIndex = shuffledAnswers.indexOf(correctAnswer)

        val currentQuestion = CurrentQuestion(
            question,
            shuffledAnswers,
            correctIndex
        )

        _currentQuestion.value = currentQuestion
    }
}