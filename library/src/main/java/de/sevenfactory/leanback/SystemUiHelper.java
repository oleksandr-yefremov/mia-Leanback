package de.sevenfactory.leanback;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.view.View;

class SystemUiHelper implements View.OnSystemUiVisibilityChangeListener {

    // System UI visibility flags
    interface Flags {
        int SHOW = View.SYSTEM_UI_FLAG_LAYOUT_STABLE            // Stay stable during layout changes
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION    // Recalc layout  on navigation bar changes
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;        // Recalc layout  on system UI changes

        int HIDE = View.SYSTEM_UI_FLAG_LAYOUT_STABLE            // Stay stable during layout changes
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION    // Recalc layout on navigation bar changes
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN         // Recalc layout on system UI changes
                | View.SYSTEM_UI_FLAG_FULLSCREEN                // Hide System UI
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;          // Hide navgation bar
    }

    // Auto hide delay
    private static final long DELAY = DateUtils.SECOND_IN_MILLIS * 3;

    private boolean mIsShowing;

    private final boolean  mAutoHide;
    private final View     mDecorView;
    private final Handler  mHandler;
    private final Runnable mHideRunnable;

    private final OnVisibilityChangeListener mListener;

    /* Constructors */

    public SystemUiHelper(Activity activity) {
        this(activity, null);
    }

    public SystemUiHelper(Activity activity, OnVisibilityChangeListener listener) {
        this(activity, listener, true);
    }

    public SystemUiHelper(Activity activity, OnVisibilityChangeListener listener, boolean autoHide) {
        // Init
        mDecorView = activity.getWindow().getDecorView();
        mListener = listener;
        mAutoHide = autoHide;

        // Delayed hide
        mHandler = new Handler(Looper.getMainLooper());
        mHideRunnable = new HideRunnable();

        // Listen for system UI visibility changes
        mDecorView.setOnSystemUiVisibilityChangeListener(this);

        // Defaults
        mIsShowing = true;
    }

    /* Package local */

    void toggle() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    void show() {
        mIsShowing = true;

        // Remove currently queued hide calls
        mHandler.removeCallbacks(mHideRunnable);

        // Set flags
        setFlags(Flags.SHOW);
    }

    void hide() {
        mIsShowing = false;

        // Remove currently queued hide calls
        mHandler.removeCallbacks(mHideRunnable);

        // Set flags
        setFlags(Flags.HIDE);
    }

    void delayHide(long delayMillis) {
        // Remove currently queued hide calls
        mHandler.removeCallbacks(mHideRunnable);

        // Delay new hide call
        mHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    /* Helpers */

    private void setFlags(int flags) {
        mDecorView.setSystemUiVisibility(flags);
    }

    private void notifyListener() {
        if (mListener != null) {
            mListener.onVisibilityChanged(isShowing());
        }
    }

    /* OnSystemUiVisibilityChangeListener */

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        // Update state
        if (!mIsShowing && mAutoHide && ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)) {
            mIsShowing = true;

            // Autohide
            delayHide(DELAY);
        } else {
            mIsShowing = false;
        }

        notifyListener();
    }

    /* HideRunnable */

    private class HideRunnable implements Runnable {
        @Override
        public void run() {
            hide();
        }
    }

    /* OnVisibilityChangeListener */

    public interface OnVisibilityChangeListener {
        void onVisibilityChanged(boolean visible);
    }
}
