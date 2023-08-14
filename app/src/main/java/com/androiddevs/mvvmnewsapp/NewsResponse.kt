package com.androiddevs.mvvmnewsapp

data class NewsResponse(val totalResults: Int = 0,
                        val articles: MutableList<ArticlesItem>?,
                        val status: String = "")