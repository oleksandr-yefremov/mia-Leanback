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
