/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import ru.oig.etyvpn.R;
import ru.oig.etyvpn.ui.activities.MainActivity;
import ru.oig.etyvpn.core.IOpenVPNServiceInternal;
import ru.oig.etyvpn.core.OpenVPNService;


public class ConfirmDialog extends Activity implements
        CompoundButton.OnCheckedChangeListener, DialogInterface.OnClickListener {
    private static final String TAG = "OpenVPNVpnConfirm";

    public static final String EXTRA_PACKAGE_NAME = "android.intent.extra.PACKAGE_NAME";

    public static final String ANONYMOUS_PACKAGE = "ru.oig.etyvpn.ANYPACKAGE";

    private String mPackage;

    private Button mButton;

    private AlertDialog mAlert;

    private IOpenVPNServiceInternal mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = IOpenVPNServiceInternal.Stub.asInterface(service);
            try {
                mService.addAllowedExternalApp(mPackage);
                Intent i = new Intent(ConfirmDialog.this, MainActivity.class);
                startActivity(i);
            } catch (RemoteException e) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();

        Intent serviceintent = new Intent(this, OpenVPNService.class);
        serviceintent.setAction(OpenVPNService.START_SERVICE);
        bindService(serviceintent, mConnection, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        if (intent.getStringExtra(EXTRA_PACKAGE_NAME) != null) {
            mPackage = intent.getStringExtra(EXTRA_PACKAGE_NAME);
        } else {
            mPackage = getCallingPackage();
            if (mPackage == null) {
                finish();
                return;
            }
        }

        try {
            View view = View.inflate(this, R.layout.api_confirm, null);
            CharSequence appString;
            if (mPackage.equals(ANONYMOUS_PACKAGE)) {
                appString = getString(R.string.all_app_prompt, getString(R.string.app));
            } else {
                PackageManager pm = getPackageManager();
                ApplicationInfo app = pm.getApplicationInfo(mPackage, 0);
                appString = getString(R.string.prompt, app.loadLabel(pm), getString(R.string.app));
                ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(app.loadIcon(pm));
            }


            ((TextView) view.findViewById(R.id.prompt)).setText(appString);
            ((CompoundButton) view.findViewById(R.id.check)).setOnCheckedChangeListener(this);


            Builder builder = new AlertDialog.Builder(this);

            builder.setView(view);

            builder.setIconAttribute(android.R.attr.alertDialogIcon);
            builder.setTitle(android.R.string.dialog_alert_title);
            builder.setPositiveButton(android.R.string.ok, this);
            builder.setNegativeButton(android.R.string.cancel, this);

            mAlert = builder.create();
            mAlert.setCanceledOnTouchOutside(false);

            mAlert.setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mButton = mAlert.getButton(DialogInterface.BUTTON_POSITIVE);
                    mButton.setEnabled(false);

                }
            });

            //setCloseOnTouchOutside(false);


        } catch (Exception e) {
            Log.e(TAG, "onResume", e);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        mButton.setEnabled(checked);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == DialogInterface.BUTTON_POSITIVE) {
            try {
                mService.addAllowedExternalApp(mPackage);
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            setResult(RESULT_OK);

        }

        if (which == DialogInterface.BUTTON_NEGATIVE) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

}

