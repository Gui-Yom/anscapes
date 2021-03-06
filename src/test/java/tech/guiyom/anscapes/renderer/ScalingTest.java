package tech.guiyom.anscapes.renderer;

import org.junit.jupiter.api.Test;
import tech.guiyom.anscapes.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScalingTest {

    @Test
    public void testRgbScaling() throws IOException {
        BufferedImage img = Utils.getSampleImage();
        int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        int[] out = new int[64 * 64];
        AbstractImageRenderer.resize(data, img.getWidth(), img.getHeight(), out, 64, 64);
        BufferedImage img2 = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        img2.setRGB(0, 0, 64, 64, out, 0, 64);
        ImageIO.write(img2, "png", new File("temp/scaling.png"));
    }
}
