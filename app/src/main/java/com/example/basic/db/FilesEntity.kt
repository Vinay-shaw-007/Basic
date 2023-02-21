package com.example.basic.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ImageList")
data class FilesEntity(
    @ColumnInfo(name = "ImageUri") val imageUri: String,
    @ColumnInfo(name = "ImageFileName") val imageFileName: String,
    @ColumnInfo(name = "ImageSize") val imageSize: String,
    @ColumnInfo(name = "ImageStarred") val imageStarred: Int,
    @ColumnInfo(name = "FileType") val fileType: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)