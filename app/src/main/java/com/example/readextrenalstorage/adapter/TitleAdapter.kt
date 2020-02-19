package com.example.readextrenalstorage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readextrenalstorage.R
import com.example.readextrenalstorage.data.Model
import kotlinx.android.synthetic.main.title_item.view.*

class TitleAdapter(
    private val ctx: Context,
    private val list: MutableList<Model>,
    private val listener: (Model) -> Unit

) : RecyclerView.Adapter<TitleAdapter.TitleVh>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleVh {
        val view = LayoutInflater.from(ctx).inflate(R.layout.title_item, parent, false)
        return TitleVh(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TitleVh, position: Int) {
        holder.onBind(list[position], listener)
    }

    class TitleVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(s: Model, listener: (Model) -> Unit) {
            itemView.tv_title.text = s.name

            itemView.setOnClickListener {
                listener(s)
            }
        }
    }

}