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

        ImgConverter converter = new ImgConverter(ColorMode.ANSI, 64, 64);

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_ansi.txt");
            out.write(converter.convertToSequence(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRgbColors() {

        ImgConverter converter = new ImgConverter(ColorMode.RGB, 64, 64);

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_rgb.txt");
            out.write(converter.convertToSequence(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void benchmarkVideo() {

        ImgConverter converter = new ImgConverter(ColorMode.RGB, 96, 54);

        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("shield.png"));
            int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());

            // Warmup
            for (int i = 0; i < 200; ++i)
                converter.convertToSequence(data, img.getWidth(), img.getHeight());

            int numRun = 200;
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < numRun; ++i)
                converter.convertToSequence(data, img.getWidth(), img.getHeight());

            long totalTime = System.currentTimeMillis() - startTime;
            // Goal is much less than 40ms for 25fps
            System.out.printf("Conversion time :%n - Total : %d ms%n - Per frame : %f ms%n", totalTime, (double) totalTime / numRun);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScaling() throws IOException {

        BufferedImage img = ImageIO.read(getClass().getResourceAsStream("shield.png"));
        ImgConverter converter = new ImgConverter(ColorMode.RGB, 96, 54);
        int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        int[] out = converter.resize(data, img.getWidth(), img.getHeight());
        BufferedImage img2 = new BufferedImage(96, 54, BufferedImage.TYPE_INT_ARGB);
        img2.setRGB(0, 0, 96, 54, out, 0, 96);
        ImageIO.write(img2, "png", new File("temp/scaling.png"));
    }
}
