/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.permissioncontroller.role.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.permissioncontroller.DeviceUtils;
import com.android.permissioncontroller.R;
import com.android.permissioncontroller.role.ui.auto.AutoDefaultAppListFragment;
import com.android.permissioncontroller.role.ui.handheld.HandheldDefaultAppListFragment;
import com.android.permissioncontroller.role.ui.wear.WearDefaultAppListFragment;

/**
 * Activity for the list of default apps.
 */
public class DefaultAppListActivity extends SettingsActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (DeviceUtils.isAuto(this)) {
            // Automotive relies on a different theme. Apply before calling super so that
            // fragments are restored properly on configuration changes.
            setTheme(R.style.CarSettings);
        }

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Fragment fragment;
            if (DeviceUtils.isAuto(this)) {
                fragment = AutoDefaultAppListFragment.newInstance();
            } else if (DeviceUtils.isWear(this)) {
                fragment = WearDefaultAppListFragment.Companion.newInstance();
            } else {
                fragment = HandheldDefaultAppListFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }
    }
}
