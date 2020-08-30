package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.Anscapes;
import tech.guiyom.anscapes.AnsiColor;
import tech.guiyom.anscapes.ColorMode;

import java.awt.Color;
import java.util.function.BiConsumer;

public class RgbImageRenderer extends AbstractImageRenderer {

    private int bias = 16;

    protected RgbImageRenderer(int targetWidth, int targetHeight) {
        super(ColorMode.RGB, targetWidth, targetHeight);
    }

    protected RgbImageRenderer(int targetWidth, int targetHeight, int bias) {
        this(targetWidth, targetHeight);
        this.bias = bias;
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
                AnsiColor upper = Anscapes.rgb(new Color(data[y * targetHeight + x]));
                AnsiColor lower = null;
                if (y + 1 < targetHeight) {
                    lower = Anscapes.rgb(new Color(data[y * targetHeight + targetHeight + x]));
                } else {
                    lower = Anscapes.rgb(0, 0, 0);
                }

                if (bias == 0) {
                    if (!upper.equals(prevUpper))
                        buffer.put(upper.fg());
                    if (!lower.equals(prevLower))
                        buffer.put(lower.bg());
                } else {
                    if (!colorsEqualsBias(upper, prevUpper))
                        buffer.put(upper.fg());
                    if (!colorsEqualsBias(lower, prevLower))
                        buffer.put(lower.bg());
                }

                buffer.put(CHAR_TOP);

                prevUpper = upper;
                prevLower = lower;
            }

            buffer.put(Anscapes.RESET);
            buffer.put(System.lineSeparator());
        }

        resultConsumer.accept(buffer.array(), buffer.position());
    }

    private boolean colorsEqualsBias(AnsiColor c1, AnsiColor c2) {
        if (c2 == null)
            return false;
        return (c1.color().getRed() - c2.color().getRed() + c1.color().getGreen() - c2.color().getGreen() + c1.color().getBlue() - c2.color().getBlue()) / 3 <= bias;
    }
}
