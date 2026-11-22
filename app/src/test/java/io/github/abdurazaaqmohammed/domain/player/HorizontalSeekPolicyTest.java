package io.github.abdurazaaqmohammed.domain.player;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HorizontalSeekPolicyTest {

    @Test public void distanceUsesRealMillisecondsPerDp() {
        assertEquals(7_500, HorizontalSeekPolicy.deltaMs(100f, 2.0f, 150f));
        assertEquals(54_000, HorizontalSeekPolicy.deltaMs(720f, 2.0f, 150f));
    }

    @Test public void leftDistanceProducesNegativeDelta() {
        assertEquals(-15_000, HorizontalSeekPolicy.deltaMs(-200f, 2.0f, 150f));
    }

    @Test public void longerDistanceProducesLargerAbsoluteDelta() {
        int shortDelta = HorizontalSeekPolicy.deltaMs(50f, 2.0f, 150f);
        int longDelta = HorizontalSeekPolicy.deltaMs(400f, 2.0f, 150f);
        assertTrue(Math.abs(longDelta) > Math.abs(shortDelta));
    }

    @Test public void targetIsClampedToVideoBounds() {
        assertEquals(0, HorizontalSeekPolicy.targetMs(10_000, -30_000, 120_000));
        assertEquals(120_000, HorizontalSeekPolicy.targetMs(100_000, 50_000, 120_000));
        assertEquals(90_000, HorizontalSeekPolicy.targetMs(60_000, 30_000, 120_000));
    }

    @Test public void invalidValuesAreSafe() {
        assertEquals(0, HorizontalSeekPolicy.deltaMs(100f, 0f, 150f));
        assertEquals(0, HorizontalSeekPolicy.deltaMs(100f, 2.0f, 0f));
        assertEquals(0, HorizontalSeekPolicy.targetMs(60_000, 30_000, 0));
    }
}
