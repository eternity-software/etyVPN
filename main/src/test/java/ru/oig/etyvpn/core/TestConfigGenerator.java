/*
 * Copyright (c) 2012-2018 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.core;

import android.content.pm.PackageManager;
import android.os.Build;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.oig.etyvpn.VpnProfile;
import ru.oig.etyvpn.core.Connection;

import static ru.oig.etyvpn.VpnProfile.AUTH_RETRY_NOINTERACT;
import static ru.oig.etyvpn.VpnProfile.TYPE_USERPASS;

/**
 * Created by arne on 14.03.18.
 */


@Config(sdk = Build.VERSION_CODES.O_MR1)
@RunWith(RobolectricTestRunner.class)
public class TestConfigGenerator {
    @Test
    public void testAuthRetryGen() throws PackageManager.NameNotFoundException {
        /*Context mc = mock(Context.class);
        PackageManager mpm = mock(PackageManager.class);

        PackageInfo mpi = new PackageInfo();
        mpi.versionCode = 177;
        mpi.versionName = "foo";

        when(mc.getCacheDir()).thenReturn(new File("/j/unit/test/"));
        when(mc.getPackageName()).thenReturn("ru.oig.etyvpn");
        when(mc.getPackageManager()).thenReturn(mpm);
        when(mpm.getPackageInfo(eq("ru.oig.etyvpn"),eq(0))).thenReturn(mpi);*/



        VpnProfile vp = new VpnProfile ("test") {
            @Override
            public String getPlatformVersionEnvString() {
                return "test";
            }
        };

        vp.mAuthenticationType = TYPE_USERPASS;
        vp.mAuthRetry = AUTH_RETRY_NOINTERACT;
        String config = vp.getConfigFile(RuntimeEnvironment.application, false);
        Assert.assertTrue(config.contains("\nauth-retry nointeract\n"));
        for (Connection connection: vp.mConnections)
            Assert.assertTrue(connection.mProxyType == Connection.ProxyType.NONE);

    }

    @Test
    public void testEscape()
    {
        String uglyPassword = "^OrFg1{G^SS8b4J@B$Y1Dr\\GwG-dw3aBJ/R@WI*doCVP',+:>zjqC[&b6[8=KL:`{l&:i!_4*npE?4k2c^(n>9Tjp~u2Z]l8(y&Gg<-cwR2k=yKK:-%f-ezQ\"^g)[d,kbsu$cqih\\wA~on$~)QSODtip2cd,+->qv,roF*9>6q:lTepm=r?Y-+(K]ERGn\"+AiLj<(R_'BOg:vsh0wh]BQ-PVo534;l%R*FF!+,$?Q00%839(k?E!x0R[Lx6qK\\&";
        String escapedUglyPassword = VpnProfile.openVpnEscape(uglyPassword);

        String testWithQuote = "'meinpw";
        String escapedQuote = VpnProfile.openVpnEscape(testWithQuote);
        org.junit.Assert.assertEquals("\"'meinpw\"", escapedQuote);
    }


}
