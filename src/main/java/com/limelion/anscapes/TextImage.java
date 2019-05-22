package com.limelion.anscapes;

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

    public TextImage(ImgConverter converter, BufferedImage image) {

        this(converter.convert(image),
             Math.floorDiv(image.getWidth(), converter.reductionScale()),
             Math.floorDiv(image.getHeight(), converter.reductionScale()),
             converter.getColorMode());
    }

    public TextImage(BufferedImage image, ColorMode colorMode, int reductionScale) {

        this(ImgConverter.builder()
                         .mode(colorMode)
                         .reductionScale(reductionScale)
                         .smoothing(true)
                         .build(), image);
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
