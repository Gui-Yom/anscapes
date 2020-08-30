package tech.guiyom.anscapes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.guiyom.anscapes.renderer.ImageRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RgbImageRendererTest {
    @BeforeAll
    public static void setup() {
        new File("temp").mkdir();
    }

    @Test
    public void testRgbColors() throws IOException {

        ImageRenderer converter = ImageRenderer.createRenderer(ColorMode.RGB, 64, 64);

        FileOutputStream out = new FileOutputStream("temp/shield_rgb.txt");
        String result = converter.renderString(ImageIO.read(getClass().getResourceAsStream("shield.png")));
        out.write(result.getBytes(StandardCharsets.UTF_8));
        out.close();
    }

    @Test
    public void testRgbVideo() throws IOException {

        ImageRenderer converter = ImageRenderer.createRenderer(ColorMode.RGB, 384, 216);

        BufferedImage img = ImageIO.read(getClass().getResourceAsStream("shield.png"));
        int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());

        // Warmup (200 rounds)
        for (int i = 0; i < 200; ++i)
            converter.render(data, img.getWidth(), img.getHeight(), (buf, len) -> buf[0] = 'a');

        int numRun = 400;
        long startTime = System.nanoTime();

        for (int i = 0; i < numRun; ++i)
            converter.render(data, img.getWidth(), img.getHeight(), (buf, len) -> buf[0] = 'a');

        long totalTime = (System.nanoTime() - startTime);
        System.out.printf("RGB Conversion time :%n - Total : %d ms%n - Per frame : %f ms%n - Per pixel : %f ns%n",
                totalTime / 1_000_000,
                (double) totalTime / (numRun * 1_000_000),
                (double) totalTime / (numRun * img.getHeight() * img.getWidth()));
    }
}
