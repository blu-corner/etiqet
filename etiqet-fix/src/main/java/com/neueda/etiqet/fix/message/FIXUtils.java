package com.neueda.etiqet.fix.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public final class FIXUtils {

    public static final Logger LOG = LoggerFactory.getLogger(FIXUtils.class);
    public static final String SOH_STR = "\u0001";
    public static final char SOH_CHR = '\u0001';
    public static final String LOG_SEPARATOR = "|";
    public static final String TAG_VALUE_SEPARATOR = "=";
    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter
        .ofPattern("yyyyMMdd-HH:mm:ss.SSS");
    private static Random random = new Random();

    public static String getDateTime() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        return dateTimeFormat.format(utc);
    }

    public static String getDateTime(String secondsOffset) {
        LocalDateTime local = LocalDateTime.parse(getDateTime(), dateTimeFormat);
        local = local.plusSeconds(Long.parseLong(secondsOffset));
        return dateTimeFormat.format(local);
    }

    public static String genClientOrderID() {
        return getDateTime() + random.nextInt(10000) + 1;
    }

}
