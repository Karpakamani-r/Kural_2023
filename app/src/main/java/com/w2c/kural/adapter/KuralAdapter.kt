package com.w2c.kural.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.w2c.kural.R
import com.w2c.kural.adapter.KuralAdapter.KuralViewHolder
import com.w2c.kural.database.DatabaseController
import com.w2c.kural.database.Kural
import com.w2c.kural.databinding.ListItemKuralBinding
import com.w2c.kural.utils.IntentKeys
import com.w2c.kural.view.activity.KuralDetails
import java.util.concurrent.Executors


class KuralAdapter(private val kuralList: MutableList<Kural>) :
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
        if (isFromFavourite) {
            //setUpDeleteIcon(holder)
        } else {
            //manageIconForList(holder, kural)
        }
    }
    /*
        private fun setUpDeleteIcon(holder: KuralViewHolder) {
            holder.kuralListBinding.ivFav.setImageResource(R.drawable.ic_delete)
        }

        private fun manageIconForList(holder: KuralViewHolder, kural: Kural) {
            //UI update for favourite in Kural list
            holder.kuralListBinding.ivFav.setImageResource(
                if (kural.favourite == 0) R.drawable.ic_favorite_border else R.drawable.ic_favorite
            )
        }*/

    override fun getItemCount(): Int {
        return kuralList.size
    }

    inner class KuralViewHolder(var kuralListBinding: ListItemKuralBinding) :
        RecyclerView.ViewHolder(kuralListBinding.root) {

        init {
            kuralListBinding.constraintLayout.setOnClickListener {
//                val context = kuralListBinding.root.context
//                val intent = Intent(context, KuralDetails::class.java)
//                intent.putExtra(IntentKeys.KURAL_NO, kuralList[adapterPosition].number)
//                context.startActivity(intent)
            }
            /*kuralListBinding.ivFav.setOnClickListener { v: View ->
                            if (isFromFavourite) {
                                showUnFavAlert(v.context, adapterPosition)
                            } else {
                                updateFav(v.context, adapterPosition)
                            }
                        }*/
        }

        private fun updateFav(context: Context, adapterPosition: Int) {
            //Updates in Model
            val kural = kuralList[adapterPosition]
            kural.favourite = if (kural.favourite == 0) 1 else 0
            updateKuralInDB(context, kural)
        }

        private fun showUnFavAlert(context: Context, adapterPosition: Int) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Remove Favourite!")
            builder.setMessage("Do you want to remove this from favourites?")
            builder.setCancelable(false)
            builder.setPositiveButton("Yes") { _: DialogInterface?, _: Int ->
                updateFav(
                    context,
                    adapterPosition
                )
            }
            builder.setNegativeButton("No") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.show()
        }

        private fun updateKuralInDB(context: Context, kural: Kural) {
            //Accessing database in Background Thread
            Executors.newSingleThreadExecutor().execute {
                DatabaseController.getInstance(context)
                    .kuralDAO
                    .updateKuralData(kural)
                refreshAdapter(kural)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun refreshAdapter(kural: Kural?) {
            //Updating in Local as well
            Handler(Looper.getMainLooper()).post {
                if (isFromFavourite) {
                    kuralList.remove(kural)
                    notifyDataSetChanged()
                } else {
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }
}