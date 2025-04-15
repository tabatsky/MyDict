package jatx.mydict.ui.testing

import jatx.mydict.domain.models.Word

data class CurrentQuestion(
    val questionWord: Word,
    val question: String,
    val answers: List<String>,
    val correctIndex: Int,
    val foreignToRussian: Boolean
)