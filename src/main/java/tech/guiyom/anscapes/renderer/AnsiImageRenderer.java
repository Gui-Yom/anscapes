package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.Anscapes;
import tech.guiyom.anscapes.AnsiColor;
import tech.guiyom.anscapes.ColorMode;

import java.awt.Color;
import java.util.function.BiConsumer;

/**
 * This class permits conversion from image to ansi sequences, effectively allowing to draw images on the terminal.
 * <p>
 * You should use one instance per image / image sequence.
 */
public class AnsiImageRenderer extends AbstractImageRenderer {

    private static final int ditherThreshold = 5;

    /**
     * @param targetWidth  the target width for image rescaling
     * @param targetHeight the target height for image rescaling
     */
    public AnsiImageRenderer(int targetWidth, int targetHeight) {
        super(ColorMode.ANSI, targetWidth, targetHeight);
    }

    @Override
    public void render(int[] data, int originalWidth, int originalHeight, BiConsumer<char[], Integer> resultConsumer) {

        // Resize if needed
        if (originalWidth != targetWidth || originalHeight != targetHeight)
            data = resize(data, originalWidth, originalHeight);

        this.buffer.reset();
        AnsiColor prevUpper = null;
        AnsiColor prevLower = null;

        for (int i = 0; i < targetHeight / 2; ++i) {

            int y = i * 2;
            for (int x = 0; x < targetWidth; ++x) {
                AnsiColor upper = Anscapes.findNearestColor(new Color(data[y * targetHeight + x]), ditherThreshold);
                AnsiColor lower = null;
                if (y + 1 < targetHeight) {
                    lower = Anscapes.findNearestColor(new Color(data[y * targetHeight + targetHeight + x]), ditherThreshold);
                } else {
                    lower = Anscapes.Colors.BLACK;
                }

                if (!upper.equals(prevUpper))
                    buffer.put(upper.fg());
                if (!lower.equals(prevLower))
                    buffer.put(lower.bg());

                buffer.put(CHAR_TOP);

                prevUpper = upper;
                prevLower = lower;
            }

            buffer.put(Anscapes.RESET);
            buffer.put(System.lineSeparator());
        }

        resultConsumer.accept(buffer.array(), buffer.position());
    }
}
