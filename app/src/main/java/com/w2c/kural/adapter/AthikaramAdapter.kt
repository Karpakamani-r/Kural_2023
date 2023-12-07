package com.w2c.kural.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.w2c.kural.databinding.ListItemChapterBinding
import com.w2c.kural.model.Chapter
import com.w2c.kural.utils.AthikaramClickListener

class AthikaramAdapter(val tag: String, val list: List<String>, val listener: AthikaramClickListener) :
    RecyclerView.Adapter<AthikaramAdapter.AthikaramViewHolder>() {
    class AthikaramViewHolder(val binding: ListItemChapterBinding, val listener: AthikaramClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AthikaramViewHolder {
        val binding =
            ListItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AthikaramViewHolder(
            binding, listener
        )
    }

    override fun onBindViewHolder(holder: AthikaramAdapter.AthikaramViewHolder, position: Int) {
        val chapter = Chapter("$tag-${position + 1}", list[position])
        holder.binding.chapter = chapter
    }

    override fun getItemCount(): Int {
        return list.size
    }
}