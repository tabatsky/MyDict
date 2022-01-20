package jatx.mydict.ui.testing

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jatx.mydict.R
import jatx.mydict.databinding.TestingFragmentBinding
import jatx.mydict.domain.Language
import jatx.mydict.ui.base.BaseFragment
import jatx.mydict.ui.dict.DictFragment

class TestingFragment : BaseFragment() {

    companion object {
        private lateinit var language: Language

        fun newInstance(language: Language): TestingFragment {
            this.language = language
            return TestingFragment()
        }
    }

    private lateinit var testingFragmentBinding: TestingFragmentBinding
    private lateinit var viewModel: TestingViewModel
    private lateinit var allTV: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setTitle(getString(R.string.title_testing))
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