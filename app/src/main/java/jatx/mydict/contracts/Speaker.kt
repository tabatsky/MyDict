package jatx.mydict.contracts

import jatx.mydict.domain.Language

interface Speaker {
    fun speak(text: String, language: Language, isForeign: Boolean)
}