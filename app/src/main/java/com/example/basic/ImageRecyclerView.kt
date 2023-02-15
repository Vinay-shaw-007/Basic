package com.example.basic

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basic.db.ImageEntity

class ImageRecyclerView : RecyclerView.Adapter<ImageRecyclerView.ImageViewHolder>() {

    private lateinit var context: Context

    private var uriImageList: List<ImageEntity> = ArrayList()
    fun setData(uriImageList: List<ImageEntity>) {
        this.uriImageList = uriImageList
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val view = inflater.inflate(R.layout.single_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return uriImageList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(context).load(uriImageList[position].imageUri.toUri()).into(holder.imageView)
    }
}