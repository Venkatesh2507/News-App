package com.androiddevs.mvvmnewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.ArticlesItem
import com.androiddevs.mvvmnewsapp.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.ivArticleImage
import kotlinx.android.synthetic.main.item_article_preview.view.tvDescription
import kotlinx.android.synthetic.main.item_article_preview.view.tvPublishedAt
import kotlinx.android.synthetic.main.item_article_preview.view.tvSource
import kotlinx.android.synthetic.main.item_article_preview.view.tvTitle

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    private val differCallback = object : DiffUtil.ItemCallback<ArticlesItem>(){
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem.url==newItem.url

        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem==newItem
        }
    }
    val differ = AsyncListDiffer(this,differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
         return ArticleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview,parent,false))
    }

    override fun getItemCount(): Int {
          return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
         val articlesItem = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(articlesItem.urlToImage).into(ivArticleImage)
            tvSource.text =  articlesItem.source?.name
            tvTitle.text = articlesItem.title
            tvDescription.text = articlesItem.description
            tvPublishedAt.text = articlesItem.publishedAt

            setOnClickListener{
                onItemClickListener?.let { it(articlesItem) }
            }
        }

    }
private var onItemClickListener:((ArticlesItem)->Unit)?=null
    fun setOnItemClickListener(listener: (ArticlesItem)->Unit){
        onItemClickListener = listener
    }
}