package jatx.mydict.contracts

import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import kotlinx.serialization.Serializable

interface Navigator {
    fun navigateTo(screen: Screen)
    fun back()
}

sealed class Screen

object MainScreen: Screen()
data class DictScreen(val language: Language): Screen()

@Serializable
sealed class WordScreen: Screen()
@Serializable
data class AddWordScreen(val language: Language, val initialOrderByValue: Int): WordScreen()
@Serializable
data class EditWordScreen(val word: Word): WordScreen()

data class TestingScreen(val language: Language): Screen()

object AuthScreen: Screen()