package com.snotshot.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.snotshot.myapplication.databinding.ActivityNewsBinding
import com.snotshot.myapplication.adapters.ArticleAdapter

import androidx.recyclerview.widget.RecyclerView

import android.widget.ProgressBar
import com.snotshot.myapplication.models.Article
import androidx.recyclerview.widget.LinearLayoutManager

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi

import com.jacksonandroidnetworking.JacksonParserFactory

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError

import org.json.JSONException

import org.json.JSONObject

import com.androidnetworking.interfaces.JSONObjectRequestListener
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.snotshot.myapplication.extensions.SpacesItemDecoration


class NewsActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding: ActivityNewsBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    // declaring the url string variable
    private var url: String? = null

    private var API_KEY : String = "8b09440685e845c2924e76804d0369a3";

    // declaring the views
    private var mProgressBar: ProgressBar? = null
    private var mRecyclerView: RecyclerView? = null

    // declaring an ArrayList of articles
    private var mArticleList: ArrayList<Article>? = null

    private var mArticleAdapter: ArticleAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "News"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        // initializing the Fast Android Networking Library
        AndroidNetworking.initialize(applicationContext)

        // setting the JacksonParserFactory

        // setting the JacksonParserFactory
        AndroidNetworking.setParserFactory(JacksonParserFactory())

        // assigning views to their ids

        // assigning views to their ids
        mProgressBar = binding.progressBarNews
        mRecyclerView = binding.newsList

        // setting the recyclerview layout manager

        // setting the recyclerview layout manager
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(this))

        // initializing the ArrayList of articles

        // initializing the ArrayList of articles
        mArticleList = ArrayList()

        get_news_from_api()
    }

    fun get_news_from_api() {
        // clearing the articles list before adding news ones
        mArticleList!!.clear()

        AndroidNetworking.get("https://newsapi.org/v2/top-headlines")
            .addQueryParameter("language", "en")
            .addQueryParameter("category", "technology")
            .addQueryParameter("apiKey", API_KEY)
            .addHeaders("token", "1234")
            .setTag("test")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(response: JSONObject) {
                    // disabling the progress bar
                    mProgressBar!!.visibility = View.GONE

                    // handling the response
                    try {

                        // storing the response in a JSONArray
                        val articles = response.getJSONArray("articles")

                        // looping through all the articles
                        // to access them individually
                        for (j in 0 until articles.length()) {
                            // accessing each article object in the JSONArray
                            val article = articles.getJSONObject(j)

                            // initializing an empty ArticleModel
                            val currentArticle = Article()

                            // storing values of the article object properties
                            val author = article.getString("author")
                            val title = article.getString("title")
                            val description = article.getString("description")
                            val url = article.getString("url")
                            val urlToImage = article.getString("urlToImage")
                            val publishedAt = article.getString("publishedAt")
                            val content = article.getString("content")

                            // setting the values of the ArticleModel
                            // using the set methods
                            currentArticle.author = author
                            currentArticle.title = title
                            currentArticle.description = description
                            currentArticle.url = url
                            currentArticle.urlToImage = urlToImage
                            currentArticle.publishedAt = publishedAt
                            currentArticle.content = content

                            // adding an article to the articles List
                            mArticleList!!.add(currentArticle)
                        }

                        // setting the adapter
                        mRecyclerView!!.layoutManager =
                            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        mArticleAdapter = ArticleAdapter(applicationContext, mArticleList!!)
                        val decoration = SpacesItemDecoration(16)
                        mRecyclerView!!.addItemDecoration(decoration)
                        mRecyclerView!!.adapter = mArticleAdapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // logging the JSONException LogCat
                        Log.d(TAG, "Error : " + e.message)
                    }
                }

                override fun onError(error: ANError) {
                    // logging the error detail and response to LogCat
                    Log.d(TAG, "Error detail : " + error.errorDetail)
                    Log.d(TAG, "Error response : " + error.response)
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }
}