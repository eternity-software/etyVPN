/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.ui.sheets;

import ru.oig.etyvpn.models.etyVPNServer;

public interface ServerSelectorCallback {

    void onServerSelected(etyVPNServer server);
}
