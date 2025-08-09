package jatx.mydict.domain.models

import jatx.mydict.domain.Language
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi @Serializable
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

    fun actualOriginal(language: Language) = if (language == Language.JAPANESE) {
        original
    } else {
        original.replaceFirstChar { it.lowercase() }
    }

    val actualTranslation = translation.replaceFirstChar { it.lowercase() }
    override fun hashCode(): Int {
        var result = id
        result = 31 * result + original.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + translation.hashCode()
        result = 31 * result + language.hashCode()
        return result
    }
}