package com.bxb.sunduk_pay.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for generating unique colors for anonymous users
 * based on their index using the golden angle method.
 */
@Component
public class AnonymousColorCreator {

    /** Full hue circle constant. */
    private static final int FULL_HUE_CIRCLE = 360;
    /** Default saturation value for HSL color.*/
    private static final int DEFAULT_SATURATION = 65;
    /** Default lightness value for HSL color. */
    private static final int DEFAULT_LIGHTNESS = 50;
    /** Divisor constant for percentage calculations.*/
    private static final int PERCENTAGE_DIVISOR = 100;
    /** Hue range constant for 300 degrees. */
    private static final int HUE_RANGE_300 = 300;
    /** Hue range constant for 240 degrees. */
    private static final int HUE_RANGE_240 = 240;
    /** Hue range constant for 180 degrees. */
    private static final int HUE_RANGE_180 = 180;
    /** Hue range constant for 120 degrees.*/
    private static final int HUE_RANGE_120 = 120;
    /** Hue range constant for 60 degrees. */
    private static final int HUE_RANGE_60 = 60;
    /** Size of each hue sector. */
    private static final int HUE_SECTOR_SIZE = 60;

    /** Maximum value for RGB color components. */
    private static final int RGB_MAX_VALUE = 255;

    /** Golden angle in degrees used for color generation. */
    private static final double GOLDEN_ANGLE = 137.508;

   /**
    * Generates a unique color in HEX format based on the provided index.
    * @param index the index to generate the color for.
    * @return a HEX color string.
    */
    public String generateColor(final Long index) {
        double hue = (index * GOLDEN_ANGLE) % FULL_HUE_CIRCLE;
        return hslToHex(hue, DEFAULT_SATURATION, DEFAULT_LIGHTNESS);
    }

    /**
     * Converts HSL color values to HEX format.
     * @param h hue component (0-360)
     * @param s saturation component (0-100)
     * @param l lightness component (0-100)
     * @return a HEX color string
     */
    private String hslToHex(final double h, double s, double l) {
        s /= PERCENTAGE_DIVISOR;
        l /= PERCENTAGE_DIVISOR;

        double c = (1 - Math.abs(2 * l - 1)) * s;
        double x = c * (1 - Math.abs((h / HUE_SECTOR_SIZE) % 2 - 1));
        double m = l - c / 2;

        double r = 0;
        double g = 0;
        double b = 0;

        if (h < HUE_RANGE_60) {
            r = c;
            g = x;
        }
        else if (h < HUE_RANGE_120) {
            r = x;
            g = c;
        }
        else if (h < HUE_RANGE_180) {
            g = c;
            b = x;
        }
        else if (h < HUE_RANGE_240) {
            g = x;
            b = c;
        }
        else if (h < HUE_RANGE_300) {
            r = x;
            b = c;
        }
        else {
            r = c;
            b = x;
        }

        int red = (int) ((r + m) * RGB_MAX_VALUE);
        int green = (int) ((g + m) * RGB_MAX_VALUE);
        int blue = (int) ((b + m) * RGB_MAX_VALUE);

        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
