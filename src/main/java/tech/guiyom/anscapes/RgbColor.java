package tech.guiyom.anscapes;

import java.awt.Color;

public class RgbColor implements AnsiColor {

    private final int r;
    private final int g;
    private final int b;

    public RgbColor(Color c) {
        this(c.getRed(), c.getGreen(), c.getBlue());
    }

    public RgbColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Uses a color parsing method similar to {@link Color#Color(int)}
     *
     * @param rgb
     */
    public RgbColor(int rgb) {
        this((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
    }

    /**
     * Create a new AWT Color object each time.
     *
     * @return the corresponding AWT Color
     */
    @Override
    public Color color() {
        return new Color(r, g, b);
    }

    @Override
    public int r() {
        return r;
    }

    @Override
    public int g() {
        return g;
    }

    @Override
    public int b() {
        return b;
    }

    @Override
    public String fg() {
        return Anscapes.CSI + "38;2;" + r() + ';' + g() + ';' + b() + 'm';
    }

    @Override
    public String bg() {
        return Anscapes.CSI + "48;2;" + r() + ';' + g() + ';' + b() + 'm';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RgbColor) {
            RgbColor other = (RgbColor) obj;
            return r == other.r && g == other.g && b == other.b;
        } else if (obj instanceof AnsiColor) {
            AnsiColor other = (AnsiColor) obj;
            return r == other.r() && g == other.g() && b == other.b();
        }
        return false;
    }
}
