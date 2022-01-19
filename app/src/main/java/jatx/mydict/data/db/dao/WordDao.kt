package jatx.mydict.data.db.dao

import androidx.room.*
import jatx.mydict.data.db.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE language=:language ORDER BY original")
    fun getAllByLanguage(language: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words ORDER BY id")
    suspend fun getAll(): List<WordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplaceList(list: List<WordEntity>)

    @Insert
    suspend fun addWord(wordEntity: WordEntity)

    @Update
    suspend fun editWord(wordEntity: WordEntity)

    @Delete
    suspend fun deleteWord(wordEntity: WordEntity)
}