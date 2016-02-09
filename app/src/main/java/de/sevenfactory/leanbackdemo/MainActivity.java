package de.sevenfactory.leanbackdemo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

import de.sevenfactory.leanback.LeanbackActivity;

public class MainActivity extends LeanbackActivity {

    private MenuItem  mFullscreenButton;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Leanback experience
        forceFullscreenOnLandscape(true);

        // Video View
        mVideoView = (VideoView) findViewById(R.id.video_view);
    }

    @Override
    protected void onPause() {
        mVideoView.pause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoView.setVideoPath(getString(R.string.video_url));
        mVideoView.start();
    }

    /* Menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        mFullscreenButton = menu.findItem(R.id.menu_fullscreen);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fullscreen:
                toggleFullscreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* OnFullscreenChangeListener */

    @Override
    public void onFullscreenChanged(boolean isFullscreen) {
        super.onFullscreenChanged(isFullscreen);

        if (isFullscreen) {
            mFullscreenButton.setIcon(R.drawable.ic_fullscreen_exit_white_36dp);
        } else {
            mFullscreenButton.setIcon(R.drawable.ic_fullscreen_white_36dp);
        }
    }
}
