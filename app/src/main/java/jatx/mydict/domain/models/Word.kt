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
    val decapitalizedOriginal: String
        get() = if (language == Language.JAPANESE) {
            original
        } else {
            original.replaceFirstChar { it.lowercase() }
        }

    val decapitalizedTranslation = translation.replaceFirstChar { it.lowercase() }

    fun matches(answer: String) = if (language == Language.JAPANESE)  {
        original == answer
    } else {
        original.lowercase() == answer.lowercase()
    }

    fun startsWith(answer: String) = if (language == Language.JAPANESE)  {
        original.startsWith(answer)
    } else {
        original.lowercase().startsWith(answer.lowercase())
    }

    fun commonStartLength(answer: String): Int {
        var n = 0
        while (n < original.length && n < answer.length) {
            val substr = answer.substring(0, n + 1)
            if (matches(substr)) return (n + 1)
            if (startsWith(substr)) {
                n = n + 1
            } else {
                break
            }
        }
        return n
    }

    override fun equals(other: Any?): Boolean {
        return other is Word
                && other.id ==id
                && other.original == original
                && other.comment == comment
                && other.translation == translation
                && other.language == language
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + original.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + translation.hashCode()
        result = 31 * result + language.hashCode()
        return result
    }

    val correctedOrderByValue: Float
        get() = correctAnswerCount - 10.0f * (incorrectAnswerCount + 1) / (correctAnswerCount + 1)
}