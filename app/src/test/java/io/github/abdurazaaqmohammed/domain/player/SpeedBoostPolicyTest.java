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
}
