package jatx.mydict.ui.auth

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jatx.mydict.databinding.AuthFragmentBinding

class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var authFragmentBinding: AuthFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authFragmentBinding = AuthFragmentBinding.inflate(inflater, container, false)

        authFragmentBinding.lifecycleOwner = this
        authFragmentBinding.viewmodel = viewModel

        authFragmentBinding.btnSignIn.setOnClickListener {
            val email = authFragmentBinding.etLogin.text.toString()
            val password = authFragmentBinding.etPassword.text.toString()
            viewModel.auth.signIn(email, password)
        }

        authFragmentBinding.btnSignUp.setOnClickListener {
            val email = authFragmentBinding.etLogin.text.toString()
            val password = authFragmentBinding.etPassword.text.toString()
            viewModel.auth.signUp(email, password)
        }

        return authFragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.auth.loadAuth { email, password ->
            authFragmentBinding.etLogin.setText(email)
            authFragmentBinding.etPassword.setText(password)
        }
    }
}