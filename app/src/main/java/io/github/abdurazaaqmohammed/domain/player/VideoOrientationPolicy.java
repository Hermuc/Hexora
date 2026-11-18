package io.github.abdurazaaqmohammed.domain.player;

/**
 * 根据方向模式和视频显示尺寸决定播放器的屏幕方向。
 * 纯 Java，无 Android 依赖，便于 JVM 单测和未来复用。
 */
public final class VideoOrientationPolicy {
    public enum OutputOrientation { PORTRAIT, LANDSCAPE }

    private VideoOrientationPolicy() {
    }

    /**
     * @param mode            当前方向模式
     * @param width           视频原始宽度
     * @param height          视频原始高度
     * @param rotationDegrees 容器旋转角（0/90/180/270），用于纠正竖屏拍摄视频
     * @return 目标方向；null 表示尺寸未知，保持当前方向
     */
    public static OutputOrientation resolve(
            OrientationMode mode, int width, int height, int rotationDegrees) {
        if (mode == OrientationMode.PORTRAIT) return OutputOrientation.PORTRAIT;
        if (mode == OrientationMode.LANDSCAPE) return OutputOrientation.LANDSCAPE;
        if (width <= 0 || height <= 0) return null;

        int rotation = ((rotationDegrees % 360) + 360) % 360;
        int displayWidth = width;
        int displayHeight = height;
        if (rotation == 90 || rotation == 270) {
            displayWidth = height;
            displayHeight = width;
        }
        if (displayWidth <= 0 || displayHeight <= 0) return null;

        // 宽大于高为横屏；正方形按竖屏处理，避免手机上出现异常的大横屏。
        return displayWidth > displayHeight
                ? OutputOrientation.LANDSCAPE
                : OutputOrientation.PORTRAIT;
    }
}
