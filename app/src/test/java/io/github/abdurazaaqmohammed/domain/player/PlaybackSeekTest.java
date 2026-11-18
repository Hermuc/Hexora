package io.github.abdurazaaqmohammed.domain.player;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Regression tests for the long-video SeekBar overflow.
 * These cases fail with the old int expression:
 *   seekTo(sb.getProgress() * dur / 1000)
 */
public class PlaybackSeekTest {
    private static final int LONG_VIDEO_MS = 12_157_333; // 3h22m37s

    @Test public void fullProgressOnLongVideoDoesNotOverflow() {
        assertEquals(LONG_VIDEO_MS,
                PlaybackSeek.progressToPositionMs(1000, 1000, LONG_VIDEO_MS));
    }

    @Test public void halfProgressOnLongVideoDoesNotOverflow() {
        assertEquals(6_078_666,
                PlaybackSeek.progressToPositionMs(500, 1000, LONG_VIDEO_MS));
    }

    @Test public void reportedDragPositionMapsToExactTarget() {
        assertEquals(10_151_373,
                PlaybackSeek.progressToPositionMs(835, 1000, LONG_VIDEO_MS));
    }

    @Test public void twoHourVideoBoundaryIsSafe() {
        assertEquals(2_145_600,
                PlaybackSeek.progressToPositionMs(298, 1000, 7_200_000));
        assertEquals(2_152_800,
                PlaybackSeek.progressToPositionMs(299, 1000, 7_200_000));
    }

    @Test public void longVideoBoundaryIsSafe() {
        assertEquals(2_139_690,
                PlaybackSeek.progressToPositionMs(176, 1000, LONG_VIDEO_MS));
        assertEquals(2_151_847,
                PlaybackSeek.progressToPositionMs(177, 1000, LONG_VIDEO_MS));
    }

    @Test public void maxDurationStillWorksWithLongIntermediate() {
        assertEquals(Integer.MAX_VALUE,
                PlaybackSeek.progressToPositionMs(1000, 1000, Integer.MAX_VALUE));
    }

    @Test public void progressIsClampedToRange() {
        assertEquals(0, PlaybackSeek.progressToPositionMs(-1, 1000, 1000));
        assertEquals(1000, PlaybackSeek.progressToPositionMs(2000, 1000, 1000));
    }

    @Test public void invalidScalesReturnZero() {
        assertEquals(0, PlaybackSeek.progressToPositionMs(500, 0, 1000));
        assertEquals(0, PlaybackSeek.progressToPositionMs(500, 1000, 0));
        assertEquals(0, PlaybackSeek.positionToProgress(500, 0, 1000));
    }

    @Test public void positionToProgressHandlesBounds() {
        assertEquals(0, PlaybackSeek.positionToProgress(-1, LONG_VIDEO_MS, 1000));
        assertEquals(1000, PlaybackSeek.positionToProgress(LONG_VIDEO_MS, LONG_VIDEO_MS, 1000));
        assertEquals(1000, PlaybackSeek.positionToProgress(Integer.MAX_VALUE, LONG_VIDEO_MS, 1000));
        assertEquals(500, PlaybackSeek.positionToProgress(6_078_667, LONG_VIDEO_MS, 1000));
    }

    @Test public void exactThirtyFiveMinuteBoundaryIsSafe() {
        int duration = 2_147_483; // just under the 32-bit multiplication limit at max=1000
        assertEquals(duration, PlaybackSeek.progressToPositionMs(1000, 1000, duration));
        assertEquals(2_147_484, PlaybackSeek.progressToPositionMs(1000, 1000, 2_147_484));
    }
}
