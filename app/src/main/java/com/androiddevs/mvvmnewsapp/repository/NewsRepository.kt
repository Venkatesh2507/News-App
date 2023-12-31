package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.Api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.ArticlesItem
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import retrofit2.Retrofit

class NewsRepository(val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String , pageNumber: Int)= RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
    suspend fun searchNews(searchQuery: String , pageNumber: Int)=RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
    suspend fun upsert(article: ArticlesItem)=db.getArticleDao().upsert(article)
    fun getSavedNews() = db.getArticleDao().getAllArticles()
    suspend fun deleteArticle(articlesItem: ArticlesItem) = db.getArticleDao().deleteArticles(articlesItem)



}