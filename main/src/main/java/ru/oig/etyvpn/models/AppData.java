/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppData {
    @SerializedName("active")
    private boolean active;

    @SerializedName("ads_id")
    private String adsId;

    @SerializedName("last_version")
    private int lastVersion;

    @SerializedName("update_banner_text")
    private String updateBannerText;

    @SerializedName("update_required")
    private boolean updateRequired;

    @SerializedName("countries")
    private List<etyVPNCountry> countries;

    @SerializedName("servers")
    private List<etyVPNServer> servers;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAdsId() {
        return adsId;
    }

    public void setAdsId(String adsId) {
        this.adsId = adsId;
    }

    public int getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(int lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getUpdateBannerText() {
        return updateBannerText;
    }

    public void setUpdateBannerText(String updateBannerText) {
        this.updateBannerText = updateBannerText;
    }

    public boolean isUpdateRequired() {
        return updateRequired;
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    public List<etyVPNCountry> getCountries() {
        return countries;
    }

    public void setCountries(List<etyVPNCountry> countries) {
        this.countries = countries;
    }

    public List<etyVPNServer> getServers() {
        return servers;
    }

    public void setServers(List<etyVPNServer> servers) {
        this.servers = servers;
    }
}
