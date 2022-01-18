package jatx.mydict.domain

enum class Language(
    val dbString: String,
    val rusString: String
) {
    ENGLISH("english", "Английский"),
    DEUTSCH("deutsch", "Немецкий"),
    JAPANESE("japanese", "Японский");

    companion object {
        fun getByDbString(dbString: String) = values()
            .find { it.dbString == dbString }
            ?: throw LanguageNotSupportedException()

        class LanguageNotSupportedException: Exception()
    }
}