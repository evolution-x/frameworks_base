/*
 * Copyright (C) 2020 The Android Open Source Project
 * Copyright (C) 2023 Paranoid Android
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

package com.android.server.biometrics.sensors.face.sense;

import android.annotation.NonNull;
import android.content.Context;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Slog;

import com.android.server.biometrics.BiometricsProto;
import com.android.server.biometrics.log.BiometricContext;
import com.android.server.biometrics.log.BiometricLogger;
import com.android.server.biometrics.sensors.ClientMonitorCallback;
import com.android.server.biometrics.sensors.HalClientMonitor;

import java.io.File;
import java.util.Map;
import java.util.function.Supplier;

import vendor.aospa.biometrics.face.ISenseService;

public class FaceUpdateActiveUserClient extends HalClientMonitor<ISenseService> {
    private static final String TAG = "FaceUpdateActiveUserClient";
    private static final String FACE_DATA_DIR = "facedata";

    private final boolean mHasEnrolledBiometrics;
    @NonNull private final Map<Integer, Long> mAuthenticatorIds;

    FaceUpdateActiveUserClient(@NonNull Context context,
            @NonNull Supplier<ISenseService> lazyDaemon, int userId, @NonNull String owner,
            int sensorId, @NonNull BiometricLogger logger,
            @NonNull BiometricContext biometricContext, boolean hasEnrolledBiometrics,
            @NonNull Map<Integer, Long> authenticatorIds) {
        super(context, lazyDaemon, null /* token */, null /* listener */, userId, owner,
                0 /* cookie */, sensorId, logger, biometricContext,
                false /* isMandatoryBiometrics */);
        mHasEnrolledBiometrics = hasEnrolledBiometrics;
        mAuthenticatorIds = authenticatorIds;
    }

    @Override
    public void start(@NonNull ClientMonitorCallback callback) {
        super.start(callback);
        startHalOperation();
    }

    @Override
    public void unableToStart() {
        // Nothing to do here
    }

    @Override
    protected void startHalOperation() {
        try {
            final ISenseService daemon = getFreshDaemon();
            mAuthenticatorIds.put(getTargetUserId(),
                    mHasEnrolledBiometrics ? daemon.getAuthenticatorId() : 0L);
            mCallback.onClientFinished(this, true /* success */);
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to setActiveUser: " + e);
            mCallback.onClientFinished(this, false /* success */);
        }
    }

    @Override
    public int getProtoEnum() {
        return BiometricsProto.CM_UPDATE_ACTIVE_USER;
    }
}
