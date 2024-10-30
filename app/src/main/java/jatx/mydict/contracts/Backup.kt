package jatx.mydict.contracts

import jatx.mydict.domain.models.Word

interface Backup {
    fun loadData()
    fun saveData()
    fun loadDataFromFirestore()
    fun saveDataToFirestore()
}

data class BackupData(
    val words: List<Word>
)