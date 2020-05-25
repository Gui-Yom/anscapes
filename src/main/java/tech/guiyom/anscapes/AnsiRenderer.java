package tech.guiyom.anscapes;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.function.BiConsumer;

import static tech.guiyom.anscapes.Anscapes.Colors;

/**
 * This class permits conversion from image to ansi sequences, effectively allowing to draw images on the terminal.
 * <p>
 * You should use one instance per image / image sequence.
 */
public class AnsiRenderer {

    /**
     * Chars used to output image. They will then hold colors.
     */
    private static final char CHAR_FULL = '\u2588',
            CHAR_TOP = '\u2580',
            CHAR_BOTTOM = '\u2584',
            CHAR_EMPTY = ' ';

    private static final int ditherThreshold = 5;
    // Target size
    private final int targetWidth;
    private final int targetHeight;
    private final CharBuffer buffer;
    // Color mode
    private ColorMode colorMode;

    /**
     * @param cm           the color mode used for conversion
     * @param targetWidth  the target width for image rescaling
     * @param targetHeight the target height for image rescaling
     */
    public AnsiRenderer(ColorMode cm, int targetWidth, int targetHeight) {
        this.colorMode = cm;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        // Scaling the buffer for the worst case to prevent further array copies.
        this.buffer = CharBuffer.allocate(39 * targetHeight * targetWidth + targetHeight * 5);
        this.buffer.mark();
    }

    private static int[] bytesToARGB(ByteBuffer buf, int width, int height) {
        int[] data = new int[width * height];
        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y) {
                int index = y * width + x * 4;
                int b = buf.get(index) & 0xff;
                int g = buf.get(index + 1) & 0xff;
                int r = buf.get(index + 2) & 0xff;
                int a = buf.get(index + 3) & 0xff;
                if (a > 0x00 && a < 0xff) {
                    int halfa = a >> 1;
                    r = (r >= a) ? 0xff : (r * 0xff + halfa) / a;
                    g = (g >= a) ? 0xff : (g * 0xff + halfa) / a;
                    b = (b >= a) ? 0xff : (b * 0xff + halfa) / a;
                }
                data[y * width + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        return data;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public ColorMode getColorMode() {
        return colorMode;
    }

    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
    }

    /**
     * Resize pixels to the target dimensions
     *
     * @param pixels         the pixel data
     * @param originalWidth  the original pixel array width
     * @param originalHeight the original pixel array height
     * @return the resized pixel data
     */
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

    /**
     * Convert an image to a sequence of ansi codes.
     *
     * @param image the image to convert
     * @return a sequence of ansi codes in the form of a String
     */
    public TerminalImage render(BufferedImage image) {

        return new TerminalImage(renderToString(image), targetWidth, targetHeight, colorMode);
    }

    /**
     * Convert a pixel array to a TerminalImage
     *
     * @param data           the pixel array
     * @param originalWidth  the pixel array width
     * @param originalHeight the pixel array height
     * @return the created TerminalImage instance
     */
    public TerminalImage render(int[] data, int originalWidth, int originalHeight) {
        return new TerminalImage(renderToString(data, originalWidth, originalHeight), targetWidth, targetHeight, colorMode);
    }

    /**
     * Convert a BufferedImage to an ansi sequence
     *
     * @param image the image to convert
     * @return the ansi sequence
     */
    public String renderToString(BufferedImage image) {
        int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        return renderToString(data, image.getWidth(), image.getHeight());
    }

    /**
     * Convert a pixel array to an ansi sequence
     *
     * @param data           the pixel array
     * @param originalWidth  the pixel array width
     * @param originalHeight the pixel array height
     * @return the ansi sequence
     */
    public String renderToString(int[] data, int originalWidth, int originalHeight) {
        String[] result = new String[1];
        renderDirect(data, originalWidth, originalHeight, (buf, len) -> result[0] = new String(buf, 0, len));
        return result[0];
    }

    public void renderDirect(int[] data, int originalWidth, int originalHeight, BiConsumer<char[], Integer> renderConsumer) {

        if (originalWidth != targetWidth || originalHeight != targetHeight)
            data = resize(data, originalWidth, originalHeight);

        this.buffer.reset();
        int i = 0;
        Cell lastCell = null;

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

            // TODO add more reset options (after each line or after each char)

            if (colorMode == ColorMode.ANSI) {

                Colors topAnsiColor = Anscapes.findNearestColor(topPixel, ditherThreshold);
                Colors topBgAnsiColor = null;
                Colors bottomAnsiColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);
                Colors bottomBgAnsiColor = null;

                // If top pixel is the same as bottom pixel
                if (topAnsiColor == bottomAnsiColor) {

                    if (topAnsiColor == Colors.BLACK)
                        currentCell = new Cell(null, Colors.BLACK, CHAR_EMPTY);
                    else
                        currentCell = new Cell(topAnsiColor, null, CHAR_FULL);

                } else {

                    bottomBgAnsiColor = Anscapes.findNearestColor(bottomPixel, ditherThreshold);

                    if (topAnsiColor == Colors.BLACK || bottomBgAnsiColor == Colors.WHITE_BRIGHT) {
                        currentCell = new Cell(bottomAnsiColor, topAnsiColor, CHAR_BOTTOM);
                    } else
                        currentCell = new Cell(topAnsiColor, bottomBgAnsiColor, CHAR_TOP);
                }
            } else if (colorMode == ColorMode.RGB) {

                AnsiColor topColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                AnsiColor topBgColor = null;
                AnsiColor bottomColor = null;
                AnsiColor bottomBgColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());

                if (topPixel.equals(bottomPixel)) {

                    if (topPixel.equals(Colors.BLACK.color()))
                        currentCell = new Cell(null, Colors.BLACK, CHAR_EMPTY);
                    else
                        currentCell = new Cell(topColor, null, CHAR_FULL);

                } else {

                    if (topPixel.equals(Colors.BLACK.color()) || bottomPixel.equals(Colors.WHITE_BRIGHT.color())) {
                        bottomColor = Anscapes.rgb(bottomPixel.getRed(), bottomPixel.getGreen(), bottomPixel.getBlue());
                        topBgColor = Anscapes.rgb(topPixel.getRed(), topPixel.getGreen(), topPixel.getBlue());
                        currentCell = new Cell(bottomColor, topBgColor, CHAR_BOTTOM);
                    } else
                        currentCell = new Cell(topColor, bottomBgColor, CHAR_TOP);
                }
            }

            // Save space if last code equals current code
            // TODO use a treshold to save even more space
            if (currentCell.colorsEquals(lastCell))
                currentCell.getClear().writeTo();
            else
                currentCell.writeTo();

            lastCell = currentCell;

            ++i;
            if (i % targetWidth == 0) {
                buffer.put(Anscapes.RESET);
                buffer.put(System.lineSeparator());
                i += targetWidth;
                lastCell = null;
            }
        }
        renderConsumer.accept(buffer.array(), buffer.position());
    }

    public void renderDirect(ByteBuffer buf, int originalWidth, int originalHeight, BiConsumer<char[], Integer> renderConsumer) {
        renderDirect(bytesToARGB(buf, originalWidth, originalHeight), originalWidth, originalHeight, renderConsumer);
    }

    /**
     * Represent a cell in the terminal grid with its character, foreground color and background color
     */
    private class Cell {

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

        public void writeTo() {
            char[] temp = null;
            if (fgColor != null) {
                buffer.put(fgColor.fg());
            }
            if (bgColor != null) {
                buffer.put(bgColor.bg());
            }
            buffer.put(character);
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
