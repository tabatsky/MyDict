package jatx.mydict.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.dict.DictAdapter
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@BindingAdapter("itemsWithScrollPosition")
fun <T> setItems(rv: RecyclerView, itemsWithScrollPosition: Pair<List<T>, Int>) {
    (rv.adapter as? DictAdapter)?.updateWords(
        itemsWithScrollPosition.first.map { it as Word },
        itemsWithScrollPosition.second
    )
}