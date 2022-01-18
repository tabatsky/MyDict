package jatx.mydict.navigation

import jatx.mydict.domain.Language

interface Navigator {
    fun navigateTo(screen: Screen)
    fun back()
}

sealed class Screen

object MainScreen: Screen()
class DictScreen(val language: Language): Screen()
class AddWordScreen(val language: Language): Screen()
