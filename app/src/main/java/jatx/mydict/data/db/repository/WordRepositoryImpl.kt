package jatx.mydict.data.db.repository

import jatx.mydict.data.db.AppDatabase
import jatx.mydict.data.db.entity.toEntity
import jatx.mydict.data.db.entity.toModel
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import jatx.mydict.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class WordRepositoryImpl(private val appDatabase: AppDatabase): WordRepository {
    override fun getAllByLanguage(language: Language) = appDatabase
        .wordDao()
        .getAllByLanguage(language.dbString)
        .map { list ->
            list.map { it.toModel() }
        }

    override fun getStatsByLanguage(language: Language): Flow<Pair<Int, Int>> {
        val correctFlow = appDatabase.wordDao().getCorrectAnswerSumByLanguage(language.dbString)
        val incorrectFlow = appDatabase.wordDao().getIncorrectAnswerSumByLanguage(language.dbString)
        return correctFlow.combine(incorrectFlow) { correct, incorrect ->
            correct to incorrect
        }
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

    override suspend fun searchForWord(original: String, language: Language): Word? {
        val list = appDatabase
            .wordDao()
            .searchForWord(original, language.dbString)
        return if (list.isEmpty()) {
            null
        } else {
            list[0].toModel()
        }
    }

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