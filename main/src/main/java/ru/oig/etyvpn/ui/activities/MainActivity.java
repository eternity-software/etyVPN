/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Picasso;
import com.yandex.metrica.YandexMetrica;

import java.util.Locale;

import ru.oig.etyvpn.BuildConfig;
import ru.oig.etyvpn.R;
import ru.oig.etyvpn.api.IOpenVPNAPIService;
import ru.oig.etyvpn.core.ConnectionStatus;
import ru.oig.etyvpn.core.VpnStatus;
import ru.oig.etyvpn.models.AppData;
import ru.oig.etyvpn.models.FetchData;
import ru.oig.etyvpn.models.etyVPNCountry;
import ru.oig.etyvpn.models.etyVPNServer;
import ru.oig.etyvpn.ui.sheets.ServerSelectorBottomSheet;
import ru.oig.etyvpn.ui.sheets.ServerSelectorCallback;

public class MainActivity extends AppCompatActivity implements Handler.Callback, VpnStatus.StateListener, ServerSelectorCallback {


    private static final int ICS_OPENVPN_PERMISSION = 7;
    protected IOpenVPNAPIService mService = null;

    private Handler mHandler;

    private TextView stateTextView;
    private WebView webView;

    private String config;

    private AppData appData;
    private etyVPNServer server;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.

            mService = IOpenVPNAPIService.Stub.asInterface(service);


