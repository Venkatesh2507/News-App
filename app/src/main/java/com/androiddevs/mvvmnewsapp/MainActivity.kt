package com.androiddevs.mvvmnewsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.db.ArticleDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.activity_main.newsNavHostFragment

class MainActivity : AppCompatActivity() {
    lateinit var viewModal: NewsViewModal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = NewsRepository(ArticleDatabase(this))
        val viewModalProviderFactory = NewsViewModelProviderFactory(repository)
        viewModal = ViewModelProvider(this,viewModalProviderFactory).get(NewsViewModal::class.java)

        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

    }
}
