package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.Anscapes;
import tech.guiyom.anscapes.AnsiColor;
import tech.guiyom.anscapes.ColorMode;

import java.awt.Color;
import java.util.function.BiConsumer;

/**
 * Allow conversion of image to an ansi escape sequence of 16 basic colors.
 * <p>
 * You should use one instance per image / image sequence.
 */
public class AnsiImageRenderer extends AbstractImageRenderer {

    private final int threshold;

    /**
     * @param targetWidth  the target width for image rescaling
     * @param targetHeight the target height for image rescaling
     */
    public AnsiImageRenderer(int targetWidth, int targetHeight) {
        this(targetWidth, targetHeight, 8);
    }

    /**
     * @param targetWidth  the target width for image rescaling
     * @param targetHeight the target height for image rescaling
     */
    public AnsiImageRenderer(int targetWidth, int targetHeight, int threshold) {
        super(ColorMode.ANSI, targetWidth, targetHeight);
        this.threshold = threshold;
    }

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
                AnsiColor upper = Anscapes.findNearestColor(new Color(data[y * targetHeight + x], true), threshold);
                AnsiColor lower = null;
                if (y + 1 < targetHeight) {
                    lower = Anscapes.findNearestColor(new Color(data[y * targetHeight + targetHeight + x], true), threshold);
                } else {
                    lower = Anscapes.Colors.BLACK;
                }

                if (!upper.equals(prevUpper))
                    outputBuffer.put(upper.fg());
                if (!lower.equals(prevLower))
                    outputBuffer.put(lower.bg());

                outputBuffer.put(CHAR_TOP);

                prevUpper = upper;
                prevLower = lower;
            }

            outputBuffer.put(Anscapes.RESET);
            outputBuffer.put(System.lineSeparator());
        }

        resultConsumer.accept(outputBuffer.array(), outputBuffer.position());
    }
}
