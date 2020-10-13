package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.Anscapes;
import tech.guiyom.anscapes.AnsiColor;
import tech.guiyom.anscapes.ColorMode;

import java.util.function.BiConsumer;

public class RgbImageRenderer extends AbstractImageRenderer {

    private final int bias;

    /**
     * Create a new ImageRenderer that render images with 24bit colors.
     * Default to a bias of 0.
     *
     * @param targetWidth
     * @param targetHeight
     */
    public RgbImageRenderer(int targetWidth, int targetHeight) {
        this(targetWidth, targetHeight, 0);
    }

    /**
     * Create a new ImageRenderer that render images with 24bit colors.
     *
     * @param targetWidth
     * @param targetHeight
     * @param bias
     */
    public RgbImageRenderer(int targetWidth, int targetHeight, int bias) {
        super(ColorMode.RGB, targetWidth, targetHeight);
        this.bias = bias;
    }

    // TODO control background color when dealing with transparent images

    @Override
    public void render(int[] data, int originalWidth, int originalHeight, BiConsumer<char[], Integer> resultConsumer) {

        // Resize if needed
        if (originalWidth != targetWidth || originalHeight != targetHeight) {
            resize(data, originalWidth, originalHeight);
            data = resizeBuffer;
        }

        this.outputBuffer.reset();
        AnsiColor prevUpper = null;
        AnsiColor prevLower = null;

        for (int i = 0; i < targetHeight / 2; ++i) {

            int y = i * 2;
            for (int x = 0; x < targetWidth; ++x) {
                AnsiColor upper = Anscapes.rgb(data[y * targetHeight + x]);
                AnsiColor lower = null;
                if (y + 1 < targetHeight) {
                    lower = Anscapes.rgb(data[y * targetHeight + targetHeight + x]);
                } else {
                    lower = Anscapes.Colors.BLACK;
                }

                if (bias == 0) {
                    if (!upper.equals(prevUpper))
                        outputBuffer.put(upper.fg());
                    if (!lower.equals(prevLower))
                        outputBuffer.put(lower.bg());
                } else {
                    if (upper.diffBiased(prevUpper, bias))
                        outputBuffer.put(upper.fg());
                    if (lower.diffBiased(prevLower, bias))
                        outputBuffer.put(lower.bg());
                }

                outputBuffer.put(CHAR_TOP);

                prevUpper = upper;
                prevLower = lower;
            }

            outputBuffer.put(Anscapes.RESET);
            outputBuffer.put(System.lineSeparator());

            prevUpper = null;
            prevLower = null;
        }

        resultConsumer.accept(outputBuffer.array(), outputBuffer.position());
    }
}
