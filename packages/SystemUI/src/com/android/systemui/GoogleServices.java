/*
 * Copyright (C) 2016 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.systemui;

import android.app.AlarmManager;
import android.content.Context;

import com.android.systemui.res.R;
import com.android.systemui.statusbar.phone.CentralSurfaces;
import com.android.systemui.util.wakelock.DelayedWakeLock;

import com.android.systemui.ambientmusic.AmbientIndicationContainer;
import com.android.systemui.ambientmusic.AmbientIndicationService;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.Lazy;

public class GoogleServices extends VendorServices {
    private final Context mContext;
    private final ArrayList<Object> mServices;
    private final CentralSurfaces mCentralSurfaces;
    private final AlarmManager mAlarmManager;
    private final DelayedWakeLock.Factory mDelayedWakeLockFactory;

    @Inject
    public GoogleServices(
            Context context,
            AlarmManager alarmManager,
            CentralSurfaces centralSurfaces,
            DelayedWakeLock.Factory delayedWakeLockFactory) {
        super();
        mContext = context;
        mServices = new ArrayList<>();
        mAlarmManager = alarmManager;
        mCentralSurfaces = centralSurfaces;
        mDelayedWakeLockFactory = delayedWakeLockFactory;
    }

    @Override
    public void start() {
        final AmbientIndicationContainer ambientIndicationContainer =
            (AmbientIndicationContainer) mCentralSurfaces
                .getNotificationShadeWindowView()
                    .findViewById(R.id.ambient_indication_container);
        ambientIndicationContainer.initializeView(
            mContext, mCentralSurfaces, ambientIndicationContainer, mDelayedWakeLockFactory);
        addService(new AmbientIndicationService(mContext, ambientIndicationContainer, mAlarmManager));
    }

    private void addService(Object obj) {
        if (obj != null) {
            mServices.add(obj);
        }
    }
}
