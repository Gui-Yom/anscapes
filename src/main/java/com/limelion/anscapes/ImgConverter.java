package com.limelion.anscapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import static com.limelion.anscapes.Anscapes.Colors;

/**
 * A translation of <a href="https://github.com/fenwick67/term-px">https://github.com/fenwick67/term-px</a> from JS to
 * Java and optimized a bit. It permits conversion from image to ansi codes sequences, allowing to draw images on the
 * terminal.
 */
public class ImgConverter {

    /**
     * Chars used to output image. They will hold colors.
     */
    private static final char FULL_CHAR = '\u2588',
        TOP_CHAR = '\u2580',
        BOTTOM_CHAR = '\u2584',
        EMPTY_CHAR = ' ';

    private int ditherThreshold;
    // Smooth the image
    private Scaling scaling;
    // Color mode
    private ColorMode colorMode;
    // Resize at 1/scale
    private float scale;

    /**
     * Use builder instead
     *
     * @param builder
     */
    private ImgConverter(Builder builder) {

        this.colorMode = builder.colorMode;
        this.scale = builder.scale;
        this.scaling = builder.scaling;
        this.ditherThreshold = builder.ditherThreshold;
    }

    public static Builder builder() {

        return new Builder();
    }

    /**
     * Escape to allow copy paste. Useful for commands like 'echo -e'
     *
     * @param s
     *
     * @return the escaped code
     */
    public static String escape(String s) {

        return s.replaceAll("\n/g", "\\n").replaceAll("\r/g", "\\r").replaceAll("\033/g", "\\033");
    }

