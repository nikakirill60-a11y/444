package ru.metrocraft.port.mechanics;

import org.junit.Test;
import static org.junit.Assert.*;

public class FilterRulesTest {
    @Test public void donorTimingAndExistingPortCapacityArePreserved() {
        assertEquals(60,FilterRules.REPLACEMENT_TICKS); assertEquals(6000,FilterRules.CAPACITY_TICKS);
        assertEquals(1200,FilterRules.WARNING_TICKS);
    }
    @Test public void onlyPartialOrEmptyFiltersCanBeReplaced() {
        for(int n:new int[]{Integer.MIN_VALUE,-1,0,1,5999})assertTrue(FilterRules.canReplace(n));
        for(int n:new int[]{6000,6001,Integer.MAX_VALUE})assertFalse(FilterRules.canReplace(n));
    }
    @Test public void wireBoundsAndRoundedSecondsCannotOverflow() {
        assertEquals(-1,FilterRules.sanitize(Integer.MIN_VALUE));
        assertEquals(6000,FilterRules.sanitize(Integer.MAX_VALUE));
        assertEquals(0,FilterRules.seconds(-1)); assertEquals(1,FilterRules.seconds(1));
        assertEquals(1,FilterRules.seconds(20)); assertEquals(2,FilterRules.seconds(21));
        assertEquals(300,FilterRules.seconds(Integer.MAX_VALUE));
        assertEquals("05:00",FilterRules.time(6000)); assertEquals("01:00",FilterRules.time(1200));
        assertEquals("00:01",FilterRules.time(1)); assertEquals("00:00",FilterRules.time(0));
    }
    @Test public void lowWarningDistinguishesAbsentAndExpiredMask() {
        assertFalse(FilterRules.low(-1)); assertFalse(FilterRules.low(1201));
        for(int n:new int[]{0,1,1199,1200})assertTrue(FilterRules.low(n));
    }
    @Test public void alarmOnlyOncePerEpisodeRegardlessOfPacketsOrWatchToggling() {
        FilterAlarm alarm=new FilterAlarm();assertFalse(alarm.update(-1,true));assertFalse(alarm.update(6000,true));
        assertTrue(alarm.update(1200,true));
        for(int i=1200;i>=0;i--)assertFalse(alarm.update(i,true));
        assertFalse(alarm.update(0,false));assertFalse(alarm.update(0,true));
        assertFalse(alarm.update(6000,true));assertTrue(alarm.update(0,true));
        assertFalse(alarm.update(-1,true));assertTrue(alarm.update(100,true));
    }
    @Test public void timeOnlyOrMissingWatchCannotConsumeOrPlayAlarm() {
        FilterAlarm alarm=new FilterAlarm();for(int i=0;i<10;i++)assertFalse(alarm.update(500,false));
        assertTrue(alarm.update(500,true));assertFalse(alarm.update(500,true));
    }
}
