package jatx.mydict.domain.models

import jatx.mydict.domain.Language

data class Word(
    val id: Int = 0,
    val original: String,
    val comment: String,
    val translation: String,
    val language: Language
)
