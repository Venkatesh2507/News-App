package com.androiddevs.mvvmnewsapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class ArticlesItem(
    @PrimaryKey(autoGenerate = true)
    val id : Int?=null,
    val publishedAt: String?,
    val author: String ?,
    val urlToImage: String?,
    val description: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val content: String?
): Serializable