package org.jungletree.api.util;

public final class ExtendedMath {

    private ExtendedMath() {}

    public static byte clamp(byte val, byte min, byte max) {
        if (val < min) {
            return min;
        }
        return val > max ? max : val;
    }

    public static int clamp(short val, short min, short max) {
        if (val < min) {
            return min;
        }
        return val > max ? max : val;
    }

    public static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        return Math.min(val, max);
    }

    public static long clamp(long val, long min, long max) {
        if (val < min) {
            return min;
        }
        return Math.min(val, max);
    }

    public static float clamp(float val, float min, float max) {
        if (val < min) {
            return min;
        }
        return Math.min(val, max);
    }

    public static double clamp(double val, double min, double max) {
        if (val < min) {
            return min;
        }
        return Math.min(val, max);
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 1 -> {
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                }
                case 2 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                }
                case 3 -> {
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 4 -> {
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                }
                case 5 -> {
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                }
            }
        }
        return 0xff000000 | r << 16 | g << 8 | b;
    }
}
