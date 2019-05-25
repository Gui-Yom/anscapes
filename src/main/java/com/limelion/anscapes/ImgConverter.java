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
        BOTTOM_CHAR = '\u2584',
        EMPTY_CHAR = ' ';

    private int ditherThreshold;
    // Smooth the image
    private boolean smoothing;
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
        int width = (int) (image.getWidth() * scale);
        int height = (int) (image.getHeight() * scale);

        Image resized = image.getScaledInstance(width, height, smoothing ? Image.SCALE_SMOOTH : Image.SCALE_DEFAULT);
        //System.out.println("Num pixels : " + (image.getWidth() * image.getHeight()));

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
        Pixel lastRPixel = null;
        StringBuilder converted = new StringBuilder();

        while (i < data.length) {

            Color topPixel = new Color(data[i], data[i + 1], data[i + 2]);
            Color bottomPixel = null;

            // Check if another line exists, else assume black
            if (i + width * 3 + 2 < data.length) {
                bottomPixel = new Color(data[i + width * 3],
                                        data[i + width * 3 + 1],
                                        data[i + width * 3 + 2]);
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

            i += 3;
            if ((i / 3) % width == 0) {
                converted.append(Anscapes.RESET).append(System.lineSeparator());
                i += width * 3;
                lastRPixel = null;
            }
        }

        return new TextImage(converted.toString(), width, height, colorMode);
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
        private float scale = 0.25f;
        private int ditherThreshold = 5;

        public Builder smoothing(boolean smoothing) {

            this.smoothing = smoothing;
            return this;
        }

        public Builder mode(ColorMode colorMode) {

            this.colorMode = colorMode;
            return this;
        }

        public Builder scale(float scale) {

            System.out.println(scale);

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
