package jatx.mydict.data.db.repository

import jatx.mydict.data.db.AppDatabase
import jatx.mydict.data.db.entity.toEntity
import jatx.mydict.data.db.entity.toModel
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WordRepositoryImpl(private val appDatabase: AppDatabase): WordRepository {
    override fun getAllByLanguage(language: Language): Flow<List<Word>> = appDatabase
        .wordDao()
        .getAllByLanguage(language.dbString)
        .map { list ->
            list.map { it.toModel() }
        }

    override suspend fun addWord(word: Word) = appDatabase
        .wordDao()
        .addWord(word.toEntity())
}