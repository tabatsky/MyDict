package jatx.mydict.ui.testing2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import jatx.mydict.R
import jatx.mydict.databinding.Testing2FragmentBinding
import jatx.mydict.ui.testing.TestingFragmentArgs
import kotlinx.serialization.InternalSerializationApi
import kotlin.getValue

@OptIn(InternalSerializationApi::class)
class Testing2Fragment : Fragment() {

    private val args by navArgs<TestingFragmentArgs>()


    private val viewModel: Testing2ViewModel by lazy {
        ViewModelProvider(this)[Testing2ViewModel::class.java].apply {
            language = args.language
        }
    }

    private lateinit var testing2FragmentBinding: Testing2FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        testing2FragmentBinding = Testing2FragmentBinding.inflate(inflater, container, false)

        with(testing2FragmentBinding) {
            btnNextQuestion.setOnClickListener {
                viewModel.showNext()
            }
            etAnswer.addTextChangedListener(
                onTextChanged = { newText, _, _, _ ->
                    viewModel.updateAnswer(newText.toString())
                },
                afterTextChanged = {
                    etAnswer.setSelection(etAnswer.text.length)
                }
            )
            btnHint.setOnClickListener {
                viewModel.currentAnswer.value?.let { currentAnswer ->
                    viewModel.currentWord.value?.let {
                        if (it.matches(currentAnswer)) {
                            return@setOnClickListener
                        } else {
                            val commonLength = it.commonStartLength(currentAnswer)
                            val newAnswer = it.decapitalizedOriginal.substring(0, commonLength + 1)
                            viewModel.updateAnswer(newAnswer)
                        }
                    }
                }
            }
        }

        return testing2FragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.currentWord.observe(viewLifecycleOwner) { currentWord ->
            testing2FragmentBinding.tvQuestion.text = currentWord?.decapitalizedTranslation
        }
        viewModel.currentAnswer.observe(viewLifecycleOwner) { currentAnswer ->
            updateAnswerField(currentAnswer)
        }
        viewModel.startJob()
    }

    private fun updateAnswerField(answer: String) {
        with(testing2FragmentBinding.etAnswer) {
            setText(answer)
            viewModel.currentWord.value?.let {
                if (it.matches(answer)) {
                    isEnabled = false
                    setBackgroundResource(R.color.green)
                    viewModel.speakCurrentWord()
                    viewModel.correctAnswer()
                } else {
                    isEnabled = true
                    if (it.startsWith(answer)) {
                        setBackgroundResource(R.color.blue)
                    } else {
                        setBackgroundResource(R.color.red)
                    }
                }
            }
        }
    }
}