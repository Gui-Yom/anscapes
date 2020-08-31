package tech.guiyom.anscapes.renderer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(ints = { 0, 2, 4, 8, 16, 32 })
    public void testRgbColors(final int bias) throws IOException {

        ImageRenderer converter = new RgbImageRenderer(64, 64, bias);

        FileOutputStream out = new FileOutputStream("temp/shield_rgb_" + bias + ".txt");
        String result = converter.renderString(ImageIO.read(getClass().getResourceAsStream("shield.png")));
        out.write(result.getBytes(StandardCharsets.UTF_8));
        out.close();
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 2, 4, 8, 16, 32 })
    public void testRgbVideo(final int bias) throws IOException {

        ImageRenderer converter = new RgbImageRenderer(360, 360, bias);

        BufferedImage img = ImageIO.read(getClass().getResourceAsStream("shield.png"));
        int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());

        // Warmup (200 rounds)
        for (int i = 0; i < 200; ++i)
            converter.render(data, img.getWidth(), img.getHeight(), (buf, len) -> buf[0] = 'a');

        // Bench
        int numRun = 600;
        long startTime = System.nanoTime();
        for (int i = 0; i < numRun; ++i)
            converter.render(data, img.getWidth(), img.getHeight(), (buf, len) -> buf[0] = 'a');
        long totalTime = System.nanoTime() - startTime;

        System.out.printf("RGB (bias=%d) | Total (ms) : %d | Per frame (ms) : %f%n",
                bias,
                totalTime / 1_000_000,
                (double) totalTime / (numRun * 1_000_000));
    }
}
