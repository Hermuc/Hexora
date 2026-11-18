package io.github.abdurazaaqmohammed.domain.player;

/**
 * 长按倍速的纯策略：基础倍速高于增强倍速时保持基础倍速，避免长按反而降速。
 */
public final class SpeedBoostPolicy {
    public static final float DEFAULT_BOOST_SPEED = 2.0f;

    private SpeedBoostPolicy() {
    }

    public static float effectiveSpeed(float baseSpeed, float boostSpeed) {
        if (baseSpeed <= 0f) return boostSpeed > 0f ? boostSpeed : 1f;
        return Math.max(baseSpeed, boostSpeed);
    }
}