            try {
                // Request permission to use the API

                Intent i = mService.prepare(getPackageName());
                if (i != null) {
                    startActivityForResult(i, ICS_OPENVPN_PERMISSION);
                } else {
                    onActivityResult(ICS_OPENVPN_PERMISSION, Activity.RESULT_OK, null);
                }
                // mService.startVPN(config);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // VPN permission granted, proceed with VPN setup.
                bindService();
            } else {
                // VPN permission denied, handle accordingly (e.g., show a message or exit the app).
                Toast.makeText(this, "VPN permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stateTextView = findViewById(R.id.state_text_view);
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://etysoft.ru/redirect.php?region=" + Locale.getDefault().getLanguage());


        findViewById(R.id.button_connect).setOnClickListener(v -> {


            AdRequest adRequest = new AdRequest.Builder().build();

            String unitId = BuildConfig.DEBUG ? "ca-app-pub-3940256099942544/1033173712" : "ca-app-pub-3502438832844506/9134960236";


            InterstitialAd.load(this, unitId, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            interstitialAd.show(MainActivity.this);
                            Log.i("s", "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d("s", loadAdError.toString());

                        }
                    });

            if (VpnStatus.isVPNActive()) {
                try {
                    mService.disconnect();
                    // updateButton(ConnectionStatus.LEVEL_NOTCONNECTED);
                } catch (RemoteException e) {

                }
            } else {
                ((TextView) v).setEnabled(false);
                ((TextView) v).setText(getString(R.string.btn_state_init));
                Thread thread = new Thread(() -> {
                    try {
                        config = FetchData.getConfig(server.getConfigUrl());
                        runOnUiThread(() -> {
                            try {
                                System.out.println("conf " + server.getConfigUrl() + config);
                                YandexMetrica.reportEvent("VpnConnectionAttempt");
                                mService.startVPN(config);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                runOnUiThread(() -> {
                                    v.setEnabled(true);
                                });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            v.setEnabled(true);
                        });
                    }

                });
                thread.start();

            }


        });


        mHandler = new android.os.Handler(this);


        bindService();
    }

    public void updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    // Make the network call in a separate thread
                    appData = FetchData.fetchData();

                    // Handle the result on the main thread

                    if (appData != null) {
                        for (etyVPNServer vpnServer : appData.getServers()) {
                            if (vpnServer.isActive()) {
                                server = vpnServer;
                            }
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (appData != null) {
                                // Handle loaded data
                                Log.d("Data Loaded", appData.toString());
                                displayServer();

                            } else {
                                // Handle data loading error
                                Log.e("Data Error", "Error loading data");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        VpnStatus.addStateListener(this);


    }


    private void unbindService() {
        unbindService(mConnection);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindService();
        VpnStatus.removeStateListener(this);
    }

    private void bindService() {

        Intent icsopenvpnService = new Intent(IOpenVPNAPIService.class.getName());
        icsopenvpnService.setPackage("ru.oig.etyvpn");

        bindService(icsopenvpnService, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        /*if(msg.what == MSG_UPDATE_STATE) {
            mStatus.setText((CharSequence) msg.obj);
        } else if (msg.what == MSG_UPDATE_MYIP) {

            mMyIp.setText((CharSequence) msg.obj);
        }*/
        return true;
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level, Intent Intent) {
        runOnUiThread(() -> {

            switch (level) {
                case UNKNOWN_LEVEL -> stateTextView.setText("unknown");
                case LEVEL_CONNECTED -> stateTextView.setText("Connected");
                case LEVEL_CONNECTING_SERVER_REPLIED -> stateTextView.setText("Server replied");
                case LEVEL_CONNECTING_NO_SERVER_REPLY_YET ->
                        stateTextView.setText("Waiting for reply");
                case LEVEL_NOTCONNECTED -> stateTextView.setText("Disconnected");
            }

            updateButton(level);

        });

    }

    public void updateButton(ConnectionStatus status) {
        TextView button = findViewById(R.id.button_connect);
        button.setBackgroundResource(R.drawable.button);
        if (server != null) {
            if (!VpnStatus.isVPNActive())
                button.setText(getString(R.string.connect_country).replace("%s", server.getName()));
        } else {
            button.setText(getString(R.string.select_server));
        }
        button.setEnabled(false);
        switch (status) {
            case LEVEL_CONNECTED -> {
                YandexMetrica.reportEvent("VpnConnected");
                button.setBackgroundResource(R.drawable.button_red);
                button.setText(getString(R.string.disconnect_btn));
                button.setEnabled(true);
            }
            case LEVEL_NOTCONNECTED -> {
                button.setBackgroundResource(R.drawable.button);

                button.setEnabled(true);
            }
            case LEVEL_CONNECTING_NO_SERVER_REPLY_YET ->
                    button.setText(getString(R.string.btn_state_waiting));
            case LEVEL_CONNECTING_SERVER_REPLIED ->
                    button.setText(getString(R.string.btn_state_replied));
        }

    }

    public void displayServer() {
        TextView name = findViewById(R.id.server_name);
        TextView status = findViewById(R.id.server_status);
        TextView button = findViewById(R.id.button_connect);
        if (server == null) {
            name.setText(getString(R.string.select_server));
            button.setText(getString(R.string.select_server));
            button.setEnabled(false);
            return;
        }

        if (!VpnStatus.isVPNActive())
            button.setText(getString(R.string.connect_country).replace("%s", server.getName()));
        button.setEnabled(true);
        name.setText(server.getName() + " ");

        etyVPNCountry country = null;

        if (server.isActive()) {
            status.setText(getString(R.string.server_available));
        } else {
            status.setText(getString(R.string.server_unavailable));
        }

        for (etyVPNCountry eCountry : appData.getCountries()) {
            if (server.getCountryCode().equals(eCountry.getCode())) {
                country = eCountry;
            }
        }

        findViewById(R.id.country_bar).setOnClickListener(v -> {

            if (appData == null) return;
            if (VpnStatus.isVPNActive()) {
                try {
                    mService.disconnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            ServerSelectorBottomSheet bottomSheet = new ServerSelectorBottomSheet(appData, MainActivity.this);
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());

        });

        // Use Picasso to load the flag image
        if (country != null) {
            name.setText(country.getName() + ", " + server.getName());
            Picasso.get().load(country.getFlagUrl()).into((ImageView) findViewById(R.id.country_flag));
        }
    }

    @Override
    public void setConnectedVPN(String uuid) {

    }

    @Override
    public void onServerSelected(etyVPNServer server) {
        this.server = server;
        displayServer();
    }
}