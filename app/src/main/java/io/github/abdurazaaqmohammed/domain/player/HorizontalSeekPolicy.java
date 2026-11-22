package io.github.abdurazaaqmohammed.domain.player;

/**
 * 水平滑动距离到播放位置的纯策略。
 * 距离按 dp 与固定的毫秒/dp 换算，不读取视频总时长；总时长只用于边界夹取。
 */
public final class HorizontalSeekPolicy {
    public static final float DEFAULT_MS_PER_DP = 150f;

    private HorizontalSeekPolicy() {
    }

    /** 手指水平位移（px）换算为真实时长增量（ms），可正可负。 */
    public static int deltaMs(float distancePx, float density, float msPerDp) {
        if (density <= 0f || msPerDp <= 0f
                || Float.isNaN(distancePx) || Float.isInfinite(distancePx)) {
            return 0;
        }
        return Math.round(distancePx / density * msPerDp);
    }

    /** 以起始位置加增量得到目标位置，并夹取到 [0, durationMs]。 */
    public static int targetMs(int startMs, int deltaMs, int durationMs) {
        if (durationMs <= 0) return 0;
        long target = (long) startMs + deltaMs;
        if (target < 0) return 0;
        if (target > durationMs) return durationMs;
        return (int) target;
    }
}
