package io.github.abdurazaaqmohammed.player;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.SeekBar;
import android.widget.TextView;

import io.github.abdurazaaqmohammed.MPManager.R;
import io.github.abdurazaaqmohammed.domain.player.HorizontalSeekPolicy;
import io.github.abdurazaaqmohammed.domain.player.PlaybackSeek;

/**
 * 单指水平滑动预览 seek 的控制器。
 * 只负责触摸距离、预览与松手提交；真正的 seek 由 PlayerManager 完成。
 */
public final class HorizontalSeekGestureController {
    private final Activity activity;
    private final PlayerManager playerManager;
    private final SeekBar seekBar;
    private final TextView indicator;
    private final float density;
    private final int touchSlop;

    private boolean tracking;
    private boolean active;
    private float downX;
    private float downY;
    private int startPositionMs;
    private int durationMs;
    private int lastTargetMs;
    private int lastDeltaMs;

    public HorizontalSeekGestureController(Activity activity, PlayerManager playerManager,
                                           SeekBar seekBar, TextView indicator) {
        this.activity = activity;
        this.playerManager = playerManager;
        this.seekBar = seekBar;
        this.indicator = indicator;
        this.density = activity.getResources().getDisplayMetrics().density;
        this.touchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
    }

    public void onDown(MotionEvent event) {
        cancel();
        if (!playerManager.isPlayingVideo()) return;
        int duration = playerManager.getDuration();
        if (duration <= 0) return;
        tracking = true;
        downX = event.getX();
        downY = event.getY();
        startPositionMs = playerManager.getCurrentPosition();
        durationMs = duration;
    }

    /**
     * @return true 表示本控制器已接管本次滑动（预览中），Activity 可据此抑制单击/双击。
     */
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!tracking) return false;
        if (e2.getPointerCount() != 1 || e1.getPointerCount() != 1) {
            cancel();
            return false;
        }

        float totalDx = e2.getX() - downX;
        float totalDy = e2.getY() - downY;
        if (!active) {
            if (Math.abs(totalDx) <= touchSlop) return false;
            if (Math.abs(totalDx) <= Math.abs(totalDy) * 1.5f) return false;
            active = true;
        }

        int deltaMs = HorizontalSeekPolicy.deltaMs(
                totalDx, density, HorizontalSeekPolicy.DEFAULT_MS_PER_DP);
        int targetMs = HorizontalSeekPolicy.targetMs(startPositionMs, deltaMs, durationMs);
        lastDeltaMs = deltaMs;
        lastTargetMs = targetMs;
        showPreview(targetMs, deltaMs);
        return true;
    }

    public void commit() {
        if (!active) {
            reset();
            return;
        }
        int targetMs = lastTargetMs;
        reset();
        playerManager.seekTo(targetMs);
    }

    public void cancel() {
        reset();
    }

    public boolean isActive() {
        return active;
    }

    private void showPreview(int targetMs, int deltaMs) {
        if (seekBar != null) {
            seekBar.setProgress(PlaybackSeek.positionToProgress(
                    targetMs, durationMs, seekBar.getMax()));
        }
        if (indicator == null) return;

        String targetText = DateUtils.formatElapsedTime(Math.max(0, targetMs) / 1000L);
        int seconds = Math.round(Math.abs(deltaMs) / 1000f);
        String deltaText = activity.getString(
                deltaMs >= 0 ? R.string.player_seek_forward : R.string.player_seek_backward,
                seconds);
        indicator.setText(activity.getString(R.string.player_seek_preview, targetText, deltaText));
        indicator.setVisibility(View.VISIBLE);
    }

    private void reset() {
        tracking = false;
        active = false;
        lastTargetMs = 0;
        lastDeltaMs = 0;
        if (indicator != null) indicator.setVisibility(View.GONE);
    }
}
