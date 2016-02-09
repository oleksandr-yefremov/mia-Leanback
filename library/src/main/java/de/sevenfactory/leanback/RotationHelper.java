/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 ProSiebenSat.1 Digital GmbH
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.sevenfactory.leanback;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

class RotationHelper implements SensorEventListener {
    private final static float MIN_ROTATION = 0.8f;
    
    private int mCurrentOrientation;
    
    private Activity      mActivity;
    private SensorManager mSensorManager;
    private Sensor        mSensor;
    
    public RotationHelper(Activity activity) {
        mActivity = activity;
        
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }
    
    public void resume() {
        if (mSensorManager != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    
    public void pause() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this, mSensor);
        }
    }
    
    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        float x = sensorEvent.values[0] / SensorManager.GRAVITY_EARTH;
        float y = sensorEvent.values[1] / SensorManager.GRAVITY_EARTH;
        
        // Rotate only on large movements
        if (Math.abs(x) > MIN_ROTATION || Math.abs(y) > MIN_ROTATION) {
            int orientation = getOrientation(x, y);
            if (orientation != mCurrentOrientation) {
                // The returned orientation isn't used
                // Instead, the orientation is reset to the user preference
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                mCurrentOrientation = orientation;
            }
        }
    }
    
    @Override
    public void onAccuracyChanged(final Sensor sensor, final int i) {
        // Not used
    }
    
    /**
     * Returns a rotation for given x,y orientation parameters
     */
    private int getOrientation(float x, float y) {
        if (Math.abs(x) > Math.abs(y)) {
            return x > 0
                    ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else {
            return y > 0
                    ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }
    }


}
