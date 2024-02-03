/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import ru.oig.etyvpn.BuildConfig;
import ru.oig.etyvpn.R;
import ru.oig.etyvpn.core.CacheUtils;
import ru.oig.etyvpn.ui.NonCancelableDialog;


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

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getApplicationContext());


        if(resultCode != ConnectionResult.SUCCESS && !BuildConfig.DEBUG)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.SplashTheme));

            alertDialogBuilder.setCancelable(false);
            YandexMetrica.reportEvent("UngoogledAttempt");
            alertDialogBuilder.setTitle("Google Play Services Required");
            alertDialogBuilder.setMessage("etyVPN will only work if downloaded from Google Play");
            alertDialogBuilder.show();
            return;
        }
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