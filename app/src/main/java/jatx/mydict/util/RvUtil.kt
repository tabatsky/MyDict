package jatx.mydict.util

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import jatx.mydict.domain.models.Word
import jatx.mydict.ui.dict.DictAdapter
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@BindingAdapter("items")
fun <T> setItems(rv: RecyclerView, list: List<T>) {
    (rv.adapter as? DictAdapter)?.updateWords(list.map { it as Word })
}