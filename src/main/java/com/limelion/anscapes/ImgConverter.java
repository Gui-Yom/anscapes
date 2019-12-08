package com.limelion.anscapes;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static com.limelion.anscapes.Anscapes.Colors;

/**
 * This class permits conversion from image to ansi sequences, allowing to draw images on the terminal.
 */
public class ImgConverter {

    /**
     * Chars used to output image. They will then hold colors.
     */
    private static final char FULL_CHAR = '\u2588',
            TOP_CHAR = '\u2580',
            BOTTOM_CHAR = '\u2584',
            EMPTY_CHAR = ' ';

    private static final int ditherThreshold = 5;
    // Color mode
    private ColorMode colorMode;
    // Target size
    private int targetWidth;
    private int targetHeight;

    /**
     * @param cm           the color mode used for conversion
     * @param targetWidth  the target width for image rescaling
     * @param targetHeight the target height for image rescaling
     */
    public ImgConverter(ColorMode cm, int targetWidth, int targetHeight) {

        this.colorMode = cm;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    /**
     * Escape to allow copy paste. Useful for commands like 'echo -e'
     *
     * @param s the string to be escaped
     * @return the escaped code
     */
    public static String escape(String s) {

        return s.replaceAll("\n/g", "\\n").replaceAll("\r/g", "\\r").replaceAll("\033/g", "\\033");
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public int[] resize(int[] pixels, int originalWidth, int originalHeight) {

        int[] resized = new int[targetWidth * targetHeight];
        // EDIT: added +1 to account for an early rounding problem
        int x_ratio = ((originalWidth << 16) / targetWidth) + 1;
        int y_ratio = ((originalHeight << 16) / targetHeight) + 1;
        //int x_ratio = (int)((w1<<16)/w2) ;
        //int y_ratio = (int)((h1<<16)/h2) ;
        int x2, y2;
        for (int i = 0; i < targetHeight; i++) {
            for (int j = 0; j < targetWidth; j++) {
                x2 = ((j * x_ratio) >> 16);
                y2 = ((i * y_ratio) >> 16);
                resized[(i * targetWidth) + j] = pixels[(y2 * originalWidth) + x2];
            }
        }
        return resized;
    }

    public ColorMode getColorMode() {

        return colorMode;
    }

    /**
     * Convert an image to a sequence of ansi codes.
     *
     * @param image the image to convert
     * @return a sequence of ansi codes in the form of a String
     */
    public TerminalImage convert(BufferedImage image) {

        return new TerminalImage(convertToSequence(image), targetWidth, targetHeight, colorMode);
    }

    public String convertToSequence(BufferedImage image) {

        int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        return convertToSequence(data, image.getWidth(), image.getHeight());
    }

    public TerminalImage convert(int[] data, int originalWidth, int originalHeight) {

        return new TerminalImage(convertToSequence(data, originalWidth, originalHeight), targetWidth, targetHeight, colorMode);
    }

    public String convertToSequence(int[] data, int originalWidth, int originalHeight) {

        if (originalWidth != targetWidth || originalHeight != targetHeight)
            data = resize(data, originalWidth, originalHeight);

        int i = 0;
        Cell lastCell = null;
        StringBuilder converted = new StringBuilder();

        while (i < data.length) {

            Color topPixel = new Color(data[i]);
            Color bottomPixel = null;

            // Check if another line exists, else assume black
            if (i + targetWidth < data.length) {
                bottomPixel = new Color(data[i + targetWidth]);
            } else {
                bottomPixel = Color.BLACK;
            }

            // Code for current rendered pixel
            Cell currentCell = null;

            // TODO add more reset options (after each line of after each char)


            if (colorMode == ColorMode.ANSI) {

                Colors topAnsiColor = Anscapes.findNearestColor(topPixel, ditherThreshold);
                Colors topBgAnsiColor = null;
                Colors bottomAnsiColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);
                Colors bottomBgAnsiColor = null;

                // If top pixel is the same as bottom pixel
                if (topAnsiColor == bottomAnsiColor) {

                    if (topAnsiColor == Colors.BLACK)
                        currentCell = new Cell(null, Colors.BLACK, EMPTY_CHAR);
                    else
                        currentCell = new Cell(topAnsiColor, null, FULL_CHAR);

                } else {

                    bottomBgAnsiColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);

                    if (topAnsiColor == Colors.BLACK || bottomBgAnsiColor == Colors.WHITE_BRIGHT) {
                        currentCell = new Cell(bottomAnsiColor, topAnsiColor, BOTTOM_CHAR);
                    } else
                        currentCell = new Cell(topAnsiColor, bottomBgAnsiColor, TOP_CHAR);

                }
            } else if (colorMode == ColorMode.RGB) {

                // TODO add rgb compression (as a treshold)

                AnsiColor topColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                AnsiColor topBgColor = null;
                AnsiColor bottomColor = null;
                AnsiColor bottomBgColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());

                if (topPixel.equals(bottomPixel)) {

                    if (topPixel.equals(Colors.BLACK.color()))
                        currentCell = new Cell(null, Colors.BLACK, EMPTY_CHAR);
                    else
                        currentCell = new Cell(topColor, null, FULL_CHAR);

                } else {

                    if (topPixel.equals(Colors.BLACK.color()) || bottomPixel.equals(Colors.WHITE_BRIGHT.color())) {
                        bottomColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());
                        topBgColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                        currentCell = new Cell(bottomColor, topBgColor, BOTTOM_CHAR);
                    } else
                        currentCell = new Cell(topColor, bottomBgColor, TOP_CHAR);

                }
            }

            // Save space if last code equals current code
            if (currentCell.colorsEquals(lastCell))
                converted.append(currentCell.getClear());
            else
                converted.append(currentCell);

            lastCell = currentCell;

            ++i;
            if (i % targetWidth == 0) {
                converted.append(Anscapes.RESET).append(System.lineSeparator());
                i += targetWidth;
                lastCell = null;
            }
        }

        return converted.toString();
    }

    /**
     * Represent a cell in the terminal grid with its character, foreground color and background color
     */
    private static class Cell {

        AnsiColor fgColor;
        AnsiColor bgColor;
        char character;

        Cell(AnsiColor fgColor, AnsiColor bgColor, char character) {

            this.fgColor = fgColor;
            this.bgColor = bgColor;
            this.character = character;
        }

        @Override
        public String toString() {

            return (fgColor != null ? fgColor.fg() : "") + (bgColor != null ? bgColor.bg() : "") + character;
        }

        public boolean colorsEquals(Object obj) {

            if (obj instanceof Cell) {
                Cell other = (Cell) obj;
                boolean result = true;
                if (fgColor != null)
                    result &= fgColor.equals(other.fgColor);
                if (bgColor != null)
                    result &= bgColor.equals(other.bgColor);
                return result;
            }
            return false;
        }

        public Cell getClear() {

            return new Cell(null, null, character);
        }
    }
}
