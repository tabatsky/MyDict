package jatx.mydict.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word

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
    val language: String
)

fun WordEntity.toModel() = Word(
    id = id,
    original = original,
    comment = comment,
    translation = translation,
    language = Language.getByDbString(language)
)

fun Word.toEntity() = WordEntity(
    id = id,
    original = original,
    comment = comment,
    translation = translation,
    language = language.dbString
)