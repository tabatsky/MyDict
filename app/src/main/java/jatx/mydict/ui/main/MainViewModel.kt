package jatx.mydict.ui.main

import jatx.mydict.contracts.AuthScreen
import jatx.mydict.contracts.DictScreen
import jatx.mydict.domain.Language
import jatx.mydict.ui.base.BaseViewModel
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class MainViewModel : BaseViewModel() {
    fun selectLanguage(language: Language) {
        navigator.navigateTo(DictScreen(language))
    }

    fun auth() = navigator.navigateTo(AuthScreen)

    fun loadData() = backup.loadData()

    fun saveData() = backup.saveData()

    fun loadDataFromFirestore() = backup.loadDataFromFirestore()

    fun saveDataToFirestore() = backup.saveDataToFirestore()
}