package com.limelion.anscapes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ImgConverterTest {

    @BeforeAll
    public static void setup() {
        new File("temp").mkdir();
    }

    @Test
    public void testAnsiColors() {

        ImgConverter converter = ImgConverter.builder()
                                             .mode(ColorMode.ANSI)
                                             .scale(1.0f / 6)
                                             .scaling(ImgConverter.Scaling.SMOOTH)
                                             .build();

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_ansi.txt");
            out.write(converter.convert(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getImage().getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRgbColors() {

        ImgConverter converter = ImgConverter.builder()
                                             .mode(ColorMode.RGB)
                                             .scale(1.0f / 6)
                                             .scaling(ImgConverter.Scaling.SMOOTH)
                                             .build();

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_rgb.txt");
            out.write(converter.convert(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getImage().getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThreshold() {

        ImgConverter converter = ImgConverter.builder()
                                             .mode(ColorMode.ANSI)
                                             .scale(1.0f / 6)
                                             .scaling(ImgConverter.Scaling.SMOOTH)
                                             .ditherThreshold(-1)
                                             .build();

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_nothreshold.txt");
            out.write(converter.convert(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getImage().getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImgConverter converter2 = ImgConverter.builder()
                                              .mode(ColorMode.ANSI)
                                              .scale(1.0f / 6)
                                              .scaling(ImgConverter.Scaling.SMOOTH)
                                              .ditherThreshold(20)
                                              .build();

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_" + converter2.ditherThreshold() + "threshold.txt");
            out.write(converter.convert(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getImage().getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void benchmark() {

        ImgConverter converter = ImgConverter.builder()
                                             .mode(ColorMode.RGB)
                                             .scale(1.0f / 2)
                                             .scaling(ImgConverter.Scaling.FAST)
                                             .build();

        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("shield.png"));

            // Warmup
            for (int i = 0; i < 120; ++i)
                converter.convert(img);

            int numRun = 120;
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < numRun; ++i)
                converter.convert(img);

            long totalTime = System.currentTimeMillis() - startTime;
            System.out.printf("Conversion time :%n - Total : %d ms%n - Per frame : %f ms%n", totalTime, (double) totalTime / numRun);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
