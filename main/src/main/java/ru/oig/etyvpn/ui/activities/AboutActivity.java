/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import ru.oig.etyvpn.BuildConfig;
import ru.oig.etyvpn.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView version = findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME + " " + BuildConfig.VERSION_CODE);
        findViewById(R.id.privacy_button).setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://oigroup.tilda.ws/etyvpnprivacy"));
            startActivity(browserIntent);
        });
    }
}