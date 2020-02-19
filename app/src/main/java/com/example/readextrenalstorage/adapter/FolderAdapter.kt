package com.example.readextrenalstorage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readextrenalstorage.R
import kotlinx.android.synthetic.main.file_item.view.*
import java.io.File

class FolderAdapter(
    private val ctx: Context,
    private val list: MutableList<File>,
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderVH {
        val view = LayoutInflater.from(ctx).inflate(R.layout.file_item, parent, false)
        return FolderVH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: FolderVH, position: Int) {
        holder.onBind(list[position], listener)
    }

    class FolderVH(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        fun onBind(file: File, listener: (String) -> Unit) {
            itemView.icon.setImageResource(getDrawable(file))

            itemView.tv_name.text = file.name
            itemView.setOnClickListener {
                if (file.isDirectory) {
                    listener(file.name)
                }
            }
        }

        private fun getDrawable(file: File): Int {
            return if (file.isDirectory) {
                R.drawable.folder
            } else {
                R.drawable.file
            }
        }
    }
}