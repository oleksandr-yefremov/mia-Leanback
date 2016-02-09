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
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class LeanbackLayout extends FrameLayout implements SystemUiHelper.OnVisibilityChangeListener {

    private boolean mIsFullscreen;

    private SystemUiHelper mSystemUiHelper;

    private Rect mWindowInsets;

    private ViewGroup.LayoutParams mEmbeddedLayoutParams;
    private ViewGroup.LayoutParams mFullscreenLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private OnFullscreenChangeListener mListener;

    public LeanbackLayout(Context context) {
        this(context, null);
    }

    public LeanbackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeanbackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Styling
        setBackgroundColor(Color.BLACK);

        // Defaults
        mIsFullscreen = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Get parent activity
        Activity activity = ((Activity) getContext());

        // Create system ui helper
        mSystemUiHelper = new SystemUiHelper(activity, this);

        // Save LayoutParams for embedded mode
        mEmbeddedLayoutParams = getLayoutParams();
    }

    public void setOnFullscreenChangeListener(OnFullscreenChangeListener listener) {
        mListener = listener;
    }

    public void removeOnFullscreenChangeListener() {
        mListener = null;
    }

    /* FullscreenHandling */

    public boolean toggle() {
        if (!mIsFullscreen) {
            enterFullscreen();
        } else {
            exitFullscreen();
        }

        return mIsFullscreen;
    }

    void exitFullscreen() {
        mIsFullscreen = false;

        // Show system UI
        mSystemUiHelper.show();

        // Update layout params
        setLayoutParams(mEmbeddedLayoutParams);
        updateSystemUiPadding();
    }

    void enterFullscreen() {
        mIsFullscreen = true;

        // Hide system UI
        mSystemUiHelper.hide();

        // Update layout params
        setLayoutParams(mFullscreenLayoutParams);
        updateSystemUiPadding();
    }

    public boolean isFullscreen() {
        return mIsFullscreen;
    }

    private void notifiyListener(boolean isSystemUiVisible) {
        // Notify
        if (mListener != null) {
            mListener.onFullscreenChanged(mIsFullscreen, isSystemUiVisible);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        if (mWindowInsets == null) {
            mWindowInsets = insets;
        } else {
            // Do not update top inset if set once
            // That's important if you handle orientation changes by yourself,
            // because the actionbar height can differ and is not correctly updated
            // by the system
            mWindowInsets.right = insets.right;
            mWindowInsets.left = insets.left;
            mWindowInsets.bottom = insets.bottom;
        }

        updateSystemUiPadding();

        return true;
    }

    @SuppressWarnings("deprecation")
    // suppress deprecation for statement is not working (//noinspection deprecation)
    private void updateSystemUiPadding() {
        if (mWindowInsets == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                requestApplyInsets();
            } else {
                requestFitSystemWindows();
            }

            return;
        }

        fitSystemUiOfContainer();
        fitSystemUiOfChildren(getChildAt(0));
    }

    private void fitSystemUiOfContainer() {
        if (mIsFullscreen) {
            // Use full screen
            this.setPadding(0, 0, 0, 0);
        } else {
            // Fits system UI
            this.setPadding(mWindowInsets.left, mWindowInsets.top, mWindowInsets.right, mWindowInsets.bottom);
        }
    }

    /**
     * Traverse all children in view hierarchy and recalculate paddings to fit system UI
     */
    private void fitSystemUiOfChildren(View view) {
        fitSystemOfChild(view);

        if (view instanceof ViewGroup) {
            // Travers children of ViewGroup
            ViewGroup parent = (ViewGroup) view;

            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                fitSystemUiOfChildren(child);
            }
        }
    }

    /**
     * Set padding to fit system UI for a child view.
     * If view has to fit system UI and is in fullscreen -> recalculate padding.
     * Otherwise reset it.
     */
    private void fitSystemOfChild(View view) {
        if (view.getFitsSystemWindows()) {
            if (mIsFullscreen) {
                // Fits system UI in fullscreen mode
                view.setPadding(mWindowInsets.left, mWindowInsets.top, mWindowInsets.right, mWindowInsets.bottom);
            } else {
                // Reset
                view.setPadding(0, 0, 0, 0);
            }
        }
    }

    /* OnVisibilityChangeListener */

    @Override
    public void onVisibilityChanged(boolean visible) {
        notifiyListener(visible);
    }

    /* OnFullscreenChangeListener */

    public interface OnFullscreenChangeListener {
        void onFullscreenChanged(boolean isFullscreen, boolean isSystemUiVisible);
    }
}
