package jatx.mydict.domain.models

import jatx.mydict.domain.Language
import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val id: Int = 0,
    val original: String,
    val comment: String,
    val translation: String,
    val language: Language,
    var correctAnswerCount: Int = 0,
    var incorrectAnswerCount: Int = 0,
    var orderByValue: Int
) {
    override fun equals(other: Any?): Boolean {
        return other is Word
                && other.id ==id
                && other.original == original
                && other.comment == comment
                && other.translation == translation
                && other.language == language
    }
}