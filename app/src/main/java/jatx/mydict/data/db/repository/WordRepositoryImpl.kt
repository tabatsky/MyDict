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
    override fun getAllByLanguage(language: Language) = appDatabase
        .wordDao()
        .getAllByLanguage(language.dbString)
        .map { list ->
            list.map { it.toModel() }
        }

    override suspend fun getCountByLanguage(language: Language) = appDatabase
        .wordDao()
        .getCountByLanguage(language.dbString)

    override suspend fun getAll() = appDatabase
        .wordDao()
        .getAll()
        .map { it.toModel() }

    override suspend fun insertReplaceList(list: List<Word>) = appDatabase
        .wordDao()
        .insertReplaceList(list.map { it.toEntity() })

    override suspend fun addWord(word: Word) = appDatabase
        .wordDao()
        .addWord(word.toEntity())

    override suspend fun editWord(word: Word) = appDatabase
        .wordDao()
        .editWord(word.toEntity())

    override suspend fun deleteWord(word: Word) = appDatabase
        .wordDao()
        .deleteWord(word.toEntity())
}