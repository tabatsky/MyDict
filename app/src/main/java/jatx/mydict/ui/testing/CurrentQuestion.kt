package jatx.mydict.ui.testing

data class CurrentQuestion(
    val question: String,
    val answers: List<String>,
    val correctIndex: Int
)