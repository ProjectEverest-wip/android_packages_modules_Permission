/*
 * Copyright (C) 2020 The Android Open Source Project
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
@file:Suppress("DEPRECATION")

package com.android.permissioncontroller.permission.data

import android.app.Application
import android.content.pm.PackageManager.GET_PERMISSIONS
import android.content.pm.PackageManager.MATCH_FACTORY_ONLY
import android.content.pm.PackageManager.MATCH_UNINSTALLED_PACKAGES
import android.os.UserHandle
import com.android.permissioncontroller.PermissionControllerApplication
import com.android.permissioncontroller.permission.model.livedatatypes.LightPackageInfo
import kotlinx.coroutines.Job

/**
 * A LiveData which returns all of the preinstalled packageinfos. For packages that are preinstalled
 * and then updated, the preinstalled (i.e. old) version is returned.
 *
 * @param app The current application
 * @param user The user whose packages are desired
 */
class PreinstalledUserPackageInfosLiveData
private constructor(private val app: Application, private val user: UserHandle) :
    SmartAsyncMediatorLiveData<@kotlin.jvm.JvmSuppressWildcards List<LightPackageInfo>>(
        isStaticVal = true,
        alwaysUpdateOnActive = false
    ) {

    /** Get all of the preinstalled packages in the system for this user */
    override suspend fun loadDataAndPostValue(job: Job) {
        if (job.isCancelled) {
            return
        }
        val packageInfos =
            app.applicationContext.packageManager.getInstalledPackagesAsUser(
                GET_PERMISSIONS or MATCH_UNINSTALLED_PACKAGES or MATCH_FACTORY_ONLY,
                user.identifier
            )
        postValue(packageInfos.map { packageInfo -> LightPackageInfo(packageInfo) })
    }

    override fun onActive() {
        super.onActive()

        // Data never changes, hence no need to reload
        if (value == null) {
            updateAsync()
        }
    }

    /**
     * Repository for PreinstalledUserPackageInfosLiveData.
     *
     * <p>Key value is a UserHandle, value is its corresponding LiveData.
     */
    companion object : DataRepository<UserHandle, PreinstalledUserPackageInfosLiveData>() {
        override fun newValue(key: UserHandle): PreinstalledUserPackageInfosLiveData {
            return PreinstalledUserPackageInfosLiveData(PermissionControllerApplication.get(), key)
        }
    }
}
