package io.github.abdurazaaqmohammed.player;

import android.app.Activity;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.github.abdurazaaqmohammed.MPManager.R;
import io.github.abdurazaaqmohammed.domain.player.SpeedBoostPolicy;

/**
 * 长按临时倍速控制器：按住加速，垂直拖动调档，松手恢复。
 * 只改变 PlayerManager 的临时倍速，不改基础倍速或偏好。
 */
public final class SpeedBoostController {
    private final Activity activity;
    private final PlayerManager playerManager;
    private final TextView indicator;
    private final float density;

    private boolean active;
    private float anchorY;
    private float initialSpeed;
    private float currentSpeed;

    public SpeedBoostController(Activity activity, PlayerManager playerManager, TextView indicator) {
        this.activity = activity;
        this.playerManager = playerManager;
        this.indicator = indicator;
        this.density = activity.getResources().getDisplayMetrics().density;
    }

    public boolean start(float anchorY) {
        if (active) return true;
        if (!playerManager.isPlayingVideo()
                || playerManager.getState() != PlayerManager.PlayState.PLAYING) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(activity, R.string.player_speed_boost_unsupported, Toast.LENGTH_SHORT).show();
            return false;
        }

        float baseSpeed = playerManager.getPlaybackSpeed();
        float boostSpeed = SpeedBoostPolicy.effectiveSpeed(
                baseSpeed, SpeedBoostPolicy.DEFAULT_BOOST_SPEED);
        float startSpeed = SpeedBoostPolicy.nearestSpeed(boostSpeed);
        if (!playerManager.setTemporaryPlaybackSpeed(startSpeed)) {
            playerManager.clearTemporaryPlaybackSpeed();
            Toast.makeText(activity, R.string.player_speed_boost_unsupported, Toast.LENGTH_SHORT).show();
            return false;
        }

        active = true;
        this.anchorY = anchorY;
        this.initialSpeed = startSpeed;
        this.currentSpeed = startSpeed;
        showSpeed(startSpeed);
        indicator.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        return true;
    }

    /**
     * 长按期间只接受垂直拖动；水平/多指事件也消费，避免同时触发水平 seek。
     */
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!active) return false;
        onMove(e2);
        return true;
    }

    /**
     * 直接处理原始 ACTION_MOVE。
     * GestureDetector 在长按后会把 ACTION_MOVE 全部拦截，不能依赖 onScroll。
     */
    public void onMove(MotionEvent event) {
        if (!active || event.getPointerCount() != 1) return;
        float verticalDeltaDp = (event.getY() - anchorY) / density;
        float newSpeed = SpeedBoostPolicy.speedForVerticalDrag(
                initialSpeed, verticalDeltaDp, SpeedBoostPolicy.STEP_DP);
        if (newSpeed != currentSpeed) {
            if (playerManager.setTemporaryPlaybackSpeed(newSpeed)) {
                currentSpeed = newSpeed;
                showSpeed(newSpeed);
                indicator.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
            }
        }
    }

    public void stop() {
        if (!active) return;
        active = false;
        anchorY = 0f;
        initialSpeed = 0f;
        currentSpeed = 0f;
        playerManager.clearTemporaryPlaybackSpeed();
        indicator.setVisibility(View.GONE);
    }

    public void cancel() {
        stop();
    }

    public boolean isActive() {
        return active;
    }

    private void showSpeed(float speed) {
        indicator.setText(activity.getString(
                R.string.player_speed_boost, SpeedBoostPolicy.formatSpeed(speed)));
        indicator.setVisibility(View.VISIBLE);
    }
}
