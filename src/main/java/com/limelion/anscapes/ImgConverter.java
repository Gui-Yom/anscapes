package com.limelion.anscapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import static com.limelion.anscapes.Anscapes.Colors;

/**
 * A copy of <a href="https://github.com/fenwick67/term-px">https://github.com/fenwick67/term-px</a> translated from JS
 * to Java. Allow conversion from image to ansi codes sequences.
 */
public class ImgConverter {

    /**
     * Chars used to output image. They will hold colors.
     */
    private static final char FULL_CHAR = '\u2588',
        TOP_CHAR = '\u2580',
        BOTTOM_CHAR = '\u2584';

    private int ditherThreshold;
    // Smooth the image
    private boolean smoothing;
    // Color mode
    private ColorMode colorMode;
    // Resize at 1/scale
    private int reductionScale;

    /**
     * Use builder instead
     *
     * @param builder
     */
    private ImgConverter(Builder builder) {

        this.colorMode = builder.colorMode;
        this.reductionScale = builder.reductionScale;
        this.smoothing = builder.smoothing;
        this.ditherThreshold = builder.ditherThreshold;
    }

    public static Builder builder() {

        return new Builder();
    }

    public int ditherThreshold() {

        return ditherThreshold;
    }

    public boolean smoothing() {

        return smoothing;
    }

    public ColorMode getColorMode() {

        return colorMode;
    }

    public int reductionScale() {

        return reductionScale;
    }

    /**
     * Convert an image to a sequence of ansi codes.
     *
     * @param image the image to convert
     *
     * @return a sequence of ansi codes in the form of a String
     */
    public String convert(BufferedImage image) {

        // Resize image
        int width = Math.floorDiv(image.getWidth(), Math.abs(reductionScale));
        int height = Math.floorDiv(image.getHeight(), Math.abs(reductionScale));

        Image resized = image.getScaledInstance(width, height, smoothing ? Image.SCALE_SMOOTH : Image.SCALE_DEFAULT);
        //System.out.println("Num pixels : " + (image.getWidth() * image.getHeight()));

        // DO NOT FORGET ITS 'BGR' NOT 'RGB'
        image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = image.createGraphics();
        g.drawImage(resized, 0, 0, null);
        g.dispose();

        /*
        try {
            ImageIO.write(image, "png", new File("resized.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        // Get an array containing all pixels samples
        // of size 3 * w * h
        Raster raster = image.getRaster();
        int[] data = new int[raster.getDataBuffer().getSize()];
        raster.getPixels(0, 0, width, height, data);

        /*
        System.out.println("Num elems : " + data.length);
        System.out.println("Num pixels : " + width * height);
        System.out.println("Expected elems : " + width * height * 3);
         */

        int i = 0;
        String lastChar = null;
        StringBuilder converted = new StringBuilder();

        while (i < data.length) {

            Color topPixel = new Color(data[i + 2], data[i + 1], data[i]);
            Color bottomPixel = null;

            // Check if another line exists, else assume black
            if (i + width * 3 + 2 < data.length) {
                bottomPixel = new Color(data[i + width * 3 + 2],
                                        data[i + width * 3 + 1],
                                        data[i + width * 3]);
            } else {
                bottomPixel = Color.BLACK;
            }

            // Code for current pixel
            String currChar = null;

            // TODO add more simplifications
            // TODO add more reset options (after each line of after each char)

            if (colorMode == ColorMode.ANSI) {

                Colors topColor = Anscapes.findNearestColor(topPixel, ditherThreshold);
                Colors topBgColor = null;
                Colors bottomColor = null;
                Colors bottomBgColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);

                if (topColor == Anscapes.findNearestColor(bottomPixel, ditherThreshold)) {

                    if (topColor == Colors.BLACK)
                        currChar = Colors.BLACK.bg() + ' ';
                    else
                        currChar = topColor.fg() + FULL_CHAR;

                } else {

                    if (topColor == Colors.BLACK || bottomBgColor == Colors.WHITE_BRIGHT) {
                        bottomColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);
                        topBgColor = Anscapes.findNearestColor(topPixel, ditherThreshold);
                        currChar = bottomColor.fg() + topBgColor.bg() + BOTTOM_CHAR;
                    } else
                        currChar = topColor.fg() + bottomBgColor.bg() + TOP_CHAR;

                }

                // RGB getColorMode implementation is still fucked up a bit (well, less than the ansi one)
            } else if (colorMode == ColorMode.RGB) {

                AnsiColor topColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                AnsiColor topBgColor = null;
                AnsiColor bottomColor = null;
                AnsiColor bottomBgColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());

                if (topPixel.equals(bottomPixel)) {

                    if (topPixel.equals(Colors.BLACK.color()))
                        currChar = Colors.BLACK.bg() + ' ';
                    else
                        currChar = topColor.fg() + FULL_CHAR;

                } else {

                    if (topPixel.equals(Colors.BLACK.color()) || bottomPixel.equals(Colors.WHITE_BRIGHT.color())) {
                        bottomColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());
                        topBgColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                        currChar = bottomColor.fg() + topBgColor.bg() + BOTTOM_CHAR;
                    } else
                        currChar = topColor.fg() + bottomBgColor.bg() + TOP_CHAR;

                }
            } else {

                // What else ?
                // Cmon its not possible, enjoy Java 12
            }

            // Save space if last code equals current code
            if (currChar.equals(lastChar)) {
                // Ugly af
                converted.append(currChar.replaceAll("[\\033m;\\d\\\\\\[]", ""));
            } else {
                converted.append(currChar);
            }

            lastChar = currChar;

            i += 3;
            if ((i / 3) % width == 0) {
                converted.append(Anscapes.RESET).append(System.lineSeparator());
                i += width * 3;
                lastChar = null;
            }
        }

        return converted.toString();
    }

    /**
     * Escape to allow copy paste. Useful for commands like 'echo -e'
     *
     * @param s
     *
     * @return the escaped code
     */
    public String escape(String s) {

        return s.replaceAll("\n/g", "\\n").replaceAll("\r/g", "\\r").replaceAll("\033/g", "\\033");
    }

    /**
     * Utility class to create an ImgConverter
     */
    public static class Builder {

        private boolean smoothing = true;
        private ColorMode colorMode = ColorMode.ANSI;
        private int reductionScale = 4;
        private int ditherThreshold = 5;

        public Builder smoothing(boolean smoothing) {

            this.smoothing = smoothing;
            return this;
        }

        public Builder mode(ColorMode colorMode) {

            this.colorMode = colorMode;
            return this;
        }

        public Builder reductionScale(int reductionScale) {

            if (reductionScale <= 0)
                throw new IllegalArgumentException("Scale must be superior to zero !");

            this.reductionScale = reductionScale;
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
}
