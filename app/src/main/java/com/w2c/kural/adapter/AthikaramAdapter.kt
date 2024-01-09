package com.w2c.kural.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.w2c.kural.databinding.ListItemChapterBinding
import com.w2c.kural.model.Chapter
import com.w2c.kural.utils.AdapterActions
import com.w2c.kural.utils.AthikaramClickListener

class AthikaramAdapter(
    private val tag: String,
    private val list: List<String>,
    private val callback: (pos: Int, action: AdapterActions) -> Unit
) :
    RecyclerView.Adapter<AthikaramAdapter.AthikaramViewHolder>() {
    inner class AthikaramViewHolder(val binding: ListItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                callback(layoutPosition, AdapterActions.ITEM_CLICK)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AthikaramViewHolder {
        val binding =
            ListItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AthikaramViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AthikaramAdapter.AthikaramViewHolder, position: Int) {
        val chapter = Chapter("$tag-${position + 1}", list[position])
        holder.binding.chapter = chapter
    }

    override fun getItemCount(): Int {
        return list.size
    }
}