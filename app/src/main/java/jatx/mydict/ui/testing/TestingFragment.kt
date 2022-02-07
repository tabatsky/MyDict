package jatx.mydict.ui.testing

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.navArgs
import jatx.mydict.R
import jatx.mydict.databinding.TestingFragmentBinding
import jatx.mydict.domain.Language
import jatx.mydict.ui.base.BaseFragment
import java.lang.IllegalArgumentException

class TestingFragment : BaseFragment() {

    private val args by navArgs<TestingFragmentArgs>()

    private lateinit var testingFragmentBinding: TestingFragmentBinding
    private lateinit var viewModel: TestingViewModel
    private lateinit var allTV: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        testingFragmentBinding = TestingFragmentBinding.inflate(inflater, container, false)

        with(testingFragmentBinding) {
            allTV = listOf(tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4)

            btnNextQuestion.setOnClickListener {
                viewModel.showNext()
            }
        }

        return testingFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[TestingViewModel::class.java]
        viewModel.injectFromActivity(requireActivity())

        viewModel.language = args.language
    }

    override fun onStart() {
        super.onStart()
        viewModel.currentQuestion.observe(viewLifecycleOwner) { currentQuestion ->
            currentQuestion?.let { currentQuestion ->
                testingFragmentBinding.tvQuestion.text = currentQuestion.question
                currentQuestion.answers.forEachIndexed { index, s ->
                    allTV[index].setBackgroundResource(R.color.blue)
                    allTV[index].text = s
                    allTV[index].setOnClickListener {
                        if (index == currentQuestion.correctIndex) {
                            allTV[index].setBackgroundResource(R.color.green)
                            viewModel.correctAnswer()
                        } else {
                            allTV[index].setBackgroundResource(R.color.red)
                            allTV[currentQuestion.correctIndex].setBackgroundResource(R.color.green)
                            viewModel.incorrectAnswer()
                        }

                        allTV.forEach { it.setOnClickListener {  } }
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