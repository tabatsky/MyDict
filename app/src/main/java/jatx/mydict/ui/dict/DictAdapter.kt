package jatx.mydict.ui.dict

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jatx.mydict.R
import jatx.mydict.databinding.ItemDictBinding
import jatx.mydict.domain.models.Word

class DictAdapter: ListAdapter<DictAdapter.WordWithPosition, DictAdapter.VH>(WordDiffUtil()) {

    var onWordClick: (Word) -> Unit = {}

    fun updateWords(words: List<Word>) {
        submitList(words.mapIndexed { index, word ->
            WordWithPosition(index, word)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dict, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val word = getItem(position)
        holder.bindTo(position, word)
    }

    inner class VH(private val v: View): RecyclerView.ViewHolder(v) {
        private val binding = ItemDictBinding.bind(v)

        fun bindTo(position: Int, wordWithPosition: WordWithPosition) {
            with (binding) {
                with (wordWithPosition) {
                    tvOriginal.text = "${position + 1}. ${word.original}"
                    tvTranslation.text = word.translation
                }
            }
            v.setOnClickListener {
                onWordClick(wordWithPosition.word)
            }
        }
    }

    class WordDiffUtil: DiffUtil.ItemCallback<WordWithPosition>() {
        override fun areItemsTheSame(oldItem: WordWithPosition, newItem: WordWithPosition) =
            (oldItem == newItem)

        override fun areContentsTheSame(oldItem: WordWithPosition, newItem: WordWithPosition) =
            (oldItem == newItem)

    }

    data class WordWithPosition(
        val position: Int,
        val word: Word
    )
}