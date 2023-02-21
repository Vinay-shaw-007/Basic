package com.example.basic

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basic.Utils.DOCUMENT_FILE
import com.example.basic.Utils.IMAGE_FILE
import com.example.basic.Utils.VIDEO_FILE
import com.example.basic.db.FilesEntity

class ImageRecyclerView(private val listener: ImageRVAdapter) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val IMAGE = 125
        private const val VIDEO = 202
        private const val DOCUMENT = 402
        private const val ITEM_NULL_FOUND = -1
    }

    private lateinit var context: Context

    private lateinit var uriImageList: List<FilesEntity>
    fun setData(uriImageList: List<FilesEntity>) {
        this.uriImageList = ArrayList()
        this.uriImageList = uriImageList
    }

    override fun getItemViewType(position: Int): Int {
        return when (uriImageList[position].fileType) {
            IMAGE_FILE -> IMAGE
            VIDEO_FILE -> VIDEO
            DOCUMENT_FILE -> DOCUMENT
            else -> ITEM_NULL_FOUND
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return when (viewType) {
            IMAGE -> ImageViewHolder(inflater.inflate(R.layout.single_image_item, parent, false))
            VIDEO -> VideoViewHolder(inflater.inflate(R.layout.video_item, parent, false))
            DOCUMENT -> DocumentViewHolder(inflater.inflate(R.layout.pdf_item, parent, false))
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemCount(): Int {
        return uriImageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = uriImageList[position]
        Log.d("Filename_", item.toString())

        when (holder) {
            is ImageViewHolder -> {

                if (item.imageStarred == 0) {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_border_24)
                } else {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_24)
                }

                Glide.with(context).load(item.imageUri.toUri()).placeholder(R.drawable.baseline_image_not_supported).into(holder.imageView)

                holder.imageView.setOnClickListener {
                    listener.onItemClicked(item)
                }
                holder.starBtn.setOnClickListener {
                    listener.onStarClicked(item, holder)
                }
                holder.deleteBtn.setOnClickListener {
                    listener.onDeleteClicked(item)
                }
            }

            is VideoViewHolder -> {

                if (item.imageStarred == 0) {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_border_24)
                } else {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_24)
                }

                holder.videoView.setOnClickListener {
                    listener.onVideoClicked(item)
                }

                holder.starBtn.setOnClickListener {
                    listener.onStarClicked(item, holder)
                }
                holder.deleteBtn.setOnClickListener {
                    listener.onDeleteClicked(item)
                }
            }

            is DocumentViewHolder -> {

                if (item.imageStarred == 0) {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_border_24)
                } else {
                    holder.starBtn.setImageResource(R.drawable.baseline_star_24)
                }

                holder.pdfDesc.text = item.imageFileName

                holder.pdf.setOnClickListener {
                    listener.onPdfClicked(item)
                }

                holder.starBtn.setOnClickListener {
                    listener.onStarClicked(item, holder)
                }
                holder.deleteBtn.setOnClickListener {
                    listener.onDeleteClicked(item)
                }
            }
        }
    }


    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val deleteBtn: ImageView = itemView.findViewById(R.id.delete_btn)
        val starBtn: ImageView = itemView.findViewById(R.id.star_btn)
    }

    inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pdf: ImageView = itemView.findViewById(R.id.pdf)
        val pdfDesc: TextView = itemView.findViewById(R.id.pdf_desc)
        val deleteBtn: ImageView = itemView.findViewById(R.id.delete_btn)
        val starBtn: ImageView = itemView.findViewById(R.id.star_btn)
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.video_view)
        val deleteBtn: ImageView = itemView.findViewById(R.id.delete_btn)
        val starBtn: ImageView = itemView.findViewById(R.id.star_btn)
    }
}

interface ImageRVAdapter {
    fun onItemClicked(filesEntity: FilesEntity)
    fun onStarClicked(filesEntity: FilesEntity, holder: RecyclerView.ViewHolder)
    fun onDeleteClicked(filesEntity: FilesEntity)
    fun onPdfClicked(filesEntity: FilesEntity)
    fun onVideoClicked(filesEntity: FilesEntity)
}