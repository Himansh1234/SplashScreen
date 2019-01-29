package com.example.vsvll.splashscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Donation extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        webView = findViewById(R.id.donation_id);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://mlp.co/wayoflif");
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack() == true) {
            webView.goBack();
        } else if (webView.canGoBack() == false) {
            //here we can add and alert dialog box saying "Are you sure you want to leave"
            //the below line will close the app if no page to go back
            super.onBackPressed();
        }
    }


}
