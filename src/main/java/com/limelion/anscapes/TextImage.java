package com.limelion.anscapes;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class TextImage {

    private String image;
    private int width;
    private int height;
    private ColorMode colorMode;

    public TextImage(String image, int width, int height, ColorMode colorMode) {

        this.image = image;
        this.width = width;
        this.height = height;
        this.colorMode = colorMode;
    }

    public String getImage() {

        return image;
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
