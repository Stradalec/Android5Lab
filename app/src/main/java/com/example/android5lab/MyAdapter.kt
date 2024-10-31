package com.example.android5lab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(private val imageUrlList: List<String>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewItem: ImageView = view.findViewById(R.id.recyclerViewIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rview_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.imageViewItem)
            .load(imageUrlList[position])
            .centerCrop()
            .into(holder.imageViewItem)
        holder.imageViewItem.setOnClickListener {
            onClick(imageUrlList[position])
        }
    }

    override fun getItemCount(): Int {
        return imageUrlList.size
    }
}