package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.ColorMode;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

public interface ImageRenderer {

    static ImageRenderer createRenderer(ColorMode cmode, int targetWidth, int targetHeight) {
        if (cmode == ColorMode.ANSI) {
            return new AnsiImageRenderer(targetWidth, targetHeight);
        } else if (cmode == ColorMode.RGB) {
            return new RgbImageRenderer(targetWidth, targetHeight);
        } else {
            throw new IllegalArgumentException("Unsupported color mode (cmode)");
        }
    }

    ColorMode getColorMode();

    void render(int[] data, int originalWidth, int originalHeight, BiConsumer<char[], Integer> resultConsumer);

    void render(ByteBuffer buf, int originalWidth, int originalHeight, BiConsumer<char[], Integer> resultConsumer);

    String renderString(int[] data, int originalWidth, int originalHeight);

    String renderString(BufferedImage image);

    TerminalImage render(int[] data, int originalWidth, int originalHeight);

    TerminalImage render(BufferedImage image);
}
