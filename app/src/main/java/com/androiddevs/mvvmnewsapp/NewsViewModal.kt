package com.androiddevs.mvvmnewsapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.bumptech.glide.load.engine.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModal(
    val newsRepository: NewsRepository
):ViewModel(){
val breakingNews: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
var breakingNewsPage = 1
    val searchNews: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    var breakingNewsResponse: NewsResponse?=null
    var searchNewsResponse: NewsResponse?=null


    init {
        getBreakingNews("us")
    }
    fun getBreakingNews(countryCode : String) = viewModelScope.launch {
        breakingNews.postValue(Resources.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun searchNews(countryCode: String) = viewModelScope.launch {
        searchNews.postValue(Resources.Loading())
        val response = newsRepository.searchNews(countryCode,searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resources<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                breakingNewsPage++
                if (breakingNewsResponse==null){
                    breakingNewsResponse = resultResponse
                }
                else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    if (newArticles != null) {
                        oldArticles?.addAll(newArticles)
                    }
                    Log.d("NewsViewModal", "handleBreakingNewsResponse: $oldArticles")
                }
                return Resources.Success(breakingNewsResponse ?:resultResponse)
            }
        }
        return Resources.Error(response.message())
    }
    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resources<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {resultResponse->
                searchNewsPage++
                if (searchNewsResponse==null){
                    searchNewsResponse = resultResponse
                }
                else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    if (newArticles != null) {
                        oldArticles?.addAll(newArticles)
                    }
                }
                return Resources.Success(searchNewsResponse ?:resultResponse)
            }
        }
        return Resources.Error(response.message())
    }

    fun saveArticle(articlesItem: ArticlesItem) = viewModelScope.launch {
        newsRepository.upsert(articlesItem)

    }
    fun getSavedNews() = newsRepository.getSavedNews()
    fun deleteArticles(articlesItem: ArticlesItem) =viewModelScope.launch {
        newsRepository.deleteArticle(articlesItem)
    }
}