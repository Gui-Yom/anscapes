package tech.guiyom.anscapes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AnsiRendererTest {

    @BeforeAll
    public static void setup() {
        new File("temp").mkdir();
    }

    @Test
    public void testAnsiColors() {

        AnsiRenderer converter = new AnsiRenderer(ColorMode.ANSI, 64, 64);

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_ansi.txt");
            String result = converter.renderToString(ImageIO.read(getClass().getResourceAsStream("shield.png")));
            System.out.write(result.getBytes(StandardCharsets.UTF_8));
            System.out.flush();
            out.write(result.getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRgbColors() {

        AnsiRenderer converter = new AnsiRenderer(ColorMode.RGB, 64, 64);

        try {
            FileOutputStream out = new FileOutputStream("temp/shield_rgb.txt");
            out.write(converter.renderToString(ImageIO.read(getClass().getResourceAsStream("shield.png"))).getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void benchmarkVideo() {

        AnsiRenderer converter = new AnsiRenderer(ColorMode.RGB, 384, 216);

        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream("shield.png"));
            int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());

            // Warmup (200 rounds)
            for (int i = 0; i < 200; ++i)
                converter.renderDirect(data, img.getWidth(), img.getHeight(), (buf, len) -> {
                    buf[0] = 'a';
                });

            int numRun = 400;
            long startTime = System.nanoTime();

            for (int i = 0; i < numRun; ++i)
                converter.renderDirect(data, img.getWidth(), img.getHeight(), (buf, len) -> {
                    buf[0] = 'a';
                });

            long totalTime = (System.nanoTime() - startTime);
            System.out.printf("Conversion time :%n - Total : %d ms%n - Per frame : %f ms%n - Per pixel : %f ns%n",
                    totalTime / 1_000_000,
                    (double) totalTime / (numRun * 1_000_000),
                    (double) totalTime / (numRun * img.getHeight() * img.getWidth()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScaling() throws IOException {

        BufferedImage img = ImageIO.read(getClass().getResourceAsStream("shield.png"));
        AnsiRenderer converter = new AnsiRenderer(ColorMode.RGB, 96, 54);
        int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        int[] out = converter.resize(data, img.getWidth(), img.getHeight());
        BufferedImage img2 = new BufferedImage(96, 54, BufferedImage.TYPE_INT_ARGB);
        img2.setRGB(0, 0, 96, 54, out, 0, 96);
        ImageIO.write(img2, "png", new File("temp/scaling.png"));
    }
}
