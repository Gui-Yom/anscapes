package com.limelion.anscapes;

public class TerminalImage {

    private String sequence;
    private int width;
    private int height;
    private ColorMode colorMode;

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
