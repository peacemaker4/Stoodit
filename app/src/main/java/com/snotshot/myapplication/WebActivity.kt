package com.snotshot.myapplication

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.databinding.ActivityWebBinding


class WebActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding: ActivityWebBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    private var myWebView: WebView? = null

    // declaring the url string variable
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "News"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        myWebView = binding.webView

        val intent = intent


        // checking if there is an intent
        if (intent != null) {
            // retrieving the url in the intent
            url = intent.getStringExtra("url_key")

            // loading and displaying a
            // web page in the activity
            myWebView!!.loadUrl(url!!)
        }
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