/*
 * Copyright (c) 2012-2024 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.remote;

import android.app.Application;
import android.os.StrictMode;

import de.blinkt.etyvpn.remote.BuildConfig;

public class RemoteExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.BUILD_TYPE.equals("debug"))
            enableStrictModes();

    }
    private void enableStrictModes() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog();
        //builder.penaltyDeath();

        StrictMode.VmPolicy policy = builder.build();
        StrictMode.setVmPolicy(policy);
    }
}
