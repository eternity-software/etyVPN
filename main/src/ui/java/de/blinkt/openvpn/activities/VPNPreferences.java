/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;

import ru.oig.etyvpn.R;
import ru.oig.etyvpn.VpnProfile;
import ru.oig.etyvpn.core.ProfileManager;
import ru.oig.etyvpn.fragments.Settings_Allowed_Apps;
import ru.oig.etyvpn.fragments.Settings_Authentication;
import ru.oig.etyvpn.fragments.Settings_Basic;
import ru.oig.etyvpn.fragments.Settings_Connections;
import ru.oig.etyvpn.fragments.Settings_IP;
import ru.oig.etyvpn.fragments.Settings_Obscure;
import ru.oig.etyvpn.fragments.Settings_Routing;
import ru.oig.etyvpn.fragments.Settings_UserEditable;
import ru.oig.etyvpn.fragments.ShowConfigFragment;
import ru.oig.etyvpn.fragments.VPNProfileList;
import ru.oig.etyvpn.views.ScreenSlidePagerAdapter;


public class VPNPreferences extends BaseActivity {

    static final Class[] validFragments = new Class[]{
            Settings_Authentication.class, Settings_Basic.class, Settings_IP.class,
            Settings_Obscure.class, Settings_Routing.class, ShowConfigFragment.class,
            Settings_Connections.class, Settings_Allowed_Apps.class,
    };

    private String mProfileUUID;
	private VpnProfile mProfile;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    public VPNPreferences() {
		super();
	}


    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected boolean isValidFragment(String fragmentName) {
        for (Class c: validFragments)
            if (c.getName().equals(fragmentName))
                return true;
        return false;

    }

    @Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(getIntent().getStringExtra(getPackageName() + ".profileUUID"),mProfileUUID);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
        getProfile();
		// When a profile is deleted from a category fragment in hadset mod we need to finish
		// this activity as well when returning
		if (mProfile==null || mProfile.profileDeleted) {
			setResult(VPNProfileList.RESULT_VPN_DELETED);
			finish();
		}
		if (mProfile.mTemporaryProfile)
        {
            Toast.makeText(this, "Temporary profiles cannot be edited", Toast.LENGTH_LONG).show();
            finish();
        }
	}

    private void getProfile() {
        Intent intent = getIntent();

        if(intent!=null) {
            String profileUUID = intent.getStringExtra(getPackageName() + ".profileUUID");
            if(profileUUID==null) {
                Bundle initialArguments = getIntent().getBundleExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS);
                profileUUID =  initialArguments.getString(getPackageName() + ".profileUUID");
            }
            if(profileUUID!=null){

                mProfileUUID = profileUUID;
                mProfile = ProfileManager.get(this, mProfileUUID);

            }
        }
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		mProfileUUID = getIntent().getStringExtra(getPackageName() + ".profileUUID");
		if(savedInstanceState!=null){
			String savedUUID = savedInstanceState.getString(getPackageName() + ".profileUUID");
			if(savedUUID!=null)
				mProfileUUID=savedUUID;
		}
        super.onCreate(savedInstanceState);

		mProfile = ProfileManager.get(this,mProfileUUID);
		if(mProfile==null) {
		    Toast.makeText(this, "Profile to edit cannot be found.", Toast.LENGTH_LONG).show();
		    finish();
		    return;
		}

        setTitle(getString(R.string.edit_profile_title, mProfile.getName()));


        setContentView(R.layout.main_activity);

        disableToolbarElevation();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), this);


        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(getPackageName() + ".profileUUID",mProfileUUID);
        mPagerAdapter.setFragmentArgs(fragmentArguments);

        if (mProfile.mUserEditable) {
            mPagerAdapter.addTab(R.string.basic, Settings_Basic.class);
            mPagerAdapter.addTab(R.string.server_list, Settings_Connections.class);
            mPagerAdapter.addTab(R.string.ipdns, Settings_IP.class);
            mPagerAdapter.addTab(R.string.routing, Settings_Routing.class);
            mPagerAdapter.addTab(R.string.settings_auth, Settings_Authentication.class);

            mPagerAdapter.addTab(R.string.advanced, Settings_Obscure.class);
        } else {
            mPagerAdapter.addTab(R.string.basic, Settings_UserEditable.class);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPagerAdapter.addTab(R.string.vpn_allowed_apps, Settings_Allowed_Apps.class);
        }
        mPagerAdapter.addTab(R.string.generated_config, ShowConfigFragment.class);


        mPager.setAdapter(mPagerAdapter);

        //TabBarView tabs = (TabBarView) findViewById(R.id.sliding_tabs);
        //tabs.setViewPager(mPager);

	}


	@Override
	public void onBackPressed() {
		setResult(RESULT_OK, getIntent());
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.remove_vpn)
			askProfileRemoval();
        if (item.getItemId() == R.id.duplicate_vpn) {
            Intent data = new Intent();
            data.putExtra(VpnProfile.EXTRA_PROFILEUUID, mProfileUUID);
            setResult(VPNProfileList.RESULT_VPN_DUPLICATE, data);
            finish();
        }

        return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.vpnpreferences_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	private void askProfileRemoval() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Confirm deletion");
		dialog.setMessage(getString(R.string.remove_vpn_query, mProfile.mName));

		dialog.setPositiveButton(android.R.string.yes,
                (dialog1, which) -> removeProfile(mProfile));
		dialog.setNegativeButton(android.R.string.no,null);
		dialog.create().show();
	}

	protected void removeProfile(VpnProfile profile) {
		ProfileManager.getInstance(this).removeProfile(this,profile);
		setResult(VPNProfileList.RESULT_VPN_DELETED);
		finish();

	}

    private void disableToolbarElevation() {
        ActionBar toolbar = getSupportActionBar();
        toolbar.setElevation(0);
    }

}
