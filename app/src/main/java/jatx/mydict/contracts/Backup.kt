package jatx.mydict.backup

import jatx.mydict.domain.models.Word

interface Backup {
    fun loadData()
    fun saveData()
}

data class BackupData(
    val words: List<Word>
)