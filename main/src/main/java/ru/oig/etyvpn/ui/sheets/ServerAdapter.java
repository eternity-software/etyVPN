/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.sheets;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

import ru.oig.etyvpn.R;
import ru.oig.etyvpn.models.AppData;
import ru.oig.etyvpn.models.etyVPNCountry;
import ru.oig.etyvpn.models.etyVPNServer;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.CountryViewHolder> {

    private List<etyVPNServer> servers;
    private AppData appData;
    private Context context;
    private ServerSelectorCallback serverSelectorCallback;


    public ServerAdapter(AppData appData, ServerSelectorCallback serverSelectorCallback, Context context) {
        this.servers = appData.getServers();
        this.appData = appData;
        this.serverSelectorCallback = serverSelectorCallback;
        this.context = context;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        etyVPNServer server = servers.get(position);

        holder.countryName.setText(server.getName() + " ");

        etyVPNCountry country = null;

        if(server.isActive())
        {
            holder.avail.setText(context.getString(R.string.server_available));
        }
        else
        {
            holder.avail.setText(context.getString(R.string.server_unavailable));
        }

        for(etyVPNCountry eCountry : appData.getCountries())
        {
            if(server.getCountryCode().equals(eCountry.getCode()))
            {
                country = eCountry;
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if(server.isActive())
                serverSelectorCallback.onServerSelected(server);

        });

        // Use Picasso to load the flag image
        if(country != null)
        {
            holder.countryName.setText(country.getName() + ", " + server.getName());
            Picasso.get().load(country.getFlagUrl()).into(holder.countryFlag);
        }

    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView countryName;
        TextView avail;
        ImageView countryFlag;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.textViewCountryName);
            avail = itemView.findViewById(R.id.text_avail);
            countryFlag = itemView.findViewById(R.id.imageViewCountryFlag);
        }
    }
}
