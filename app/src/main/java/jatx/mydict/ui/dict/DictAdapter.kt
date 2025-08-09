package jatx.mydict.ui.dict

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jatx.mydict.R
import jatx.mydict.databinding.ItemDictBinding
import jatx.mydict.domain.Language
import jatx.mydict.domain.models.Word
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class DictAdapter(
    private val language: Language
): ListAdapter<DictAdapter.WordWithPosition, DictAdapter.VH>(WordDiffUtil()) {

    var onWordClick: (Word) -> Unit = {}

    fun updateWords(words: List<Word>) {
        submitList(words.mapIndexed { index, word ->
            WordWithPosition(index, word, language)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dict, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val word = getItem(position)
        holder.bindTo(word)
    }

    inner class VH(private val v: View): RecyclerView.ViewHolder(v) {
        private val binding = ItemDictBinding.bind(v)

        fun bindTo(wordWithPosition: WordWithPosition) {
            binding.wordWithPosition = wordWithPosition
            v.setOnClickListener {
                onWordClick(wordWithPosition.word)
            }
        }
    }

    class WordDiffUtil: DiffUtil.ItemCallback<WordWithPosition>() {
        override fun areItemsTheSame(oldItem: WordWithPosition, newItem: WordWithPosition) =
            (oldItem.word.original == newItem.word.original)

        override fun areContentsTheSame(oldItem: WordWithPosition, newItem: WordWithPosition) =
            (oldItem == newItem)

    }

    data class WordWithPosition(
        val position: Int,
        val word: Word,
        val language: Language
    ) {
        val originalWithPosition =  "${position + 1}. ${word.actualOriginal(language)}"
        val translation = word.actualTranslation
    }
}