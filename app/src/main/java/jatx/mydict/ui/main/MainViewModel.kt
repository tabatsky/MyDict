package jatx.mydict.ui.main

import jatx.mydict.contracts.AuthScreen
import jatx.mydict.contracts.DictScreen
import jatx.mydict.domain.Language
import jatx.mydict.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    fun selectLanguage(language: Language) {
        navigator.navigateTo(DictScreen(language))
    }

    fun auth() = navigator.navigateTo(AuthScreen)

    fun loadData() = backup.loadData()

    fun saveData() = backup.saveData()
}