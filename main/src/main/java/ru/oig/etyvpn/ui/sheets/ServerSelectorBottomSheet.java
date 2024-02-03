/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import ru.oig.etyvpn.R;
import ru.oig.etyvpn.models.AppData;
import ru.oig.etyvpn.models.etyVPNServer;

public class ServerSelectorBottomSheet extends BottomSheetDialogFragment implements ServerSelectorCallback {

    private List<etyVPNServer> servers;
    private AppData appData;
    private ServerSelectorCallback serverSelectorCallback;

    public ServerSelectorBottomSheet(AppData appData, ServerSelectorCallback serverSelectorCallback) {
        this.appData = appData;
        this.serverSelectorCallback = serverSelectorCallback;
        this.servers = appData.getServers();
    }

    public ServerSelectorBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.country_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        ServerAdapter serverAdapter = new ServerAdapter(appData, this, getContext());
        recyclerView.setAdapter(serverAdapter);

    }

    @Override
    public void onServerSelected(etyVPNServer server) {
        dismiss();
        serverSelectorCallback.onServerSelected(server);
    }
}