package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.ColorMode;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.BiConsumer;

public interface ImageRenderer {

    /**
     * Create a renderer for the desired {@link ColorMode} with default parameters.
     *
     * @param cmode        the desired {@link ColorMode}
     * @param targetWidth  the rendered image width
     * @param targetHeight the rendered image height
     * @return the image renderer
     */
    static ImageRenderer createRenderer(ColorMode cmode, int targetWidth, int targetHeight) {
        if (cmode == ColorMode.ANSI) {
            return new AnsiImageRenderer(targetWidth, targetHeight);
        } else if (cmode == ColorMode.RGB) {
            return new RgbImageRenderer(targetWidth, targetHeight);
        } else {
            throw new IllegalArgumentException("Unsupported color mode (cmode)");
        }
    }

    /**
     * @return the {@link ColorMode} used by the rendered images.
     */
    ColorMode getColorMode();

    void render(int[] data, int originalWidth, int originalHeight, BiConsumer<char[], Integer> resultConsumer);

    void render(ByteBuffer buf, int originalWidth, int originalHeight, BiConsumer<char[], Integer> resultConsumer);

    void render(IntBuffer buf, int originalWidth, int originalHeight, BiConsumer<char[], Integer> resultConsumer);

    String renderString(int[] data, int originalWidth, int originalHeight);

    String renderString(BufferedImage image);

    TerminalImage render(int[] data, int originalWidth, int originalHeight);

    TerminalImage render(BufferedImage image);
}
