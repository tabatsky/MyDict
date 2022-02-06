package jatx.mydict.ui.dict

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jatx.mydict.R
import jatx.mydict.databinding.ItemDictBinding
import jatx.mydict.domain.models.Word

class DictAdapter: RecyclerView.Adapter<DictAdapter.VH>() {

    private val words: ArrayList<Word> = arrayListOf()
    var onWordClick: (Word) -> Unit = {}

    fun updateWords(words: List<Word>) {
        this.words.clear()
        this.words.addAll(words)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dict, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val word = words[position]
        with (holder.binding) {
            tvOriginal.text = "${position + 1}. ${word.original}"
            tvTranslation.text = word.translation
        }
        holder.v.setOnClickListener {
            onWordClick(word)
        }
    }

    override fun getItemCount() = words.size

    class VH(val v: View): RecyclerView.ViewHolder(v) {
        val binding = ItemDictBinding.bind(v)
    }
}