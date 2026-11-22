package io.github.abdurazaaqmohammed.domain.player;

/**
 * 长按倍速的纯策略：初始 2x，垂直拖动按离散档位调速度。
 * 档位和 36dp 步进参考 Mangayomi 的 temporary_playback_speed 实现。
 */
public final class SpeedBoostPolicy {
    public static final float DEFAULT_BOOST_SPEED = 2.0f;
    public static final float STEP_DP = 36f;
    public static final float[] TEMPORARY_SPEEDS = {
            3.0f, 2.5f, 2.0f, 1.5f, 1.0f, 0.75f, 0.5f, 0.25f
    };

    private SpeedBoostPolicy() {
    }

    /** 基础倍速高于增强倍速时保持基础倍速，避免长按反而降速。 */
    public static float effectiveSpeed(float baseSpeed, float boostSpeed) {
        if (baseSpeed <= 0f) return boostSpeed > 0f ? boostSpeed : 1f;
        return Math.max(baseSpeed, boostSpeed);
    }

    /** 吸附到最近的临时倍速档位。 */
    public static float nearestSpeed(float speed) {
        float nearest = TEMPORARY_SPEEDS[0];
        for (float candidate : TEMPORARY_SPEEDS) {
            if (Math.abs(candidate - speed) < Math.abs(nearest - speed)) {
                nearest = candidate;
            }
        }
        return nearest;
    }

    /**
     * 根据长按起点开始的垂直位移计算临时倍速。
     * 上滑（verticalDeltaDp 为负）提高速度，下滑降低速度。
     */
    public static float speedForVerticalDrag(
            float initialSpeed, float verticalDeltaDp, float stepDp) {
        if (stepDp <= 0f || Float.isNaN(verticalDeltaDp) || Float.isInfinite(verticalDeltaDp)) {
            return nearestSpeed(initialSpeed);
        }
        float snapped = nearestSpeed(initialSpeed);
        int initialIndex = indexOf(snapped);
        long movedSteps = Math.round(verticalDeltaDp / stepDp);
        long targetIndex = (long) initialIndex + movedSteps;
        if (targetIndex < 0) targetIndex = 0;
        if (targetIndex >= TEMPORARY_SPEEDS.length) targetIndex = TEMPORARY_SPEEDS.length - 1;
        return TEMPORARY_SPEEDS[(int) targetIndex];
    }

    /** HUD 显示用：0.75 -> 0.75，2.0 -> 2，2.5 -> 2.5。 */
    public static String formatSpeed(float speed) {
        String text = String.valueOf(speed);
        if (text.endsWith(".0")) return text.substring(0, text.length() - 2);
        return text;
    }

    private static int indexOf(float speed) {
        for (int i = 0; i < TEMPORARY_SPEEDS.length; i++) {
            if (TEMPORARY_SPEEDS[i] == speed) return i;
        }
        return 0;
    }
}
