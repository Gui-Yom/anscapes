package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.ColorMode;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.function.BiConsumer;

public abstract class AbstractImageRenderer implements ImageRenderer {

    /**
     * Chars used to output image. They will then hold colors.
     */
    protected static final char CHAR_FULL = '\u2588',
            CHAR_TOP = '\u2580',
            CHAR_BOTTOM = '\u2584',
            CHAR_BLANK = ' ';

    // Target size
    protected final int targetWidth;
    protected final int targetHeight;
    protected final CharBuffer buffer;
    // Color mode
    protected ColorMode colorMode;

    protected AbstractImageRenderer(ColorMode cmode, int targetWidth, int targetHeight) {
        this.colorMode = cmode;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;

        // Scaling the buffer for the worst case to prevent further array copies.
        this.buffer = CharBuffer.allocate(39 * targetHeight * targetWidth + targetHeight * 5);
        this.buffer.mark();
    }

    protected static int[] bytesToARGB(ByteBuffer buf, int width, int height) {
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

    static int[] resize(int[] pixels, int originalWidth, int originalHeight, int targetWidth, int targetHeight) {
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

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }

    public ColorMode getColorMode() {
        return colorMode;
    }

    /**
     * Resize pixels to the target dimensions
     *
     * @param pixels         the pixel data
     * @param originalWidth  the original pixel array width
     * @param originalHeight the original pixel array height
     * @return the resized pixel data
     */
    protected int[] resize(int[] pixels, int originalWidth, int originalHeight) {
        return resize(pixels, originalWidth, originalHeight, targetWidth, targetHeight);
    }

    public void render(ByteBuffer buf, int originalWidth, int originalHeight, BiConsumer<char[], Integer> renderConsumer) {
        render(bytesToARGB(buf, originalWidth, originalHeight), originalWidth, originalHeight, renderConsumer);
    }

    /**
     * Convert an image to a sequence of ansi codes.
     *
     * @param image the image to convert
     * @return a sequence of ansi codes in the form of a String
     */
    public TerminalImage render(BufferedImage image) {
        return new TerminalImage(renderString(image), targetWidth, targetHeight, colorMode);
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
        return new TerminalImage(renderString(data, originalWidth, originalHeight), targetWidth, targetHeight, colorMode);
    }

    /**
     * Convert a BufferedImage to an ansi sequence
     *
     * @param image the image to convert
     * @return the ansi sequence
     */
    public String renderString(BufferedImage image) {
        int[] data = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        return renderString(data, image.getWidth(), image.getHeight());
    }

    /**
     * Convert a pixel array to an ansi sequence
     *
     * @param data           the pixel array
     * @param originalWidth  the pixel array width
     * @param originalHeight the pixel array height
     * @return the ansi sequence
     */
    public String renderString(int[] data, int originalWidth, int originalHeight) {
        String[] result = new String[1];
        render(data, originalWidth, originalHeight, (buf, len) -> result[0] = new String(buf, 0, len));
        return result[0];
    }
}
