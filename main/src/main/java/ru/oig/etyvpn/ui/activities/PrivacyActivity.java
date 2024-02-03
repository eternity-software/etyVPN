/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yandex.metrica.YandexMetrica;

import ru.oig.etyvpn.R;
import ru.oig.etyvpn.core.CacheUtils;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        WebView webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://oigroup.tilda.ws/etyvpnprivacy");



        findViewById(R.id.button_accept).setOnClickListener(v -> {
            CacheUtils.getInstance().setBoolean(CacheUtils.POLICY_ACCEPTED, true, getApplicationContext());
            Intent intent = new Intent(PrivacyActivity.this, MainActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.button_deny).setOnClickListener(v -> {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            YandexMetrica.reportEvent("PrivacyNotAccepted");
        });

    }
}