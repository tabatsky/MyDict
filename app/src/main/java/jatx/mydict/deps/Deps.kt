package jatx.mydict.deps

import jatx.mydict.domain.repository.WordRepository

interface Deps {
    val wordRepository: WordRepository
}