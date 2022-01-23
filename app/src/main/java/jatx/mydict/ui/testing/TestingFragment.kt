package jatx.mydict.ui.testing

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import jatx.mydict.R
import jatx.mydict.databinding.TestingFragmentBinding
import jatx.mydict.domain.Language
import jatx.mydict.ui.base.BaseFragment
import java.lang.IllegalArgumentException

class TestingFragment : BaseFragment() {

    companion object {
        private val KEY_LANGUAGE = "language"

        fun newInstance(language: Language): TestingFragment {
            val testingFragment = TestingFragment()
            testingFragment.arguments = bundleOf(KEY_LANGUAGE to language)
            return testingFragment
        }
    }

    private lateinit var testingFragmentBinding: TestingFragmentBinding
    private lateinit var viewModel: TestingViewModel
    private lateinit var allTV: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val language = requireArguments().getSerializable(KEY_LANGUAGE)
                as? Language ?: throw IllegalArgumentException()

        setTitle(getString(R.string.title_testing) + language.rusString)
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

        val language = requireArguments().getSerializable(KEY_LANGUAGE)
                as? Language ?: throw IllegalArgumentException()

        viewModel.language = language
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
                        } else {
                            allTV[index].setBackgroundResource(R.color.red)
                            allTV[currentQuestion.correctIndex].setBackgroundResource(R.color.green)
                        }

                        allTV.forEach { it.setOnClickListener {  } }
                    }
                }
            }
        }
        viewModel.startJob()
    }

    override fun onStop() {
        viewModel.stopJob()
        super.onStop()
    }

}