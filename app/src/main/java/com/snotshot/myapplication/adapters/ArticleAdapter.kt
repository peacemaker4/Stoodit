package com.snotshot.myapplication.adapters

import android.content.Context
import com.snotshot.myapplication.models.Article
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.snotshot.myapplication.R
import android.content.Intent
import android.view.View
import com.snotshot.myapplication.WebActivity
import android.widget.TextView
import com.androidnetworking.widget.ANImageView
import java.util.ArrayList

class ArticleAdapter     // initializing the constructor
    (private val mContext: Context, private val mArrayList: ArrayList<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating the layout with the article view  (R.layout.article_item)
        val view = LayoutInflater.from(mContext).inflate(R.layout.article_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // the parameter position is the index of the current article
        // getting the current article from the ArrayList using the position
        val currentArticle = mArrayList[position]

        // setting the text of textViews
        holder.title.text = currentArticle.title
        holder.description.text = currentArticle.description

        // subString(0,10) trims the date to make it short
        holder.contributordate.text = currentArticle.author +
                " | " + currentArticle.publishedAt!!.substring(0, 10)

        // Loading image from network into
        // Fast Android Networking View ANImageView
        holder.image.setDefaultImageResId(R.drawable.side_nav_bar)
        holder.image.setErrorImageResId(R.drawable.ic_launcher_foreground)
        holder.image.setImageUrl(currentArticle.urlToImage)

        // setting the content Description on the Image
        holder.image.contentDescription = currentArticle.content

        // handling click event of the article
        holder.itemView.setOnClickListener { // an intent to the WebActivity that display web pages
            val intent = Intent(mContext, WebActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("url_key", currentArticle.url)

            // starting an Activity to display the page of the article
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // declaring the views
        val title: TextView
        val description: TextView
        val contributordate: TextView
        val image: ANImageView

        init {
            // assigning views to their ids
            title = itemView.findViewById(R.id.title_id)
            description = itemView.findViewById(R.id.description_id)
            image = itemView.findViewById(R.id.image_id)
            contributordate = itemView.findViewById(R.id.contributordate_id)
        }
    }

    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "ArticleAdapter"
    }
}