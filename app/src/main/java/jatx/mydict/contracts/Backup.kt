package jatx.mydict.contracts

import jatx.mydict.domain.models.Word
import kotlinx.serialization.InternalSerializationApi

interface Backup {
    fun loadData()
    fun saveData()
    fun loadDataFromFirestore()
    fun saveDataToFirestore()
}

@OptIn(InternalSerializationApi::class)
data class BackupData(
    val words: List<Word>
)