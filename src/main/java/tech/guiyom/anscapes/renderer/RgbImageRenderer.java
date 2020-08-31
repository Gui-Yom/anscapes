package tech.guiyom.anscapes.renderer;

import tech.guiyom.anscapes.Anscapes;
import tech.guiyom.anscapes.AnsiColor;
import tech.guiyom.anscapes.ColorMode;

import java.awt.Color;
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
                    if (colorsDiff2(upper, prevUpper))
                        buffer.put(upper.fg());
                    if (colorsDiff2(lower, prevLower))
                        buffer.put(lower.bg());
                }

                buffer.put(CHAR_TOP);

                prevUpper = upper;
                prevLower = lower;
            }

            buffer.put(Anscapes.RESET);
            buffer.put(System.lineSeparator());

            prevUpper = null;
            prevLower = null;
        }

        resultConsumer.accept(buffer.array(), buffer.position());
    }

    /**
     * Check if colors are different enough based on bias.
     *
     * @param c1 the first color
     * @param c2 the second color
     * @return if the colors are different enough
     */
    private boolean colorsDiff(AnsiColor c1, AnsiColor c2) {
        if (c2 == null)
            return true;
        return (c1.color().getRed() - c2.color().getRed() + c1.color().getGreen() - c2.color().getGreen() + c1.color().getBlue() - c2.color().getBlue()) / 3 > bias;
    }

    /**
     * Check if colors are different enough based on bias.
     *
     * @param c1 the first color
     * @param c2 the second color
     * @return if the colors are different enough
     */
    private boolean colorsDiff2(AnsiColor c1, AnsiColor c2) {
        if (c2 == null)
            return true;
        return Math.sqrt(Math.pow(c1.color().getRed() - c2.color().getRed(), 2) +
                                 Math.pow(c1.color().getGreen() - c2.color().getGreen(), 2) +
                                 Math.pow(c1.color().getBlue() - c2.color().getBlue(), 2)) > bias;
    }
}
