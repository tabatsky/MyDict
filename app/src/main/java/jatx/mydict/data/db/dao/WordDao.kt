package jatx.mydict.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import jatx.mydict.data.db.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE language=:language ORDER BY original")
    fun getAllByLanguage(language: String): Flow<List<WordEntity>>

    @Insert
    suspend fun addWord(wordEntity: WordEntity)
}