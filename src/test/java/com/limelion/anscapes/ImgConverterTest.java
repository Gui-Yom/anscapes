package com.limelion.anscapes;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ImgConverterTest {

    @Test
    public void testAnsiColors() {

        ImgConverter converter = ImgConverter.builder()
                                             .mode(ImgConverter.Mode.ANSI_COLORS)
                                             .reductionScale(6)
                                             .smoothing(true)
                                             .build();

        try {
            FileOutputStream out = new FileOutputStream("shield_ansi.txt");
            out.write(converter.convert(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRgbColors() {

        ImgConverter converter = ImgConverter.builder()
                                             .mode(ImgConverter.Mode.RGB)
                                             .reductionScale(6)
                                             .smoothing(true)
                                             .build();

        try {
            FileOutputStream out = new FileOutputStream("shield_rgb.txt");
            out.write(converter.convert(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
