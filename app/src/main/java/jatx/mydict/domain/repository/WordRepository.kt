package jatx.mydict.domain.repository

import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllByLanguage(language: Language): Flow<List<Word>>
    fun getStatsByLanguage(language: Language): Flow<Pair<Int, Int>>
    suspend fun getCountByLanguage(language: Language): Int
    suspend fun getAll(): List<Word>
    suspend fun insertReplaceList(list: List<Word>)
    suspend fun searchForWord(original: String, language: Language): Word?
    suspend fun addWord(word: Word)
    suspend fun editWord(word: Word)
    suspend fun deleteWord(word: Word)
}