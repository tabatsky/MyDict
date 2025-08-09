package jatx.mydict.ui.testing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import jatx.mydict.R
import jatx.mydict.databinding.TestingFragmentBinding
import jatx.mydict.ui.base.BaseFragment
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class TestingFragment : BaseFragment() {

    private val args by navArgs<TestingFragmentArgs>()

    private val viewModel: TestingViewModel by lazy {
        ViewModelProvider(this)[TestingViewModel::class.java].apply {
            language = args.language
        }
    }

    private lateinit var testingFragmentBinding: TestingFragmentBinding
    private lateinit var allTV: List<TextView>
    private lateinit var allIV: List<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        testingFragmentBinding = TestingFragmentBinding.inflate(inflater, container, false)

        with(testingFragmentBinding) {
            allTV = listOf(tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4)
            allIV = listOf(ivAnswer1, ivAnswer2, ivAnswer3, ivAnswer4)

            btnNextQuestion.setOnClickListener {
                viewModel.showNext()
            }
        }

        return testingFragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.currentQuestion.observe(viewLifecycleOwner) { currentQuestion ->
            currentQuestion?.let { current ->
                testingFragmentBinding.tvQuestion.text = current.question
                testingFragmentBinding.ivQuestion.setOnClickListener {
                    viewModel.speak(current.question, current.questionWord.language, current.foreignToRussian)
                }
                current.answers.forEachIndexed { index, s ->
                    allTV[index].setBackgroundResource(R.color.blue)
                    allTV[index].text = s
                    allTV[index].setOnClickListener {
                        if (index == current.correctIndex) {
                            allTV[index].setBackgroundResource(R.color.green)
                            viewModel.correctAnswer()
                        } else {
                            allTV[index].setBackgroundResource(R.color.red)
                            allTV[current.correctIndex].setBackgroundResource(R.color.green)
                            viewModel.incorrectAnswer()
                        }

                        allTV.forEach { it.setOnClickListener {  } }
                    }
                    allIV[index].setOnClickListener {
                        viewModel.speak(s, current.questionWord.language, !current.foreignToRussian)
                    }
                }
            }
        }
        viewModel.stats.observe(viewLifecycleOwner) {
            testingFragmentBinding.tvStatsCorrect.text = it.first.toString()
            testingFragmentBinding.tvStatsIncorrect.text = it.second.toString()
        }
        viewModel.startJob()
    }

    override fun onStop() {
        viewModel.stopJob()
        super.onStop()
    }

}