package io.github.abdurazaaqmohammed.player;

import android.net.Uri;

public class MediaItem {
    public final Uri uri;
    public final String path;
    public final String title;
    public final String artist;
    public final String album;
    public final long duration;
    public final boolean isVideo;

    /** 视频原始宽度；音频或元数据不可用时为 0。 */
    public final int videoWidth;
    /** 视频原始高度；音频或元数据不可用时为 0。 */
    public final int videoHeight;
    /** 视频容器旋转角（0/90/180/270）；音频或未读取时为 0。 */
    public final int videoRotation;

    public MediaItem(Uri uri, String path, String title, String artist, String album, long duration, boolean isVideo) {
        this(uri, path, title, artist, album, duration, isVideo, 0, 0, 0);
    }

    public MediaItem(Uri uri, String path, String title, String artist, String album, long duration,
                     boolean isVideo, int videoWidth, int videoHeight, int videoRotation) {
        this.uri = uri;
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.isVideo = isVideo;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.videoRotation = videoRotation;
    }
}
