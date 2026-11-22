package io.github.abdurazaaqmohammed.core.ui.util;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * Activity 级系统栏控制器：集中处理状态栏隐藏/恢复、API 兼容与焦点重应用。
 * 只做系统栏控制，不依赖播放器业务，便于全屏页面复用。
 */
public final class SystemUiController {
    private final Activity activity;
    private final WindowInsetsControllerCompat controller;
    private boolean statusBarHidden;

    public SystemUiController(Activity activity) {
        this.activity = activity;
        Window window = activity.getWindow();
        this.controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (this.controller != null) {
            this.controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    public void setStatusBarHidden(boolean hidden) {
        statusBarHidden = hidden;
        apply();
    }

    public void showStatusBar() {
        setStatusBarHidden(false);
    }

    /** 配置变化/从后台恢复后重新应用当前状态。 */
    public void reapply() {
        apply();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && statusBarHidden) {
            apply();
        }
    }

    public boolean isStatusBarHidden() {
        return statusBarHidden;
    }

    private void apply() {
        if (activity.isFinishing()) return;
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && controller != null) {
            if (statusBarHidden) {
                controller.hide(WindowInsetsCompat.Type.statusBars());
            } else {
                controller.show(WindowInsetsCompat.Type.statusBars());
            }
        } else {
            // API 19/20 fallback, or a controller unavailable on an OEM build.
            if (statusBarHidden) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        ViewCompat.requestApplyInsets(window.getDecorView());
    }
}
