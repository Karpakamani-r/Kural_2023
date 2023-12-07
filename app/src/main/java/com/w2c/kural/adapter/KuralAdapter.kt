package com.w2c.kural.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.w2c.kural.R
import com.w2c.kural.adapter.KuralAdapter.KuralViewHolder
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.ListItemKuralBinding
import com.w2c.kural.utils.OnItemClickListener
import java.util.concurrent.Executors


class KuralAdapter(private val kuralList: List<Kural>, private val callback: OnItemClickListener) :
    RecyclerView.Adapter<KuralViewHolder>() {
    private var isFromFavourite = false
    fun setFromFavourite() {
        isFromFavourite = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KuralViewHolder {
        val listBinding =
            ListItemKuralBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KuralViewHolder(listBinding)
    }

    override fun onBindViewHolder(holder: KuralViewHolder, position: Int) {
        val kural = kuralList[position]
        holder.kuralListBinding.tvLine1.text = kural.line1
        holder.kuralListBinding.tvLine2.text = kural.line2
        val kuralNoTxt = holder.kuralListBinding.root.context.getString(R.string.kural_no)
        holder.kuralListBinding.tvKuralNo.text = String.format(kuralNoTxt, kural.number)
    }

    override fun getItemCount(): Int {
        return kuralList.size
    }

    inner class KuralViewHolder(var kuralListBinding: ListItemKuralBinding) :
        RecyclerView.ViewHolder(kuralListBinding.root) {
        init {
            kuralListBinding.constraintLayout.setOnClickListener {
                callback.onItemClick(adapterPosition)
            }
        }
    }
}