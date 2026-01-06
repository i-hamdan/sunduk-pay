package com.bxb.sunduk_pay.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for generating unique colors for anonymous users
 * based on their index using the golden angle method.
 */
@Component
public class AnonymousColorCreator {

    /** Golden angle in degrees used for color generation */
    private static final double GOLDEN_ANGLE = 137.508;

   /**
    * Generates a unique color in HEX format based on the provided index.
    * @param index the index to generate the color for
    * @return a HEX color string
    */
    public String generateColor(Long index) {
        double hue = (index * GOLDEN_ANGLE) % 360;
        return hslToHex(hue, 65, 50);
    }

    /**
     * Converts HSL color values to HEX format.
     * @param h hue component (0-360)
     * @param s saturation component (0-100)
     * @param l lightness component (0-100)
     * @return a HEX color string
     */
    private String hslToHex(double h, double s, double l) {
        s /= 100;
        l /= 100;

        double c = (1 - Math.abs(2 * l - 1)) * s;
        double x = c * (1 - Math.abs((h / 60) % 2 - 1));
        double m = l - c / 2;

        double r = 0, g = 0, b = 0;

        if (h < 60)      { r = c; g = x; }
        else if (h < 120){ r = x; g = c; }
        else if (h < 180){ g = c; b = x; }
        else if (h < 240){ g = x; b = c; }
        else if (h < 300){ r = x; b = c; }
        else             { r = c; b = x; }

        int R = (int) ((r + m) * 255);
        int G = (int) ((g + m) * 255);
        int B = (int) ((b + m) * 255);

        return String.format("#%02X%02X%02X", R, G, B);
    }
}
