package com.androiddevs.mvvmnewsapp.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.Constants.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.MainActivity
import com.androiddevs.mvvmnewsapp.NewsViewModal
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.Resources
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_breaking_news.rvBreakingNews

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    lateinit var viewModal: NewsViewModal
    lateinit var newsAdapter : NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModal = (activity as MainActivity).viewModal


        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        viewModal.breakingNews.observe(viewLifecycleOwner, Observer {response->
           when(response){
               is Resources.Success->{
                   hideProgressBar()
                   response.data?.let {newsResponse ->
                       newsAdapter.differ.submitList(newsResponse.articles?.toList())
                       val totalPages = newsResponse.totalResults/ QUERY_PAGE_SIZE + 2
                       isLastPage = viewModal.breakingNewsPage == totalPages
                       if (isLastPage){
                           rvBreakingNews.setPadding(0,0,0,0)
                       }
                   }
               }
               is Resources.Error->{
                   hideProgressBar()
                   response.message?.let { message->
                       Log.d("Breaking News Fragment", "An error occured $message")
                   }
               }
               is Resources.Loading->{
                   showProgressBar()
               }
           }
        })
    }

    private fun hideProgressBar() {
       paginationProgressBar.visibility = View.INVISIBLE

        isLoading = false

    }
    private fun showProgressBar() {
       paginationProgressBar.visibility = View.VISIBLE
        isLoading = true

    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount>=totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition>=0
            val isTotalMoreThanVisible = totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModal.getBreakingNews("in")
                isScrolling = false

            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true

            }
        }
    }



    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}