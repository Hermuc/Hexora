package io.github.abdurazaaqmohammed.domain.player;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class VideoOrientationPolicyTest {

    @Test public void autoUsesLandscapeForWideVideo() {
        assertEquals(VideoOrientationPolicy.OutputOrientation.LANDSCAPE,
                VideoOrientationPolicy.resolve(OrientationMode.AUTO, 1920, 1080, 0));
    }

    @Test public void autoUsesPortraitForTallVideo() {
        assertEquals(VideoOrientationPolicy.OutputOrientation.PORTRAIT,
                VideoOrientationPolicy.resolve(OrientationMode.AUTO, 1080, 1920, 0));
    }

    @Test public void squareVideoIsPortrait() {
        assertEquals(VideoOrientationPolicy.OutputOrientation.PORTRAIT,
                VideoOrientationPolicy.resolve(OrientationMode.AUTO, 1080, 1080, 0));
    }

    @Test public void rotationMetadataSwapsDimensions() {
        assertEquals(VideoOrientationPolicy.OutputOrientation.PORTRAIT,
                VideoOrientationPolicy.resolve(OrientationMode.AUTO, 1920, 1080, 90));
        assertEquals(VideoOrientationPolicy.OutputOrientation.LANDSCAPE,
                VideoOrientationPolicy.resolve(OrientationMode.AUTO, 1080, 1920, 270));
    }

    @Test public void fixedModesIgnoreVideoDimensions() {
        assertEquals(VideoOrientationPolicy.OutputOrientation.PORTRAIT,
                VideoOrientationPolicy.resolve(OrientationMode.PORTRAIT, 1920, 1080, 0));
        assertEquals(VideoOrientationPolicy.OutputOrientation.LANDSCAPE,
                VideoOrientationPolicy.resolve(OrientationMode.LANDSCAPE, 1080, 1920, 0));
    }

    @Test public void unknownDimensionsKeepCurrentOrientation() {
        assertNull(VideoOrientationPolicy.resolve(OrientationMode.AUTO, 0, 0, 0));
        assertNull(VideoOrientationPolicy.resolve(OrientationMode.AUTO, -1, 1080, 0));
    }

    @Test public void modeCycleOrderIsAutoPortraitLandscape() {
        assertEquals(OrientationMode.PORTRAIT, OrientationMode.AUTO.next());
        assertEquals(OrientationMode.LANDSCAPE, OrientationMode.PORTRAIT.next());
        assertEquals(OrientationMode.AUTO, OrientationMode.LANDSCAPE.next());
    }

    @Test public void invalidPreferenceFallsBackToAuto() {
        assertEquals(OrientationMode.AUTO, OrientationMode.fromName(null));
        assertEquals(OrientationMode.AUTO, OrientationMode.fromName("NOT_A_MODE"));
        assertEquals(OrientationMode.PORTRAIT, OrientationMode.fromName("PORTRAIT"));
    }
}
