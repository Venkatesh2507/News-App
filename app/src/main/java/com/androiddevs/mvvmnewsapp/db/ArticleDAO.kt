package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.ArticlesItem
@Dao
interface ArticleDAO {

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsert(articleDAO: ArticlesItem): Long
@Query("SELECT * FROM ARTICLES")
fun getAllArticles(): LiveData<List<ArticlesItem>>
@Delete
suspend fun deleteArticles(articlesItem: ArticlesItem)

}