package jatx.mydict.navigation

import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word

interface Navigator {
    fun navigateTo(screen: Screen)
    fun back()
}

sealed class Screen

object MainScreen: Screen()
class DictScreen(val language: Language): Screen()

sealed class WordScreen: Screen()
class AddWordScreen(val language: Language): WordScreen()
class EditWordScreen(val word: Word): WordScreen()