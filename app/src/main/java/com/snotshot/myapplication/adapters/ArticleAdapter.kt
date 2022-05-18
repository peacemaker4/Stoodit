package com.snotshot.myapplication.adapters

import android.content.Context
import com.snotshot.myapplication.models.Article
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.snotshot.myapplication.R
import android.content.Intent
import android.os.Build
import android.view.View
import com.snotshot.myapplication.WebActivity
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.androidnetworking.widget.ANImageView
import java.time.*
import java.time.Duration.between
import java.time.Period.between
import java.time.chrono.ChronoPeriod.between
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*

class ArticleAdapter     // initializing the constructor
    (private val mContext: Context, private val mArrayList: ArrayList<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflating the layout with the article view  (R.layout.article_item)
        val view = LayoutInflater.from(mContext).inflate(R.layout.article_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // the parameter position is the index of the current article
        // getting the current article from the ArrayList using the position
        val currentArticle = mArrayList[position]

        // setting the text of textViews
        holder.title.text = currentArticle.title
        holder.description.text = currentArticle.description

        val instant = Instant.parse(currentArticle.publishedAt) ;


        val z: ZoneId = ZoneId.of("Asia/Almaty")
        val zdt: ZonedDateTime = instant.atZone(z)
        val zdtNow: ZonedDateTime = ZonedDateTime.now(z)
//        val diff = ChronoUnit.between(zdt, zdtNow)
//        val locale: Locale = Locale.ENGLISH
//        val f = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale)
//        val output = zdt.format(f)
        val duration = Duration.between(zdt, zdtNow)
        var output = ""
        if(duration.toDays() >= 1){
            output = duration.toDays().toString() + " d"
        }
        if(duration.toHours() >= 1){
            output = duration.toHours().toString() + " h"
        }
        else if(duration.toMinutes() > 1){
            output = duration.toMinutes().toString() + " m"
        }
        else{
            output = duration.toSeconds().toString() + " s"
        }

        if(currentArticle.author != "null" && !currentArticle.author!!.isEmpty()){
            holder.contributordate.text = currentArticle.author + " • "
        }
        holder.publishtime.text = output


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
        val publishtime: TextView
        val image: ANImageView

        init {
            // assigning views to their ids
            title = itemView.findViewById(R.id.title_id)
            description = itemView.findViewById(R.id.description_id)
            image = itemView.findViewById(R.id.image_id)
            contributordate = itemView.findViewById(R.id.contributordate_id)
            publishtime = itemView.findViewById(R.id.publish_time)
        }
    }

    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "ArticleAdapter"
    }
}