package jatx.mydict.ui.testing

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

    private val rnd = Random(137)

    fun startJob() {
        collectWordsJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deps.wordRepository.getAllByLanguage(language).collect { words ->
                    withContext(Dispatchers.Default) {
                        wordList.clear()
                        originals.clear()
                        translations.clear()
                        wordList.addAll(words)
                        originals.addAll(words.map { it.decapitalizedOriginal })
                        translations.addAll(words.map { it.decapitalizedTranslation })
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
        val wordRange = originals.indices.toList()
        val languageRange = 0..1

        val allWords = listOf(originals, translations)

        val wordIndices = wordRange
            .sortedBy {
                -wordList[it].id
            }
            .mapIndexed { sortIndex, wordIndex ->
                val sortValue = wordList[wordIndex].let { word ->
                    1000000 * word.orderByValue + sortIndex + rnd.nextInt(20) + rnd.nextDouble(1.0)
                }
                Triple(sortValue, sortIndex, wordIndex)
            }
            .sortedBy {
                it.first
            }
            .map {
                it.third
            }
            .subList(0, 4)
        val languageIndices = languageRange.shuffled()

        val foreignToRussian = languageIndices[0] == 0

        val words1 = allWords[languageIndices[0]]
        val words2 = allWords[languageIndices[1]]

        val questionWord = wordList[wordIndices[0]]
        val question = words1[wordIndices[0]]

        val answers = arrayListOf<String>()
        wordIndices.forEach {
            answers.add(words2[it])
        }

        val correctAnswer = answers[0]
        val shuffledAnswers = answers.shuffled()
        val correctIndex = shuffledAnswers.indexOf(correctAnswer)

        val currentQuestion = CurrentQuestion(
            questionWord,
            question,
            shuffledAnswers,
            correctIndex,
            foreignToRussian
        )

        _currentQuestion.value = currentQuestion
    }

    fun correctAnswer() {
        _currentQuestion.value?.questionWord?.let { word ->
            speak(word.original, word.language, true)
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
        mistake()
        _currentQuestion.value?.questionWord?.let { word ->
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    word.incorrectAnswerCount += 1
                    deps.wordRepository.editWord(word)
                }
            }
        }
    }

    fun speak(text: String, language: Language, isForeign: Boolean) =
        speaker.speak(text, language, isForeign)

    fun mistake() = mistaker.mistake()
}