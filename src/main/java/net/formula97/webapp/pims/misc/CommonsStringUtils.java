package net.formula97.webapp.pims.misc;

/**
 * Created by f97one on 2016/12/20.
 */
public class CommonsStringUtils {

    private CommonsStringUtils() {
    }

    public static boolean isNullOrEmpty(String test) {
        return test == null || test.length() == 0;
    }

    public static boolean isNullOrWhiteSpace(String test) {
        return test == null || test.trim().length() == 0;
    }
}
