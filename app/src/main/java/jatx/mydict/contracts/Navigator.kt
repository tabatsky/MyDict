package jatx.mydict.contracts

import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

interface Navigator {
    fun navigateTo(screen: Screen)
    fun back()
}

sealed class Screen

object MainScreen: Screen()
data class DictScreen(val language: Language): Screen()

@InternalSerializationApi @Serializable
sealed class WordScreen: Screen()
@InternalSerializationApi @Serializable
data class AddWordScreen(val language: Language, val initialOrderByValue: Int): WordScreen()
@InternalSerializationApi @Serializable
data class EditWordScreen(val word: Word): WordScreen()

@InternalSerializationApi @Serializable
data class TestingScreen(val language: Language): Screen()

@InternalSerializationApi @Serializable
data class Testing2Screen(val language: Language): Screen()

@InternalSerializationApi @Serializable
object AuthScreen: Screen()