    public static BufferedImage resize(BufferedImage original, float scale) {

        int width = original.getWidth();
        int height = original.getHeight();
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        int[] rawInput = new int[width * height];
        original.getRGB(0, 0, width, height, rawInput, 0, width);

        int[] rawOutput = new int[newWidth * newHeight];

        // YD compensates for the x loop by subtracting the width back out
        int YD = (height / newHeight) * width - width;
        int YR = height % newHeight;
        int XD = width / newWidth;
        int XR = width % newWidth;
        int outOffset = 0;
        int inOffset = 0;

        for (int y = newHeight, YE = 0; y > 0; y--) {
            for (int x = newWidth, XE = 0; x > 0; x--) {
                rawOutput[outOffset++] = rawInput[inOffset];
                inOffset += XD;
                XE += XR;
                if (XE >= newWidth) {
                    XE -= newWidth;
                    inOffset++;
                }
            }
            inOffset += YD;
            YE += YR;
            if (YE >= newHeight) {
                YE -= newHeight;
                inOffset += width;
            }
        }

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                resized.setRGB(x, y, rawOutput[y * newWidth + x]);
            }
        }

        return resized;
    }

    public int ditherThreshold() {

        return ditherThreshold;
    }

    public Scaling scaling() {

        return scaling;
    }

    public ColorMode getColorMode() {

        return colorMode;
    }

    public float reductionScale() {

        return scale;
    }

    /**
     * Convert an image to a sequence of ansi codes.
     *
     * @param image the image to convert
     *
     * @return a sequence of ansi codes in the form of a String
     */
    public TextImage convert(BufferedImage image) {

        // Resize image
        long startTime = System.nanoTime();

        int width = (int) (image.getWidth() * scale);
        int height = (int) (image.getHeight() * scale);

        // TODO image resize & pixel extraction

        if (scale != 1)
            if (scaling == Scaling.ALT)
                image = resize(image, scale);
            else {
                Image resized = image.getScaledInstance(width, height, scaling == Scaling.SMOOTH ? Image.SCALE_SMOOTH : Image.SCALE_FAST);
                image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.drawImage(resized, 0, 0, null);
                g.dispose();
            }

        System.out.printf("Rescaling time : %f µs%n", (System.nanoTime() - startTime) / 1000.0f);

        startTime = System.nanoTime();
        // Get an array containing all pixels samples of size w * h
        int[] data = image.getRGB(0, 0, width, height, null, 0, width);

        System.out.printf("Pixel extraction time : %f µs%n", (System.nanoTime() - startTime) / 1000.0f);

        startTime = System.nanoTime();

        /*
        System.out.println("Num elems : " + data.length);
        System.out.println("Num pixels : " + width * height);
        System.out.println("Expected elems : " + width * height * 3);
         */

        int i = 0;
        Pixel lastRPixel = null;
        StringBuilder converted = new StringBuilder();

        while (i < data.length) {

            Color topPixel = new Color(data[i]);
            Color bottomPixel = null;

            // Check if another line exists, else assume black
            if (i + width < data.length) {
                bottomPixel = new Color(data[i + width]);
            } else {
                bottomPixel = Color.BLACK;
            }

            // Code for current rendered pixel
            Pixel currentRPixel = null;

            // TODO add more reset options (after each line of after each char)

            if (colorMode == ColorMode.ANSI) {

                Colors topColor = Anscapes.findNearestColor(topPixel, ditherThreshold);
                Colors topBgColor = null;
                Colors bottomColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);
                Colors bottomBgColor = null;

                // If top pixel is the same as bottom pixel
                if (topColor == bottomColor) {

                    if (topColor == Colors.BLACK)
                        currentRPixel = new Pixel(null, Colors.BLACK, EMPTY_CHAR);
                    else
                        currentRPixel = new Pixel(topColor, null, FULL_CHAR);

                } else {

                    bottomBgColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);

                    if (topColor == Colors.BLACK || bottomBgColor == Colors.WHITE_BRIGHT) {
                        currentRPixel = new Pixel(bottomColor, topColor, BOTTOM_CHAR);
                    } else
                        currentRPixel = new Pixel(topColor, bottomBgColor, TOP_CHAR);

                }

            } else if (colorMode == ColorMode.RGB) {

                AnsiColor topColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                AnsiColor topBgColor = null;
                AnsiColor bottomColor = null;
                AnsiColor bottomBgColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());

                if (topPixel.equals(bottomPixel)) {

                    if (topPixel.equals(Colors.BLACK.color()))
                        currentRPixel = new Pixel(null, Colors.BLACK, EMPTY_CHAR);
                    else
                        currentRPixel = new Pixel(topColor, null, FULL_CHAR);

                } else {

                    if (topPixel.equals(Colors.BLACK.color()) || bottomPixel.equals(Colors.WHITE_BRIGHT.color())) {
                        bottomColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());
                        topBgColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                        currentRPixel = new Pixel(bottomColor, topBgColor, BOTTOM_CHAR);
                    } else
                        currentRPixel = new Pixel(topColor, bottomBgColor, TOP_CHAR);

                }
            } else { }

            // Save space if last code equals current code
            // This is rly important
            if (currentRPixel.colorsEquals(lastRPixel))
                converted.append(currentRPixel.getClear());
            else
                converted.append(currentRPixel);

            lastRPixel = currentRPixel;

            ++i;
            if (i % width == 0) {
                converted.append(Anscapes.RESET).append(System.lineSeparator());
                i += width;
                lastRPixel = null;
            }
        }
        System.out.printf("Conversion time : %f µs%n", (System.nanoTime() - startTime) / 1000.0f);

        return new TextImage(converted.toString(), width, height, colorMode);
    }

    public enum Scaling {

        FAST,
        SMOOTH,
        ALT
    }

    /**
     * Utility class to create an ImgConverter
     */
    public static class Builder {

        private Scaling scaling = Scaling.FAST;
        private ColorMode colorMode = ColorMode.ANSI;
        private float scale = 0.25f;
        private int ditherThreshold = 5;

        public Builder scaling(Scaling scaling) {

            this.scaling = scaling;
            return this;
        }

        public Builder mode(ColorMode colorMode) {

            this.colorMode = colorMode;
            return this;
        }

        public Builder scale(float scale) {

            if (scale <= 0)
                throw new IllegalArgumentException("Scale must be superior to zero !");

            this.scale = scale;
            return this;
        }

        public Builder ditherThreshold(int dt) {

            this.ditherThreshold = dt;
            return this;
        }

        public ImgConverter build() {

            return new ImgConverter(this);
        }
    }

    private class Pixel {

        AnsiColor fgColor;
        AnsiColor bgColor;
        char character;

        Pixel(AnsiColor fgColor, AnsiColor bgColor, char character) {

            this.fgColor = fgColor;
            this.bgColor = bgColor;
            this.character = character;
        }

        @Override
        public String toString() {

            return (fgColor != null ? fgColor.fg() : "") + (bgColor != null ? bgColor.bg() : "") + character;
        }

        public boolean colorsEquals(Object obj) {

            if (obj instanceof Pixel) {
                Pixel other = (Pixel) obj;
                boolean result = true;
                if (fgColor != null)
                    result &= fgColor.equals(other.fgColor);
                if (bgColor != null)
                    result &= bgColor.equals(other.bgColor);
                return result;
            }
            return false;
        }

        public Pixel getClear() {

            return new Pixel(null, null, character);
        }
    }
}
