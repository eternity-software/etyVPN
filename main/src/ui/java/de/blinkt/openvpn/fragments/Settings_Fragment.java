/*
 * Copyright (c) 2012-2015 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import ru.oig.etyvpn.R;
import ru.oig.etyvpn.VpnProfile;
import ru.oig.etyvpn.core.ProfileManager;

public abstract class Settings_Fragment extends Fragment {

    protected VpnProfile mProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String profileUuid = getArguments().getString(getActivity().getPackageName() + ".profileUUID");
        mProfile = ProfileManager.get(getActivity(), profileUuid);
        getActivity().setTitle(getString(R.string.edit_profile_title, mProfile.getName()));
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
    }

    protected abstract void savePreferences();
}
