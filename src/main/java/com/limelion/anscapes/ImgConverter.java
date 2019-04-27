package com.limelion.anscapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import static com.limelion.anscapes.AnsiColors.ColorBG;
import static com.limelion.anscapes.AnsiColors.ColorFG;

/**
 * A copy of <a href="https://github.com/fenwick67/term-px">https://github.com/fenwick67/term-px</a> translated from JS
 * to Java. Allow conversion from image to ansi codes sequences.
 */
public class ImgConverter {

    private static final char FULL_CHAR = '\u2588',
        TOP_CHAR = '\u2580',
        BOTTOM_CHAR = '\u2584';

    // Smooth the image
    private boolean smoothing;
    // Color mode
    private Mode mode;
    // Resize at 1/scale
    private int reductionScale;

    /**
     * Use builder instead
     *
     * @param builder
     */
    private ImgConverter(Builder builder) {

        this.mode = builder.mode;
        this.reductionScale = builder.reductionScale;
        this.smoothing = builder.smoothing;
    }

    public static Builder builder() {

        return new Builder();
    }

    /**
     * Convert an image to a sequence of ansi codes.
     *
     * @param image
     *     the image to convert
     *
     * @return a sequence of ansi codes in the form of a String
     */
    public String convert(BufferedImage image) {

        // Resize image
        int width = Math.floorDiv(image.getWidth(), reductionScale);
        int height = Math.floorDiv(image.getHeight(), reductionScale);

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

        // Here we align the bytes
        DataBuffer imgDataBuf = image.getData().getDataBuffer();

        int[] data = new int[imgDataBuf.getSize()];

        /*
        System.out.println("Num elems : " + data.length);
        System.out.println("Num pixels : " + width * height);
        System.out.println("Expected elems : " + width * height * 3);
         */

        for (int i = 0; i < data.length; ++i) {
            data[i] = imgDataBuf.getElem(i);
        }

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

            if (mode == Mode.ANSI_COLORS) {

                ColorFG topColor = findFgColor(topPixel);
                ColorBG topBgColor = null;
                ColorFG bottomColor = null;
                ColorBG bottomBgColor = findBgColor(bottomPixel);

                if (topColor == findFgColor(bottomPixel)) {

                    if (topColor == ColorFG.FG_BLACK)
                        currChar = ColorBG.BG_BLACK.code() + ' ';
                    else
                        currChar = topColor.code() + FULL_CHAR;

                } else {

                    if (topColor == ColorFG.FG_BLACK || bottomBgColor == ColorBG.BG_WHITE_BRIGHT) {
                        bottomColor = findFgColor(bottomPixel);
                        topBgColor = findBgColor(topPixel);
                        currChar = bottomColor.chain(topBgColor) + BOTTOM_CHAR;
                    } else
                        currChar = topColor.chain(bottomBgColor) + TOP_CHAR;

                }

            } else if (mode == Mode.RGB) {

                String topColor = AnsiColors.rgbFG(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                String topBgColor = null;
                String bottomColor = null;
                String bottomBgColor = AnsiColors.rgbBG(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());

                if (topPixel.equals(bottomPixel)) {

                    if (topPixel.equals(ColorFG.FG_BLACK.color()))
                        currChar = ColorFG.FG_BLACK.code() + ' ';
                    else
                        currChar = topColor + FULL_CHAR;

                } else {

                    if (topPixel.equals(ColorFG.FG_BLACK.color()) || bottomPixel.equals(ColorBG.BG_WHITE_BRIGHT.code())) {
                        bottomColor = AnsiColors.rgbFG(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());
                        topBgColor = AnsiColors.rgbBG(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                        currChar = bottomColor + topBgColor + BOTTOM_CHAR;
                    } else
                        currChar = topColor + bottomBgColor + TOP_CHAR;

                }
            }

            // Save space if last code equals current code
            if (currChar.equals(lastChar)) {
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

    private ColorBG findBgColor(Color bottom) {

        ColorBG closest = null;
        float closestDist = Float.MAX_VALUE;

        for (ColorBG color : ColorBG.values()) {
            float dist = rgbDistance(color.color(), bottom);
            if (dist < closestDist) {
                closestDist = dist;
                closest = color;
            }
        }
        return closest;
    }

    /**
     * @param top
     *
     * @return the nearest ANSI color
     */
    private ColorFG findFgColor(Color top) {

        ColorFG closest = null;
        float closestDist = Float.MAX_VALUE;

        for (ColorFG color : ColorFG.values()) {
            float dist = rgbDistance(color.color(), top);
            if (dist < closestDist) {
                closestDist = dist;
                closest = color;
            }
        }
        return closest;
    }

    /**
     * @param rgb1
     * @param rgb2
     *
     * @return an evaluation of the distance between two color
     */
    private float rgbDistance(Color rgb1, Color rgb2) {

        return (float) Math.sqrt(Math.pow(rgb1.getRed() - rgb2.getRed(), 2) +
                                 Math.pow(rgb1.getGreen() - rgb2.getGreen(), 2) +
                                 Math.pow(rgb1.getBlue() - rgb2.getBlue(), 2));
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

    public enum Mode {

        RGB,
        ANSI_COLORS
    }

    public static class Builder {

        private boolean smoothing = true;
        private Mode mode = Mode.ANSI_COLORS;
        private int reductionScale = 4;

        public Builder smoothing(boolean smoothing) {

            this.smoothing = smoothing;
            return this;
        }

        public Builder mode(ImgConverter.Mode mode) {

            this.mode = mode;
            return this;
        }

        public Builder reductionScale(int reductionScale) {

            if (reductionScale <= 0)
                throw new IllegalArgumentException("Scale must be superior to zero !");

            this.reductionScale = reductionScale;
            return this;
        }

        public ImgConverter build() {

            return new ImgConverter(this);
        }
    }
}
