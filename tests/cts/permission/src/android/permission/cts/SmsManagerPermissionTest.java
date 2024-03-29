/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.permission.cts;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assume.assumeTrue;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * Test that sending SMS and MMS messages requires permissions.
 */
@RunWith(AndroidJUnit4.class)
public class SmsManagerPermissionTest {

    private static final String SOURCE_ADDRESS = "+15550000000";
    private static final String DESTINATION_ADDRESS = "+15550000001";

    private boolean mHasTelephony;
    private SmsManager mSmsManager;
    private Context mContext;
    private TelephonyManager mTelephonyManager;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getContext();
        mTelephonyManager = mContext.getSystemService(TelephonyManager.class);
        int subId = SubscriptionManager.getDefaultSmsSubscriptionId();
        mHasTelephony = mContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_TELEPHONY);
        assumeTrue(mHasTelephony); // Don't run these tests if FEATURE_TELEPHONY is not available.

        Log.d("SmsManagerPermissionTest", "mSubId=" + subId);

        mSmsManager = SmsManager.getSmsManagerForSubscriptionId(subId);
        assertNotNull(mSmsManager);
    }

    @Test
    public void testSendTextMessage() {
        assertFalse("[RERUN] Device does not have SIM card. Use a suitable SIM Card.",
                isSimCardAbsent());

        assertThrows(SecurityException.class, () -> mSmsManager.sendTextMessage(
                DESTINATION_ADDRESS, SOURCE_ADDRESS, "Message text", null, null));
    }

    @Test
    public void testSendTextMessageWithoutPersisting() {
        assertFalse("[RERUN] Device does not have SIM card. Use a suitable SIM Card.",
                isSimCardAbsent());

        assertThrows(SecurityException.class, () -> mSmsManager.sendTextMessageWithoutPersisting(
                DESTINATION_ADDRESS, SOURCE_ADDRESS, "Message text", null, null));
    }

    @Test
    public void testSendMultipartTextMessage() {
        assertFalse("[RERUN] Device does not have SIM card. Use a suitable SIM Card.",
                isSimCardAbsent());

        ArrayList<String> messageParts = new ArrayList<>();
        messageParts.add("Message text");
        assertThrows(SecurityException.class, () -> mSmsManager.sendMultipartTextMessage(
                DESTINATION_ADDRESS, SOURCE_ADDRESS, messageParts, null, null));
    }

    @Test
    public void testSendDataMessage() {
        assertFalse("[RERUN] Device does not have SIM card. Use a suitable SIM Card.",
                isSimCardAbsent());

        assertThrows(SecurityException.class, () -> mSmsManager.sendDataMessage(
                DESTINATION_ADDRESS, SOURCE_ADDRESS, (short) 1, new byte[]{0, 0, 0}, null, null));
    }

    @Test
    public void testSendMultimediaMessage() {
        assertFalse("[RERUN] Device does not have SIM card. Use a suitable SIM Card.",
                isSimCardAbsent());

        // Ideally we would provide an Uri to an existing resource, to make sure the
        // SecurityException is not due to the invalid Uri.
        Uri uri = Uri.parse("android.resource://android.permission.cts/some-image.png");
        assertThrows(SecurityException.class,
                () -> mSmsManager.sendMultimediaMessage(mContext, uri, "", null, null));
    }

    private boolean isSimCardAbsent() {
        return ((mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT)
                || (SubscriptionManager.getDefaultSmsSubscriptionId()
                == SubscriptionManager.INVALID_SUBSCRIPTION_ID));
    }
}
