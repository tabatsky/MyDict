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
    var incorrectAnswerCount: Int = 0
)
