@file:OptIn(InternalSerializationApi::class)

package jatx.mydict.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import kotlinx.serialization.InternalSerializationApi

@Entity(
    tableName = "words",
    indices = [
        Index("original"),
        Index("translation")
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val original: String,
    val comment: String,
    val translation: String,
    val language: String,
    @ColumnInfo(defaultValue = "0") val correctAnswerCount: Int,
    @ColumnInfo(defaultValue = "0") val incorrectAnswerCount: Int,
    @ColumnInfo(defaultValue = "0") val orderByValue: Int
)

fun WordEntity.toModel() = Word(
    id = id,
    original = original,
    comment = comment,
    translation = translation,
    language = Language.getByDbString(language),
    correctAnswerCount = correctAnswerCount,
    incorrectAnswerCount = incorrectAnswerCount,
    orderByValue = orderByValue
)

fun Word.toEntity() = WordEntity(
    id = id,
    original = original,
    comment = comment,
    translation = translation,
    language = language.dbString,
    correctAnswerCount = correctAnswerCount,
    incorrectAnswerCount = incorrectAnswerCount,
    orderByValue = orderByValue
)