package com.androiddevs.mvvmnewsapp.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.MetaKeyKeyListener.handleKeyDown
import android.text.method.MetaKeyKeyListener.handleKeyUp
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
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
    private val TAG = "BreakingNewsFragment"
    private var isLongPressingDpadDown = false
    private val longPressThreshold = 2000L
    private val handler = Handler(Looper.getMainLooper())
    private val longPressRunnable = Runnable {
        if (isLongPressingDpadDown) {
            refreshBreakingNews()
        }
    }

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

        // Set OnKeyListener to handle DPAD events
        view.isFocusableInTouchMode = true
        view.requestFocus()
        rvBreakingNews.setOnKeyListener { _, keyCode, event ->
            when (event.action) {
                KeyEvent.ACTION_DOWN -> handleKeyPadDown(keyCode)
                KeyEvent.ACTION_UP -> handleKeyPadUp(keyCode)
                else -> false
            }
        }
        viewModal.breakingNews.observe(viewLifecycleOwner, Observer {response->
           when(response){
               is Resources.Success->{
                   hideProgressBar()
                   response.data?.let {newsResponse ->
                       newsAdapter.differ.submitList(newsResponse.articles?.toList())
                       Log.d(TAG, "Result Success onViewCreated: ${newsResponse.articles?.toList()}")
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
                       Log.d("Breaking News Fragment", "An error occurred $message")
                   }
               }
               is Resources.Loading->{
                   showProgressBar()
               }
           }
        })
    }

    private fun handleKeyPadDown(keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !isLongPressingDpadDown) {
            isLongPressingDpadDown = true
            handler.postDelayed(longPressRunnable, longPressThreshold)
        }

        return true
    }
    private fun handleKeyPadUp(keyCode: Int): Boolean{
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            isLongPressingDpadDown = false
            handler.removeCallbacks(longPressRunnable)
        }
        else{
            refreshBreakingNews()
        }

        return true
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
                viewModal.getBreakingNews("us")
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
    private fun refreshBreakingNews() {
        Toast.makeText(requireContext(), "Refreshing news...", Toast.LENGTH_SHORT).show()
        showProgressBar()
        viewModal.getBreakingNews("us")
        hideProgressBar()
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