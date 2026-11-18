package io.github.abdurazaaqmohammed.domain.player;

/**
 * 播放器的屏幕方向模式。
 */
public enum OrientationMode {
    AUTO,
    PORTRAIT,
    LANDSCAPE;

    /** 点击按钮时的循环顺序：自动 -> 固定竖屏 -> 固定横屏 -> 自动。 */
    public OrientationMode next() {
        switch (this) {
            case AUTO: return PORTRAIT;
            case PORTRAIT: return LANDSCAPE;
            default: return AUTO;
        }
    }

    /** 从 SharedPreferences 保存的名称恢复；非法值回退 AUTO。 */
    public static OrientationMode fromName(String name) {
        if (name != null) {
            for (OrientationMode mode : values()) {
                if (mode.name().equals(name)) return mode;
            }
        }
        return AUTO;
    }
}
