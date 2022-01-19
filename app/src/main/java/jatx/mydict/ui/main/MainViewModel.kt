package jatx.mydict.ui.main

import jatx.mydict.domain.Language
import jatx.mydict.navigation.DictScreen
import jatx.mydict.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {
    fun selectLanguage(language: Language) {
        navigator.navigateTo(DictScreen(language))
    }

    fun loadData() = backuper.loadData()

    fun saveData() = backuper.saveData()
}