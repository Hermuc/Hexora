package io.github.abdurazaaqmohammed.player;

import android.app.Activity;
import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.github.abdurazaaqmohammed.MPManager.R;
import io.github.abdurazaaqmohammed.domain.player.SpeedBoostPolicy;

/**
 * 长按临时倍速控制器：按住加速，松手恢复。
 * 只改变 PlayerManager 的临时倍速，不改基础倍速或偏好。
 */
public final class SpeedBoostController {
    private final Activity activity;
    private final PlayerManager playerManager;
    private final TextView indicator;

    private boolean active;

    public SpeedBoostController(Activity activity, PlayerManager playerManager, TextView indicator) {
        this.activity = activity;
        this.playerManager = playerManager;
        this.indicator = indicator;
    }

    public boolean start() {
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
        if (!playerManager.setTemporaryPlaybackSpeed(boostSpeed)) {
            playerManager.clearTemporaryPlaybackSpeed();
            Toast.makeText(activity, R.string.player_speed_boost_unsupported, Toast.LENGTH_SHORT).show();
            return false;
        }

        active = true;
        indicator.setText(activity.getString(R.string.player_speed_boost, boostSpeed));
        indicator.setVisibility(View.VISIBLE);
        indicator.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        return true;
    }

    public void stop() {
        if (!active) return;
        active = false;
        playerManager.clearTemporaryPlaybackSpeed();
        indicator.setVisibility(View.GONE);
    }

    public void cancel() {
        stop();
    }

    public boolean isActive() {
        return active;
    }
}
