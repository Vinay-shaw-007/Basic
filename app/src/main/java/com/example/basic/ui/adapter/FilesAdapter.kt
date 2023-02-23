package com.example.basic.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basic.R
import com.example.basic.Utils
import com.example.basic.Utils.DOCUMENT_FILE
import com.example.basic.Utils.IMAGE_FILE
import com.example.basic.Utils.VIDEO_FILE
import com.example.basic.databinding.FileItemBinding
import com.example.basic.db.FilesEntity
import com.example.basic.ui.diffcallbacks.FilesDiffCallBack

class FilesAdapter(private val listener: AdapterItemClickListener) :
    ListAdapter<FilesEntity, FilesAdapter.FilesViewHolder>(FilesDiffCallBack()) {

    private lateinit var context: Context

    inner class FilesViewHolder(val binding: FileItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        context = parent.context
        val view = FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        val fileDetails = getItem(position)
        holder.binding.starBtn.setOnClickListener {
            listener.onStarClicked(fileDetails, holder)
        }
        holder.binding.deleteBtn.setOnClickListener {
            listener.onDeleteClicked(fileDetails)
        }

        if (fileDetails.imageStarred == 0) {
            holder.binding.starBtn.setImageResource(R.drawable.baseline_star_border_24)
        } else {
            holder.binding.starBtn.setImageResource(R.drawable.baseline_star_24)
        }

        if (fileDetails.fileType == IMAGE_FILE) {
            holder.binding.imageFileLayout.visibility = View.VISIBLE
            holder.binding.videoFileLayout.visibility = View.GONE
            holder.binding.documentFileLayout.visibility = View.GONE
            Glide.with(context).load(fileDetails.imageUri.toUri()).into(holder.binding.image)
            holder.binding.image.setOnClickListener {
                listener.onImageClicked(fileDetails)
            }
            return
        }

        if (fileDetails.fileType == VIDEO_FILE) {
            holder.binding.imageFileLayout.visibility = View.GONE
            holder.binding.videoFileLayout.visibility = View.VISIBLE
            holder.binding.documentFileLayout.visibility = View.GONE
            holder.binding.videoView.setOnClickListener {
                listener.onVideoClicked(fileDetails)
            }
            return
        }

        if (fileDetails.fileType == DOCUMENT_FILE) {
            holder.binding.imageFileLayout.visibility = View.GONE
            holder.binding.videoFileLayout.visibility = View.GONE
            holder.binding.documentFileLayout.visibility = View.VISIBLE
            holder.binding.pdfDesc.text = fileDetails.imageFileName
            holder.binding.pdf.setOnClickListener {
                listener.onDocumentClicked(fileDetails)
            }
            return
        }
    }
}

interface AdapterItemClickListener {
    fun onImageClicked(filesEntity: FilesEntity)
    fun onStarClicked(filesEntity: FilesEntity, holder: FilesAdapter.FilesViewHolder)
    fun onDeleteClicked(filesEntity: FilesEntity)
    fun onDocumentClicked(filesEntity: FilesEntity)
    fun onVideoClicked(filesEntity: FilesEntity)
}