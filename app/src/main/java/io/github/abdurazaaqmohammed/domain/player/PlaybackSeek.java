package io.github.abdurazaaqmohammed.domain.player;

/**
 * 播放进度与 SeekBar 刻度之间的纯整数换算。
 * 仅依赖 Java 标准库，便于 JVM 单测和跨 UI/播放器复用。
 */
public final class PlaybackSeek {
    private PlaybackSeek() {
    }

    /**
     * SeekBar 的 progress（0..max）换算为毫秒。
     * 使用 long 作为中间值，避免长视频的 int 乘法溢出。
     */
    public static int progressToPositionMs(int progress, int max, int durationMs) {
        if (max <= 0 || durationMs <= 0) return 0;
        int safeProgress = Math.max(0, Math.min(progress, max));
        return (int) Math.min(durationMs, (long) safeProgress * durationMs / max);
    }

    /**
     * 毫秒换算为 SeekBar 的 progress（0..max）。
     * 用于播放器进度回调刷新 UI。
     */
    public static int positionToProgress(int positionMs, int durationMs, int max) {
        if (max <= 0 || durationMs <= 0) return 0;
        int safePosition = Math.max(0, Math.min(positionMs, durationMs));
        return (int) Math.min(max, (long) safePosition * max / durationMs);
    }
}
