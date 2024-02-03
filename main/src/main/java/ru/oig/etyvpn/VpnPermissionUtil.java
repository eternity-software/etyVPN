/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class VpnPermissionUtil {

    public static boolean hasVpnPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BIND_VPN_SERVICE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestVpnPermission(Context context, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{android.Manifest.permission.BIND_VPN_SERVICE},
                requestCode);
    }
}