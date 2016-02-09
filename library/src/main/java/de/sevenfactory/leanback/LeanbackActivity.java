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

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * An full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction and
 * resizes the content.
 */
public abstract class LeanbackActivity extends AppCompatActivity implements OnFullscreenChangeListener, OnSystemUiChangeListener {

    private boolean mForceLandscape = false;

    private LeanbackLayout mContainer;
    private RotationHelper mRotationHelper;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        init();

        LayoutInflater.from(this).inflate(layoutResID, mContainer, true);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, view.getLayoutParams());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        init();
        addView(view);
    }

    private void init() {
        super.setContentView(R.layout.activity_leanback);

        mContainer = (LeanbackLayout) findViewById(R.id.leanback_container);
    }

    private void addView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();

        if (params == null) {
            mContainer.addView(view);
        } else {
            mContainer.addView(view, params);
        }
    }

    /* Lifecycle handling */

    @Override
    protected void onStart() {
        super.onStart();

        mContainer.setOnFullscreenChangeListener(this);
        mContainer.setOnSystemUiChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRotationHelper != null) {
            mRotationHelper.resume();
        }
    }

    @Override
    protected void onPause() {
        if (mRotationHelper != null) {
            mRotationHelper.pause();
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        mContainer.removeOnFullscreenChangeListener();
        mContainer.removeOnSystemUiChangeListener();

        super.onStop();
    }

    /* Fullscreen */

    /**
     * Toggle fullscreen mode. If you want to know the current state,
     * pull it with {@link #isFullscreen()} or get it pushed by overriding
     * {@link #onFullscreenChanged(boolean)} (boolean)}.
     */
    protected final boolean toggleFullscreen() {
        return mContainer.toggle();
    }

    /**
     * @return true  - if activity is in fullscreen mode
     *         false - if activity is not in fullscreen mode
     */
    protected final boolean isFullscreen() {
        return mContainer.isFullscreen();
    }

    /**
     * Configure the activity to force the layout to rotate into landscape
     * when it enters fullscreen mode.
     */
    protected final void forceLandscapeInFullscreen(boolean shouldForce) {
        mForceLandscape = shouldForce;
    }

    /**
     * Configure the activity that it listens to rotation changes and go
     * instantly to fullscreen if device is in landscape mode.
     */
    protected final void forceFullscreenOnLandscape(boolean shouldGoFullscreenOnLandscape) {
        if (shouldGoFullscreenOnLandscape) {
            mRotationHelper = new RotationHelper(this);
        }
    }

    /**
     * Override this method if you want to react to fullscreen changes
     */
    @CallSuper
    @Override
    public void onFullscreenChanged(boolean isFullscreen) {
        if (mForceLandscape && isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    /**
     * Override this method if you want to react to system ui changes
     */
    @Override
    public void onSystemUiChanged(boolean isSystemUiVisible) {

    }

    /**
     * Override this method if you want to react to orientation and other configuration changes
     */
    @CallSuper
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mRotationHelper != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mContainer.enterFullscreen();
            } else {
                mContainer.exitFullscreen();
            }
        }
    }
}
