package jatx.mydict.domain.repository

import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllByLanguage(language: Language): Flow<List<Word>>
    suspend fun addWord(word: Word)
}