/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.server.biometrics.sensors;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.hardware.biometrics.BiometricRequestConstants;
import android.hardware.fingerprint.IUdfpsOverlayController;
import android.platform.test.annotations.Presubmit;
import android.platform.test.flag.junit.SetFlagsRule;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

@Presubmit
@SmallTest
public class SensorOverlaysTest {
    @Rule public final SetFlagsRule mSetFlagsRule = new SetFlagsRule();

    private static final int SENSOR_ID = 11;
    private static final long REQUEST_ID = 8;

    @Rule public final MockitoRule mockito = MockitoJUnit.rule();
    @Mock private IUdfpsOverlayController mUdfpsOverlayController;
    @Mock private AcquisitionClient<?> mAcquisitionClient;

    @Before
    public void setup() {
        when(mAcquisitionClient.getRequestId()).thenReturn(REQUEST_ID);
        when(mAcquisitionClient.hasRequestId()).thenReturn(true);
    }

    @Test
    public void noopWhenBothNull() {
        final SensorOverlays useless = new SensorOverlays(null);
        useless.show(SENSOR_ID, 2, null);
        useless.hide(SENSOR_ID);
    }

    @Test
    public void testProvidesUdfps() {
        final List<IUdfpsOverlayController> udfps = new ArrayList<>();
        SensorOverlays sensorOverlays = new SensorOverlays(null);

        sensorOverlays.ifUdfps(udfps::add);
        assertThat(udfps).isEmpty();

        sensorOverlays = new SensorOverlays(mUdfpsOverlayController);
        sensorOverlays.ifUdfps(udfps::add);
        assertThat(udfps).containsExactly(mUdfpsOverlayController);
    }

    @Test
    public void testShowUdfps() throws Exception {
        testShow(mUdfpsOverlayController);
    }

    private void testShow(IUdfpsOverlayController udfps)
            throws Exception {
        final SensorOverlays sensorOverlays = new SensorOverlays(udfps);
        final int reason = BiometricRequestConstants.REASON_UNKNOWN;
        sensorOverlays.show(SENSOR_ID, reason, mAcquisitionClient);

        if (udfps != null) {
            verify(mUdfpsOverlayController).showUdfpsOverlay(
                    eq(REQUEST_ID), eq(SENSOR_ID), eq(reason), any());
        }
    }

    @Test
    public void testHideUdfps() throws Exception {
        testHide(mUdfpsOverlayController);
    }

    private void testHide(IUdfpsOverlayController udfps)
            throws Exception {
        final SensorOverlays sensorOverlays = new SensorOverlays(udfps);
        sensorOverlays.hide(SENSOR_ID);

        if (udfps != null) {
            verify(mUdfpsOverlayController).hideUdfpsOverlay(eq(SENSOR_ID));
        }
    }
}
