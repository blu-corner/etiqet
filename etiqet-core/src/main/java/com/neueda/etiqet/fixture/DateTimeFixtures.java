package com.neueda.etiqet.fixture;

import cucumber.api.java.en.Then;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static org.junit.Assert.fail;

public class DateTimeFixtures {

    private static final Logger LOG = LoggerFactory.getLogger(DateTimeFixtures.class);

    @Then("^wait until (\\d{2}):(\\d{2})$")
    public void waitUntilTimeNoSecsNoMillis(int hour, int minute) {
        waitUntilTimeNoMillis(hour, minute, 0);
    }

    @Then("^wait until (\\d{2}):(\\d{2}):(\\d{2})$")
    public void waitUntilTimeNoMillis(int hour, int minute, int seconds) {
        waitUntilTime(hour, minute, seconds, 0);
    }

    @Then("^wait until (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})$")
    public void waitUntilTime(int hour, int minute, int seconds, int millis) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        waitUntilDateAndTime(year, month, day, hour, minute, seconds, millis);
    }

    @Then("^wait until (\\d{4})(\\d{2})(\\d{2})-(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{3})$")
    public void waitUntilDateAndTime(int year, int month, int day, int hour, int minute, int seconds, int millis) {
        Interval interval = new Interval(new DateTime(), new DateTime(year, month, day, hour, minute, seconds, millis));
        if(LOG.isDebugEnabled()) LOG.debug("{}",interval);

        long ms = interval.toDurationMillis();
        if(LOG.isDebugEnabled()) LOG.debug("{}",new DateTime());
        if(LOG.isDebugEnabled()) LOG.debug("Sleeping for {}", ms);

        waitForMilliseconds(ms);
        if(LOG.isDebugEnabled()) LOG.debug("{}", new DateTime());
    }

    @Then("^wait for (\\d+) seconds?$")
    public void waitForSeconds(int seconds) {
        waitForMilliseconds(seconds * 1000L);
    }

    @Then("^wait for (\\d+) milliseconds?$")
    public void waitForMilliseconds(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted waiting");
        }
    }
}
