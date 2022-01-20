package jatx.mydict.contracts

import jatx.mydict.domain.models.Word

interface Backup {
    fun loadData()
    fun saveData()
}

data class BackupData(
    val words: List<Word>
)