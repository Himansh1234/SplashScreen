package com.example.vsvll.splashscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Blood_Donation extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood__donation);

         webView = findViewById(R.id.blood_donation_id);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.friends2support.org/");
    }



    @Override
    public void onBackPressed() {
        if(webView.canGoBack() == true){
            webView.goBack();
        }
        else if(webView.canGoBack() == false)
        {
            //here we can add and alert dialog box saying "Are you sure you want to leave"
            //the below line will close the app if no page to go back
            super.onBackPressed();
        }

    }

}
