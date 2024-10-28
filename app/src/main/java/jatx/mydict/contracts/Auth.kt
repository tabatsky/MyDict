package jatx.mydict.contracts

import com.google.firebase.auth.FirebaseUser

interface Auth {
    val user: FirebaseUser?

    fun loadAuth(onSuccess: (String, String) -> Unit)
    fun saveAuth(email: String, password: String)
    fun signIn(email: String, password: String)
    fun signUp(email: String, password: String)
}