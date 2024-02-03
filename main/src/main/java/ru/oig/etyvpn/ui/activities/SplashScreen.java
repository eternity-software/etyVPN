/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import ru.oig.etyvpn.core.CacheUtils;


public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getApplicationContext());
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("225e3fd7-907d-483f-9df4-047981737bc5").build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(getApplication());
        YandexMetrica.reportEvent("AppOpened");

        if (CacheUtils.getInstance().hasKey(CacheUtils.POLICY_ACCEPTED, this)) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreen.this, PrivacyActivity.class);
            startActivity(intent);
        }
        finish();

    }
}