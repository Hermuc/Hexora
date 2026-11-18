package io.github.abdurazaaqmohammed.player;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import io.github.abdurazaaqmohammed.MPManager.R;
import io.github.abdurazaaqmohammed.domain.player.OrientationMode;
import io.github.abdurazaaqmohammed.domain.player.VideoOrientationPolicy;

/**
 * 播放器方向按钮与 Activity 屏幕方向的控制器。
 * 纯 UI/Android 生命周期逻辑；方向判断本身放在
 * {@link VideoOrientationPolicy}，便于单元测试。
 */
public final class PlayerOrientationController {
    private static final String TAG = "PlayerOrientation";
    private static final String PREF_KEY = "player_orientation_mode";

    private final Activity activity;
    private final TextView button;
    private final SharedPreferences prefs;

    private OrientationMode mode;
    private boolean isVideo;
    private boolean controlsLocked;
    private String currentItemPath;
    private int videoWidth;
    private int videoHeight;
    private int videoRotation;
    private boolean hasVideoMetadata;
    private int lastRequestedOrientation = Integer.MIN_VALUE;

    public PlayerOrientationController(Activity activity, TextView button) {
        this.activity = activity;
        this.button = button;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        this.mode = OrientationMode.fromName(prefs.getString(PREF_KEY, OrientationMode.AUTO.name()));
        this.button.setOnClickListener(v -> cycleMode());
    }

    public void onCreate() {
        updateButton();
    }

    public void onMediaItemChanged(MediaItem item) {
        String newPath = item != null ? item.path : null;
        boolean newItem = newPath == null || !newPath.equals(currentItemPath);
        currentItemPath = newPath;

        boolean video = item != null && item.isVideo;
        isVideo = video;

        if (!video) {
            resetVideoDimensions();
            updateButtonVisibility();
            requestOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            return;
        }

        if (newItem) {
            // 新视频的元数据可能稍后才到；先清掉上一个视频的比例，避免串用。
            resetVideoDimensions();
        }
        if (item.videoWidth > 0 && item.videoHeight > 0) {
            videoWidth = item.videoWidth;
            videoHeight = item.videoHeight;
            videoRotation = item.videoRotation;
            hasVideoMetadata = true;
        }
        updateButtonVisibility();
        applyForCurrentVideo();
    }

    /** MediaPlayer 的 onVideoSizeChanged 回调；metadata 可用时优先保留 metadata。 */
    public void onVideoSizeChanged(int width, int height) {
        if (isVideo && width > 0 && height > 0 && !hasVideoMetadata) {
            videoWidth = width;
            videoHeight = height;
            videoRotation = 0;
        }
        applyForCurrentVideo();
    }

    /** 来自 MediaItem 的精确尺寸/旋转信息。 */
    public void onVideoMetadata(int width, int height, int rotation) {
        if (isVideo && width > 0 && height > 0) {
            videoWidth = width;
            videoHeight = height;
            videoRotation = rotation;
            hasVideoMetadata = true;
        }
        applyForCurrentVideo();
    }

    public void onResume() {
        updateButton();
        if (isVideo) applyForCurrentVideo();
    }

    public void onControlsLocked(boolean locked) {
        controlsLocked = locked;
        updateButtonVisibility();
    }

    private void cycleMode() {
        mode = mode.next();
        prefs.edit().putString(PREF_KEY, mode.name()).apply();
        updateButton();
        applyForCurrentVideo();
        Toast.makeText(activity, labelRes(mode), Toast.LENGTH_SHORT).show();
    }

    private void applyForCurrentVideo() {
        if (!isVideo) return;
        VideoOrientationPolicy.OutputOrientation output =
                VideoOrientationPolicy.resolve(mode, videoWidth, videoHeight, videoRotation);
        if (output == null) {
            // 自动模式且尺寸未知：保持当前方向，等 onVideoSizeChanged/metadata。
            return;
        }
        requestOrientation(output == VideoOrientationPolicy.OutputOrientation.LANDSCAPE
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void requestOrientation(int requested) {
        if (requested == lastRequestedOrientation) return;
        lastRequestedOrientation = requested;
        try {
            activity.setRequestedOrientation(requested);
        } catch (RuntimeException error) {
            Log.w(TAG, "Unable to set requested orientation", error);
        }
    }

    private void updateButton() {
        button.setText(labelRes(mode));
        button.setContentDescription(activity.getString(
                R.string.player_orientation_button, activity.getString(labelRes(mode))));
    }

    private void updateButtonVisibility() {
        button.setVisibility(isVideo && !controlsLocked ? View.VISIBLE : View.GONE);
    }

    private void resetVideoDimensions() {
        videoWidth = 0;
        videoHeight = 0;
        videoRotation = 0;
        hasVideoMetadata = false;
    }

    private int labelRes(OrientationMode value) {
        switch (value) {
            case PORTRAIT: return R.string.player_orientation_portrait;
            case LANDSCAPE: return R.string.player_orientation_landscape;
            default: return R.string.player_orientation_auto;
        }
    }
}
