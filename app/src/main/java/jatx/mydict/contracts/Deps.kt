package jatx.mydict.contracts

import jatx.mydict.domain.repository.WordRepository

interface Deps {
    val wordRepository: WordRepository
}