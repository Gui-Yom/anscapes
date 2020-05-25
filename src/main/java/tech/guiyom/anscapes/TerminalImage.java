package tech.guiyom.anscapes;

public class TerminalImage {

    private final String sequence;
    private final int width;
    private final int height;
    private final ColorMode colorMode;

    public TerminalImage(String sequence, int width, int height, ColorMode colorMode) {
        this.sequence = sequence;
        this.width = width;
        this.height = height;
        this.colorMode = colorMode;
    }

    public String getSequence() {
        return sequence;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ColorMode getColorMode() {
        return colorMode;
    }
}
