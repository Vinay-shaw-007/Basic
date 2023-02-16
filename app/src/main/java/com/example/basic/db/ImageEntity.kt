package com.example.basic.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ImageList")
data class ImageEntity(
    @ColumnInfo(name = "ImageUri") val imageUri: String,
    @ColumnInfo(name = "ImageFileName") val imageFileName: String,
    @ColumnInfo(name = "ImageSize") val imageSize: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)