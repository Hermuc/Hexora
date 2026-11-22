package io.github.abdurazaaqmohammed.domain.player;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpeedBoostPolicyTest {

    @Test public void normalSpeedBoostsToTwoX() {
        assertEquals(2.0f, SpeedBoostPolicy.effectiveSpeed(1.0f, 2.0f), 0.001f);
    }

    @Test public void slowSpeedBoostsToTwoX() {
        assertEquals(2.0f, SpeedBoostPolicy.effectiveSpeed(0.5f, 2.0f), 0.001f);
    }

    @Test public void alreadyFastSpeedIsNotReduced() {
        assertEquals(2.5f, SpeedBoostPolicy.effectiveSpeed(2.5f, 2.0f), 0.001f);
    }

    @Test public void maxSpeedStaysAtMax() {
        assertEquals(3.0f, SpeedBoostPolicy.effectiveSpeed(3.0f, 2.0f), 0.001f);
    }

    @Test public void invalidBaseFallsBackToBoostOrNormal() {
        assertEquals(2.0f, SpeedBoostPolicy.effectiveSpeed(0f, 2.0f), 0.001f);
        assertEquals(1.0f, SpeedBoostPolicy.effectiveSpeed(0f, 0f), 0.001f);
    }

    @Test public void nearestSpeedSnapsToDisplayedLevel() {
        assertEquals(2.0f, SpeedBoostPolicy.nearestSpeed(1.8f), 0.001f);
        assertEquals(2.5f, SpeedBoostPolicy.nearestSpeed(2.4f), 0.001f);
    }

    @Test public void upwardDragIncreasesOneStepPerExtent() {
        assertEquals(2.5f, SpeedBoostPolicy.speedForVerticalDrag(2.0f, -36f, 36f), 0.001f);
    }

    @Test public void downwardDragDecreasesOneStepPerExtent() {
        assertEquals(1.0f, SpeedBoostPolicy.speedForVerticalDrag(2.0f, 72f, 36f), 0.001f);
    }

    @Test public void dragClampsAtBothEnds() {
        assertEquals(3.0f, SpeedBoostPolicy.speedForVerticalDrag(2.0f, -1000f, 36f), 0.001f);
        assertEquals(0.25f, SpeedBoostPolicy.speedForVerticalDrag(2.0f, 1000f, 36f), 0.001f);
    }

    @Test public void speedLabelsKeepTwoDecimalsForQuarterSteps() {
        assertEquals("0.75", SpeedBoostPolicy.formatSpeed(0.75f));
        assertEquals("2", SpeedBoostPolicy.formatSpeed(2.0f));
        assertEquals("2.5", SpeedBoostPolicy.formatSpeed(2.5f));
    }
}
