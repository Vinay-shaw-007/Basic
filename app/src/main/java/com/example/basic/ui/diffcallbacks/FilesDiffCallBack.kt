package com.example.basic.ui.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.example.basic.db.FilesEntity

class FilesDiffCallBack : DiffUtil.ItemCallback<FilesEntity>() {

    override fun areItemsTheSame(oldItem: FilesEntity, newItem: FilesEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FilesEntity, newItem: FilesEntity): Boolean {
        return oldItem == newItem
    }

}